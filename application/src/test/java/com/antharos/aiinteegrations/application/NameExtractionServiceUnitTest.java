package com.antharos.aiinteegrations.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.antharos.aiintegrations.application.NameExtractionService;
import com.antharos.aiintegrations.domain.NameInfo;
import com.antharos.aiintegrations.domain.repository.FileTextExtractor;
import com.antharos.aiintegrations.domain.repository.ResourceReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NameExtractionServiceUnitTest {

  private NameExtractionService nameExtractionService;

  @Mock private FileTextExtractor fileTextExtractor;

  @Mock private ResourceReader resourceReader;

  @BeforeEach
  void setUp() throws IOException {
    Set<String> mockSpanishNames = new HashSet<>();
    mockSpanishNames.add("maria");
    mockSpanishNames.add("jose");
    mockSpanishNames.add("miguel");
    when(resourceReader.readNamesFromResource("names/spanish-names.txt"))
        .thenReturn(mockSpanishNames);

    nameExtractionService = new NameExtractionService(fileTextExtractor, resourceReader);

    nameExtractionService.initializeWithCustomPipeline(null);
  }

  @Test
  void findNameInText_WhenSimpleName_ShouldReturnCorrectNameInfo() {
    String text = "Name: Juan Pérez";
    NameInfo result = nameExtractionService.findNameInText(text);

    assertEquals("Juan", result.name());
    assertEquals("Pérez", result.surname());
  }

  @Test
  void findNameInText_WhenNoNameFound_ShouldReturnEmptyNameInfo() {
    String text = "This text contains no name information";
    NameInfo result = nameExtractionService.findNameInText(text);

    assertEquals("", result.name());
    assertEquals("", result.surname());
  }

  @Test
  void findNameInText_WhenNameWithSpecialCharacters_ShouldHandleCorrectly() {
    String text = "Name: José Ramírez Núñez";
    NameInfo result = nameExtractionService.findNameInText(text);

    assertEquals("José", result.name());
    assertEquals("Ramírez Núñez", result.surname());
  }

  @Test
  void findNameInText_WhenTextHasMultipleLineBreaks_ShouldNormalizeAndExtractName() {
    String text = "Name:   \n\nJuan   \r\nPérez";
    NameInfo result = nameExtractionService.findNameInText(text);

    assertEquals("Juan", result.name());
    assertEquals("Pérez", result.surname());
  }
}
