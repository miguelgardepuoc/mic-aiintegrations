package com.antharos.aiintegrations.infrastructure.out.event;

import com.antharos.aiintegrations.domain.NameInfo;
import com.antharos.aiintegrations.domain.repository.MessageProducer;
import com.antharos.aiintegrations.infrastructure.out.event.model.BaseEvent;
import com.antharos.aiintegrations.infrastructure.out.event.model.NameInfoPayload;
import com.antharos.aiintegrations.infrastructure.out.event.model.NameInfoPayloadMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Topic;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageProducerImpl implements MessageProducer {

  private final ConnectionFactory producerConnectionFactory;
  private final ObjectMapper objectMapper;
  private final NameInfoPayloadMapper mapper;

  @Value("${producer.topic.name}")
  private String topicName;

  private static final String NAME_EXTRACTED_SUBJECT = "COMPLETE_NAME_EXTRACTED_FROM_CV";

  public MessageProducerImpl(
      ConnectionFactory producerConnectionFactory,
      ObjectMapper objectMapper,
      NameInfoPayloadMapper mapper) {
    this.producerConnectionFactory = producerConnectionFactory;
    this.objectMapper = objectMapper;
    this.mapper = mapper;
  }

  public void sendMessage(final BaseEvent<NameInfoPayload> event) {
    try (JMSContext context = this.producerConnectionFactory.createContext()) {
      final Topic topic = context.createTopic(this.topicName);

      final String messageJson = this.objectMapper.writeValueAsString(event);

      context.createProducer().send(topic, messageJson);

      log.info("Message sent: {}", messageJson);
    } catch (Exception e) {
      log.error("Error sending message: {} ", event, e);
    }
  }

  @Override
  public void sendNameInfoEvent(NameInfo nameInfo, UUID candidateId) {
    BaseEvent<NameInfoPayload> event =
        new BaseEvent<>(
            UUID.randomUUID().toString(),
            Instant.now(),
            NAME_EXTRACTED_SUBJECT,
            candidateId.toString(),
            null,
            null,
            1,
            this.mapper.toPayload(nameInfo, candidateId));
    this.sendMessage(event);
  }
}
