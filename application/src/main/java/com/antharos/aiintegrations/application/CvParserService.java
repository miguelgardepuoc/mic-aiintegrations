package com.antharos.aiintegrations.application;

import com.antharos.aiintegrations.domain.NameInfo;
import com.antharos.aiintegrations.domain.repository.BlobRepository;
import com.antharos.aiintegrations.domain.repository.MessageProducer;
import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CvParserService {

  private final NameExtractionService nameExtractionService;
  private final MessageProducer messageProducer;
  private final BlobRepository blobRepository;

  public NameInfo extractName(final UUID id, final String filename) throws IOException {
    final byte[] file = this.blobRepository.downloadFile(filename);
    final String text = this.nameExtractionService.extractTextFromFile(file);
    final NameInfo nameInfo = this.nameExtractionService.findNameInText(text);
    this.messageProducer.sendNameInfoEvent(nameInfo, id);
    return nameInfo;
  }
}
