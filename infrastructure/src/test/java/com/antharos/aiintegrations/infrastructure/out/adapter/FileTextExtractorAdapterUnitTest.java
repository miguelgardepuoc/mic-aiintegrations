package com.antharos.aiintegrations.infrastructure.out.adapter;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileTextExtractorAdapterUnitTest {

  @InjectMocks private FileTextExtractorAdapter fileTextExtractor;

  private byte[] validPdfBytes;
  private byte[] emptyPdfBytes;
  private byte[] invalidPdfBytes;

  @BeforeEach
  void setUp() throws IOException {
    // Create a sample valid PDF
    try (PDDocument document = new PDDocument();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      document.save(outputStream);
      validPdfBytes = outputStream.toByteArray();
    }

    // Empty PDF (same as valid but with no content)
    emptyPdfBytes = new byte[0];

    // Invalid PDF (random bytes)
    invalidPdfBytes = "Invalid PDF Content".getBytes();
  }

  @Test
  void extractTextFromFile_ShouldExtractText_WhenValidPdfProvided() throws IOException {
    String text = fileTextExtractor.extractTextFromFile(validPdfBytes);
    assertNotNull(text);
  }

  @Test
  void extractTextFromFile_ShouldThrowIOException_WhenPdfIsInvalid() {
    assertThrows(IOException.class, () -> fileTextExtractor.extractTextFromFile(invalidPdfBytes));
  }
}
