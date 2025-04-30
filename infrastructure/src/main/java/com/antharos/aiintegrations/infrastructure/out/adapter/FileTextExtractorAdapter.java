package com.antharos.aiintegrations.infrastructure.out.adapter;

import com.antharos.aiintegrations.domain.repository.FileTextExtractor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

@Component
public class FileTextExtractorAdapter implements FileTextExtractor {

  public String extractTextFromFile(byte[] fileBytes) throws IOException {
    try (PDDocument document = PDDocument.load(new ByteArrayInputStream(fileBytes))) {
      PDFTextStripper stripper = new PDFTextStripper();
      return stripper.getText(document);
    }
  }
}
