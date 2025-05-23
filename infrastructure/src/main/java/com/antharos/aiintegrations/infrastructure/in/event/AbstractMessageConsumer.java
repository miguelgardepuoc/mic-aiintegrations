package com.antharos.aiintegrations.infrastructure.in.event;

import com.antharos.aiintegrations.application.CvParserService;
import com.antharos.aiintegrations.infrastructure.in.event.model.EventMapper;
import com.antharos.aiintegrations.infrastructure.in.event.model.SignedUpCandidate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractMessageConsumer {

  protected final CvParserService cvParserService;

  public AbstractMessageConsumer(CvParserService cvParserService) {
    this.cvParserService = cvParserService;
  }

  protected void processMessageText(String textMessage) {
    String messageText = null;
    try {
      messageText = textMessage;
      log.info("Processing message: {}", messageText);
      SignedUpCandidate event = EventMapper.mapToSignedUpCandidateEvent(messageText);
      this.cvParserService.extractName(event.getCandidateId(), event.getCvFilename());
    } catch (Exception e) {
      log.error("Error processing message: {}", messageText, e);
    }
  }
}
