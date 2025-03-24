package com.antharos.aiintegrations.domain.repository;

import java.io.IOException;
import java.util.Set;

public interface ResourceReader {
  Set<String> readNamesFromResource(String resourcePath) throws IOException;
}
