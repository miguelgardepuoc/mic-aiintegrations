package com.antharos.aiintegrations.infrastructure.event;

import com.antharos.aiintegrations.domain.NameInfo;
import com.antharos.aiintegrations.domain.repository.MessageProducer;
import com.antharos.aiintegrations.infrastructure.event.model.NameInfoMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Topic;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageProducerImpl implements MessageProducer {

  private final ConnectionFactory producerConnectionFactory;
  private final ObjectMapper objectMapper;

  @Value("${producer.topic.name}")
  private String topicName;

  public MessageProducerImpl(
      ConnectionFactory producerConnectionFactory, ObjectMapper objectMapper) {
    this.producerConnectionFactory = producerConnectionFactory;
    this.objectMapper = objectMapper;
  }

  public void sendMessage(final UUID id, final String subject, final NameInfo nameInfo) {
    try (JMSContext context = this.producerConnectionFactory.createContext()) {
      final Topic topic = context.createTopic(this.topicName);

      final NameInfoMessage message = new NameInfoMessage(id.toString(), subject, nameInfo);

      final String messageJson = this.objectMapper.writeValueAsString(message);

      context.createProducer().send(topic, messageJson);

      log.info("Message sent: {}", messageJson);
    } catch (Exception e) {
      log.error("Error sending message: {} ", nameInfo, e);
    }
  }
}
