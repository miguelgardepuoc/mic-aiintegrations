package com.antharos.aiinteegrations.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.antharos.aiintegrations.application.CvParserService;
import com.antharos.aiintegrations.application.NameExtractionService;
import com.antharos.aiintegrations.domain.NameInfo;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CvParserServiceUnitTest {

  @Mock private NameExtractionService nameExtractionService;

  @Mock private MultipartFile mockFile;

  private CvParserService cvParserService;

  @BeforeEach
  void setUp() {
    cvParserService = new CvParserService(nameExtractionService);
  }

  @Test
  void extractName_WhenFileIsValid_ShouldReturnNameInfo() throws IOException {
    // Arrange
    String extractedText = "John Doe Resume";
    NameInfo expectedNameInfo = new NameInfo("John", "Doe");

    when(nameExtractionService.extractTextFromFile(mockFile)).thenReturn(extractedText);

    when(nameExtractionService.findNameInText(extractedText)).thenReturn(expectedNameInfo);

    // Act
    NameInfo result = cvParserService.extractName(mockFile);

    // Assert
    assertNotNull(result);
    assertEquals("John", result.name());
    assertEquals("Doe", result.surname());

    // Verify interactions
    verify(nameExtractionService).extractTextFromFile(mockFile);
    verify(nameExtractionService).findNameInText(extractedText);
  }

  @Test
  void extractName_WhenExtractionFails_ShouldThrowIOException() throws IOException {
    // Arrange
    when(nameExtractionService.extractTextFromFile(mockFile))
        .thenThrow(new IOException("File reading error"));

    // Act & Assert
    assertThrows(IOException.class, () -> cvParserService.extractName(mockFile));

    // Verify interactions
    verify(nameExtractionService).extractTextFromFile(mockFile);
    verify(nameExtractionService, never()).findNameInText(anyString());
  }

  @Test
  void extractName_WhenNoNameFound_ShouldReturnEmptyNameInfo() throws IOException {
    // Arrange
    String extractedText = "Some random text without a name";
    NameInfo emptyNameInfo = new NameInfo("", "");

    when(nameExtractionService.extractTextFromFile(mockFile)).thenReturn(extractedText);

    when(nameExtractionService.findNameInText(extractedText)).thenReturn(emptyNameInfo);

    // Act
    NameInfo result = cvParserService.extractName(mockFile);

    // Assert
    assertNotNull(result);
    assertTrue(result.name().isEmpty());
    assertTrue(result.surname().isEmpty());

    // Verify interactions
    verify(nameExtractionService).extractTextFromFile(mockFile);
    verify(nameExtractionService).findNameInText(extractedText);
  }
}
