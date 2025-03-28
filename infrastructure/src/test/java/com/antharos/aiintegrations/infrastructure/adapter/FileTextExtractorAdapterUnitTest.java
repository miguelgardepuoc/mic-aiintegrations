package com.antharos.aiintegrations.infrastructure.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FileTextExtractorAdapterUnitTest {

  private FileTextExtractorAdapter fileTextExtractorAdapter;

  @BeforeEach
  void setUp() {
    fileTextExtractorAdapter = new FileTextExtractorAdapter();
  }

  @Test
  void extractTextFromPdfFile_ValidPdf_ReturnsExtractedText() throws IOException {
    // Arrange
    String expectedText = "Sample PDF Content";
    byte[] pdfContent = createSamplePdfBytes(expectedText);
    MultipartFile pdfFile =
        new MockMultipartFile("file", "sample.pdf", "application/pdf", pdfContent);

    // Act
    String extractedText = fileTextExtractorAdapter.extractTextFromFile(pdfFile);

    // Assert
    assertTrue(extractedText.contains(expectedText));
  }

  @Test
  void extractTextFromFile_NonPdfFile_ReturnsEmptyString() throws IOException {
    // Arrange
    MultipartFile txtFile =
        new MockMultipartFile(
            "file",
            "sample.txt",
            "text/plain",
            "Sample Text Content".getBytes(StandardCharsets.UTF_8));

    // Act
    String extractedText = fileTextExtractorAdapter.extractTextFromFile(txtFile);

    // Assert
    assertEquals("", extractedText);
  }

  @Test
  void extractTextFromFile_NullFilename_ThrowsNullPointerException() {
    // Arrange
    MultipartFile fileWithNullName = mock(MultipartFile.class);
    when(fileWithNullName.getOriginalFilename()).thenReturn(null);

    // Act & Assert
    assertThrows(
        NullPointerException.class,
        () -> fileTextExtractorAdapter.extractTextFromFile(fileWithNullName));
  }

  // Utility method to create a sample PDF with content
  private byte[] createSamplePdfBytes(String content) throws IOException {
    try (org.apache.pdfbox.pdmodel.PDDocument document =
        new org.apache.pdfbox.pdmodel.PDDocument()) {
      org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
      document.addPage(page);

      try (org.apache.pdfbox.pdmodel.PDPageContentStream contentStream =
          new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page)) {
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText(content);
        contentStream.endText();
      }

      java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
      document.save(baos);
      return baos.toByteArray();
    }
  }
}
