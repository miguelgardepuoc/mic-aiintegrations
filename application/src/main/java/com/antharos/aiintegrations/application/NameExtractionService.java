package com.antharos.aiintegrations.application;

import com.antharos.aiintegrations.domain.NameInfo;
import com.antharos.aiintegrations.domain.repository.FileTextExtractor;
import com.antharos.aiintegrations.domain.repository.ResourceReader;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NameExtractionService {
  private StanfordCoreNLP pipeline;
  private final Set<String> commonSpanishNames;
  private final Set<String> compoundIndicators;
  private final FileTextExtractor fileTextExtractor;
  private final ResourceReader resourceReader;

  // Default constructor for Spring
  public NameExtractionService() {
    this.compoundIndicators =
        new HashSet<>(
            Arrays.asList("de", "del", "la", "las", "los", "da", "di", "van", "von", "el"));
    this.commonSpanishNames = new HashSet<>();
    this.fileTextExtractor = null;
    this.resourceReader = null;
  }

  // Comprehensive constructor for manual creation and dependency injection
  @Autowired
  public NameExtractionService(FileTextExtractor fileTextExtractor, ResourceReader resourceReader)
      throws IOException {
    this.fileTextExtractor = fileTextExtractor;
    this.resourceReader = resourceReader;

    // Prepositions and articles for compound surnames
    this.compoundIndicators =
        new HashSet<>(
            Arrays.asList("de", "del", "la", "las", "los", "da", "di", "van", "von", "el"));

    // Initialize set of common Spanish names from resource file
    this.commonSpanishNames = resourceReader.readNamesFromResource("names/spanish-names.txt");

    // Configure Stanford NLP for named entity recognition
    initializePipeline();
  }

  // Separate method for pipeline initialization to allow for testing and flexibility
  private void initializePipeline() {
    try {
      Properties props = new Properties();
      props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
      props.setProperty("ner.useSUTime", "false");
      props.setProperty(
          "ner.model", "edu/stanford/nlp/models/ner/spanish.ancora.distsim.s512.crf.ser.gz");
      this.pipeline = new StanfordCoreNLP(props);
    } catch (Exception e) {
      // Log the error or handle it appropriately
      this.pipeline = null;
    }
  }

  // Method for testing or manual pipeline initialization
  public void initializeWithCustomPipeline(StanfordCoreNLP customPipeline) {
    this.pipeline = customPipeline;
  }

  // Modify the NLP extraction method to handle null pipeline
  protected List<String> extractPersonNamesWithNLP(String text) {
    List<String> names = new ArrayList<>();

    // If pipeline is not initialized, return empty list
    if (pipeline == null) {
      return names;
    }

    // Extract the first 500 characters to improve performance
    String firstPart = text.length() > 500 ? text.substring(0, 500) : text;

    CoreDocument document = new CoreDocument(firstPart);
    pipeline.annotate(document);

    for (CoreEntityMention em : document.entityMentions()) {
      if (em.entityType().equals("PERSONA") || em.entityType().equals("PERSON")) {
        names.add(em.text());
      }
    }

    return names;
  }

  public String extractTextFromFile(byte[] file) throws IOException {
    return this.fileTextExtractor.extractTextFromFile(file);
  }

  public NameInfo findNameInText(String text) {
    // Normalize line breaks for processing
    text = normalizeText(text);

    // Method 1: Use NER to identify people
    List<String> names = extractPersonNamesWithNLP(text);

    // Method 2: Look for common CV patterns with regular expressions
    if (names.isEmpty()) {
      names = extractNameWithRegex(text);
    }

    // If we find at least one name, process it appropriately
    if (!names.isEmpty()) {
      return splitNameAndSurnames(names.getFirst());
    }

    return new NameInfo("", "");
  }

  /**
   * Converts each word to have only the first letter capitalized and the rest in lowercase,
   * respecting prepositions and articles.
   */
  private String capitalizeWords(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }

    StringBuilder result = new StringBuilder();
    String[] words = text.split("\\s+");

    for (int i = 0; i < words.length; i++) {
      String word = words[i];
      if (word.isEmpty()) continue;

      // If it's a preposition or article in the middle of the text, keep it lowercase
      if (i > 0 && compoundIndicators.contains(word.toLowerCase())) {
        result.append(word.toLowerCase());
      } else {
        // First letter uppercase, rest lowercase
        result
            .append(Character.toUpperCase(word.charAt(0)))
            .append(word.substring(1).toLowerCase());
      }

      // Add space if it's not the last word
      if (i < words.length - 1) {
        result.append(" ");
      }
    }

    return result.toString();
  }

  private String normalizeText(String text) {
    // Replace multiple spaces/line breaks with a single space
    return text.replaceAll("\\s+", " ").trim();
  }

  private List<String> extractNameWithRegex(String text) {
    List<String> names = new ArrayList<>();

    // Common patterns in CVs
    List<Pattern> patterns = new ArrayList<>();
    patterns.add(Pattern.compile("(?i)name\\s*[:;]\\s*([A-Za-záéíóúüñÁÉÍÓÚÜÑ\\s]{2,50})"));
    patterns.add(
        Pattern.compile(
            "(?i)^([A-Za-záéíóúüñÁÉÍÓÚÜÑ\\s]{2,50})(\\s*curriculum|\\s*cv|\\s*resume)",
            Pattern.MULTILINE));
    patterns.add(
        Pattern.compile(
            "(?i)curriculum\\s+vitae\\s+of\\s+([A-Za-záéíóúüñÁÉÍÓÚÜÑ\\s]{2,50})",
            Pattern.MULTILINE));
    patterns.add(
        Pattern.compile("(?i)personal\\s+data\\s*[:;]?\\s*([A-Za-záéíóúüñÁÉÍÓÚÜÑ\\s]{2,50})"));

    // Try each pattern
    for (Pattern pattern : patterns) {
      Matcher matcher = pattern.matcher(text);
      if (matcher.find()) {
        String name = matcher.group(1).trim();
        names.add(name);
        break;
      }
    }

    return names;
  }

  private NameInfo splitNameAndSurnames(String fullName) {
    // Clean up and split
    fullName = fullName.replaceAll("[\\r\\n\\t]", " ").trim();
    fullName = removeAddressArtifacts(fullName);
    String[] parts = fullName.split("\\s+");

    if (parts.length <= 1) {
      return new NameInfo(parts.length == 0 ? "" : parts[0], "");
    }

    String firstName;
    StringBuilder lastNames = new StringBuilder();

    boolean secondWordIsName = isLikelyFirstName(parts[1]);
    if (secondWordIsName && parts.length >= 3) {
      firstName = parts[0] + " " + parts[1];
      for (int i = 2; i < Math.min(parts.length, 5); i++) {
        if (!lastNames.isEmpty()) lastNames.append(" ");
        lastNames.append(parts[i]);
      }
    } else {
      firstName = parts[0];
      for (int i = 1; i < Math.min(parts.length, 5); i++) {
        if (!lastNames.isEmpty()) lastNames.append(" ");
        lastNames.append(parts[i]);
      }
    }

    return new NameInfo(capitalizeWords(firstName), capitalizeWords(lastNames.toString()));
  }

  private String removeAddressArtifacts(String input) {
    // Remove after common address indicators (e.g. "Plz/", "C/", "Calle", digits)
    return input.replaceAll("(Plz/|C/|Calle|Avenida|Av\\.|\\d+\\w*).*", "").trim();
  }

  private boolean isLikelyFirstName(String word) {
    // If it's a preposition or article, it's definitely not a given name
    if (compoundIndicators.contains(word.toLowerCase())) {
      return false;
    }
    // If it starts with lowercase, it's probably not a given name
    if (!Character.isUpperCase(word.charAt(0))) {
      return false;
    }
    // If it's in our list of common names
    return commonSpanishNames.contains(word.toLowerCase());
  }
}
