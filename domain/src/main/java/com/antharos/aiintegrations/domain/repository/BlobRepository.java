package com.antharos.aiintegrations.domain.repository;

public interface BlobRepository {
  byte[] downloadFile(String filename);
}
