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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class NameExtractionService {
  private final StanfordCoreNLP pipeline;
  private final Set<String> commonSpanishNames;
  private final Set<String> compoundIndicators;
  private final FileTextExtractor fileTextExtractor;

  public NameExtractionService(FileTextExtractor fileTextExtractor, ResourceReader resourceReader)
      throws IOException {
    this.fileTextExtractor = fileTextExtractor;

    // Configure Stanford NLP for named entity recognition
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
    props.setProperty("ner.useSUTime", "false");
    props.setProperty(
        "ner.model", "edu/stanford/nlp/models/ner/spanish.ancora.distsim.s512.crf.ser.gz");
    this.pipeline = new StanfordCoreNLP(props);

    // Initialize set of common Spanish names from resource file
    this.commonSpanishNames = resourceReader.readNamesFromResource("names/spanish-names.txt");

    // Prepositions and articles for compound surnames
    this.compoundIndicators =
        new HashSet<>(
            Arrays.asList("de", "del", "la", "las", "los", "da", "di", "van", "von", "el"));
  }

  public String extractTextFromFile(MultipartFile file) throws IOException {
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

  private List<String> extractPersonNamesWithNLP(String text) {
    List<String> names = new ArrayList<>();

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
    // Clean any special characters or unnecessary spaces
    fullName = fullName.replaceAll("[\\r\\n\\t]", " ").trim();
    String[] parts = fullName.split("\\s+");

    // Single case or without space
    if (parts.length <= 1) {
      return new NameInfo(parts.length == 0 ? "" : parts[0], "");
    }

    // In Spain the typical format is:
    // 1 given name + 1 paternal surname + 1 maternal surname
    // Or: 1 compound name + 1-2 surnames

    // Strategy:
    // 1. If there are 2 words: assume given name + surname
    // 2. If there are 3 or more:
    //    a. Verify if the 2nd part is a common name or starts with lowercase
    //       - If so, it's a compound name or part of surname
    //    b. Otherwise, the 1st word is the given name, the rest are surnames

    if (parts.length == 2) {
      // Simple case: given name + surname
      return new NameInfo(parts[0], parts[1]);
    }

    // Case with 3 or more words
    String firstName = parts[0];
    StringBuilder lastNames = new StringBuilder();

    // In Spanish, compound names like "María José" are common
    // but names like "Miguel García" (where García is a surname) also exist
    boolean secondWordIsName = isLikelyFirstName(parts[1]);

    // If the second word appears to be a given name, then it's a compound name
    if (secondWordIsName) {
      firstName = parts[0] + " " + parts[1];

      // The rest are surnames
      for (int i = 2; i < parts.length; i++) {
        if (!lastNames.isEmpty()) lastNames.append(" ");
        lastNames.append(parts[i]);
      }
    } else {
      // The first word is the given name, the rest are surnames
      for (int i = 1; i < parts.length; i++) {
        if (!lastNames.isEmpty()) lastNames.append(" ");
        lastNames.append(parts[i]);
      }
    }

    return new NameInfo(capitalizeWords(firstName), capitalizeWords(lastNames.toString()));
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
