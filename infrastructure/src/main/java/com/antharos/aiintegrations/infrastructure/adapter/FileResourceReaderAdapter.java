package com.antharos.aiintegrations.infrastructure.adapter;

import com.antharos.aiintegrations.domain.repository.ResourceReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class FileResourceReaderAdapter implements ResourceReader {

  @Override
  public Set<String> readNamesFromResource(String resourcePath) throws IOException {
    Set<String> names = new HashSet<>();

    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(createClassPathResource(resourcePath).getInputStream()))) {

      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim().toLowerCase();
        if (!line.isEmpty()) {
          names.add(line);
        }
      }
    }

    return names;
  }

  // Protected method to allow mocking in tests
  protected ClassPathResource createClassPathResource(String resourcePath) {
    return new ClassPathResource(resourcePath);
  }
}
