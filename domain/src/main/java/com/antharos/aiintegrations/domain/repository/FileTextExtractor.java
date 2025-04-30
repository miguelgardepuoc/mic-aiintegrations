package com.antharos.aiintegrations.domain.repository;

import java.io.IOException;

public interface FileTextExtractor {
  String extractTextFromFile(byte[] file) throws IOException;
}
