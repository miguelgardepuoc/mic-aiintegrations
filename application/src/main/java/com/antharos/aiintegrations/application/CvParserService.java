package com.antharos.aiintegrations.application;

import com.antharos.aiintegrations.domain.NameInfo;
import com.antharos.aiintegrations.domain.repository.MessageProducer;
import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CvParserService {

  private static final String NAME_EXTRACTED_SUBJECT = "COMPLETE_NAME_EXTRACTED_FROM_CV";

  private final NameExtractionService nameExtractionService;

  private final MessageProducer messageProducer;

  public NameInfo extractName(final UUID id, final byte[] file) throws IOException {
    final String text = this.nameExtractionService.extractTextFromFile(file);
    final NameInfo nameInfo = this.nameExtractionService.findNameInText(text);
    this.messageProducer.sendMessage(id, NAME_EXTRACTED_SUBJECT, nameInfo);
    return nameInfo;
  }
}
