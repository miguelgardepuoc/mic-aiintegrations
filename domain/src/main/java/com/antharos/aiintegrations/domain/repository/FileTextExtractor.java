package com.antharos.aiintegrations.domain.repository;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FileTextExtractor {
  String extractTextFromFile(MultipartFile file) throws IOException;
}
