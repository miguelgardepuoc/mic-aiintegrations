package com.antharos.aiintegrations.application;

import com.antharos.aiintegrations.domain.NameInfo;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class CvParserService {

  private final NameExtractionService nameExtractionService;

  public NameInfo extractName(final MultipartFile file) throws IOException {
    final String text = nameExtractionService.extractTextFromFile(file);
    return nameExtractionService.findNameInText(text);
  }
}
