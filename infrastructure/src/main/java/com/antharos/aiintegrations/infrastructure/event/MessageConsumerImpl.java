package com.antharos.aiintegrations.infrastructure.event;

import com.antharos.aiintegrations.application.CvParserService;
import com.antharos.aiintegrations.infrastructure.event.model.BaseMessage;
import com.antharos.aiintegrations.infrastructure.event.model.SignedUpCandidate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class MessageConsumerImpl implements MessageListener {

  private final CvParserService cvParserService;
  private final ObjectMapper objectMapper;

  @Override
  @JmsListener(
      destination = "${consumer.topic.name}",
      containerFactory = "jmsListenerContainerFactory")
  public void onMessage(jakarta.jms.Message message) {
    if (message instanceof TextMessage textMessage) {
      processMessage(textMessage);
    } else {
      log.error("Received unsupported message type: {}", message.getClass().getName());
    }
  }

  private void processMessage(TextMessage textMessage) {
    String messageText = null;
    try {
      messageText = textMessage.getText();
      log.info("Processing message: {}", messageText);

      BaseMessage<SignedUpCandidate> signedUpCandidateBaseMessage =
          this.objectMapper.readValue(messageText, new TypeReference<>() {});

      this.cvParserService.extractName(
          UUID.fromString(signedUpCandidateBaseMessage.getId()),
          signedUpCandidateBaseMessage.getCvFilename());
    } catch (Exception e) {
      log.error("Error processing message: {}", messageText, e);
    }
  }
}
