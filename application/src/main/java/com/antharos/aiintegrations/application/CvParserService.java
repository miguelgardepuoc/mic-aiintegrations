package com.antharos.aiintegrations.application;

import com.antharos.aiintegrations.domain.NameInfo;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CvParserService {

  private final NameExtractionService nameExtractionService;

  public CvParserService(NameExtractionService nameExtractionService) {
    this.nameExtractionService = nameExtractionService;
  }

  public NameInfo extractName(MultipartFile file) throws IOException {
    String text = nameExtractionService.extractTextFromFile(file);
    return nameExtractionService.findNameInText(text);
  }
}
