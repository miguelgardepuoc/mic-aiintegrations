package com.antharos.aiinteegrations.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.antharos.aiintegrations.application.CvParserService;
import com.antharos.aiintegrations.application.NameExtractionService;
import com.antharos.aiintegrations.domain.NameInfo;
import com.antharos.aiintegrations.domain.repository.MessageProducer;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CvParserServiceUnitTest {

  @Mock private NameExtractionService nameExtractionService;

  @Mock private MessageProducer messageProducer;

  @InjectMocks private CvParserService cvParserService;

  private UUID id;
  private byte[] pdfFile;
  private String extractedText;
  private NameInfo extractedName;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    pdfFile = "Dummy PDF content".getBytes(); // Simulate a PDF file as byte[]
    extractedText = "John Doe is a software engineer.";
    extractedName = new NameInfo("John", "Doe");
  }

  @Test
  void extractName_ShouldExtractText_FindName_AndSendMessage() throws IOException {
    // Mock dependencies
    when(nameExtractionService.extractTextFromFile(pdfFile)).thenReturn(extractedText);
    when(nameExtractionService.findNameInText(extractedText)).thenReturn(extractedName);

    // Call the method
    NameInfo result = cvParserService.extractName(id, pdfFile);

    // Verify interactions
    verify(nameExtractionService, times(1)).extractTextFromFile(pdfFile);
    verify(nameExtractionService, times(1)).findNameInText(extractedText);
    verify(messageProducer, times(1))
        .sendMessage(id, "COMPLETE_NAME_EXTRACTED_FROM_CV", extractedName);

    // Validate the result
    assertEquals(extractedName, result);
  }
}
