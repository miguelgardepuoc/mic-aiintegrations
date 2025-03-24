package com.antharos.aiintegrations.infrastructure.adapter;

import com.antharos.aiintegrations.domain.repository.FileTextExtractor;
import java.io.IOException;
import java.util.Objects;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileTextExtractorAdapter implements FileTextExtractor {

  public String extractTextFromFile(MultipartFile file) throws IOException {
    if (Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".pdf")) {
      try (PDDocument document = PDDocument.load(file.getInputStream())) {
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document);
      }
    }
    return "";
  }
}
