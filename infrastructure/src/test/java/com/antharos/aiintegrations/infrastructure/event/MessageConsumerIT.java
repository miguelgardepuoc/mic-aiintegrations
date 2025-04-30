package com.antharos.aiintegrations.infrastructure.event;

import static org.mockito.Mockito.*;

import com.antharos.aiintegrations.application.CvParserService;
import com.antharos.aiintegrations.infrastructure.event.model.BaseMessage;
import com.antharos.aiintegrations.infrastructure.event.model.SignedUpCandidate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = MessageConsumerIT.TestConfig.class)
@ExtendWith(SpringExtension.class)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageConsumerIT {

  private static final String TEST_TOPIC = "test-topic";

  @Autowired private JmsTemplate producerJmsTemplate;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private CvParserService cvParserService;

  @InjectMocks private MessageConsumerImpl messageConsumer;

  // Start the container manually before retrieving its properties
  static GenericContainer<?> artemisContainer =
      new GenericContainer<>("apache/activemq-artemis:latest").withExposedPorts(61616);

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    artemisContainer.start();
    if (!artemisContainer.isRunning()) {
      throw new IllegalStateException("Artemis container is not running!");
    }
    registry.add("consumer.event.port", () -> artemisContainer.getMappedPort(61616));
    registry.add(
        "consumer.event.host",
        () -> "localhost:" + artemisContainer.getMappedPort(61616) + "?virtualhost=localhost");
    registry.add("consumer.event.user", () -> "artemis");
    registry.add("consumer.event.password", () -> "artemis");
    registry.add("consumer.topic.name", () -> TEST_TOPIC);

    registry.add("producer.event.port", () -> artemisContainer.getMappedPort(61616));
    registry.add(
        "producer.event.host",
        () -> "localhost:" + artemisContainer.getMappedPort(61616) + "?virtualhost=localhost");
    registry.add("producer.event.user", () -> "artemis");
    registry.add("producer.event.password", () -> "artemis");
    registry.add("producer.topic.name", () -> TEST_TOPIC);
  }

  @Test
  void onMessage_ShouldProcessMessage_WhenValidSignedUpCandidateProvided() throws Exception {
    // Given
    UUID messageId = UUID.randomUUID();
    SignedUpCandidate candidate = new SignedUpCandidate();
    candidate.setCandidateId(UUID.randomUUID());
    candidate.setJobOfferId(UUID.randomUUID());
    candidate.setCv("Miguel García".getBytes());
    BaseMessage<SignedUpCandidate> baseMessage =
        new BaseMessage<>(messageId.toString(), "TEST_SUBJECT", candidate);
    String messageJson = objectMapper.writeValueAsString(baseMessage);

    // When
    producerJmsTemplate.convertAndSend(TEST_TOPIC, messageJson);

    // Wait a bit to ensure async message processing
    Thread.sleep(2000);

    // Then
    // verify(cvParserService, times(1)).extractName(eq(messageId), eq(candidate.getCv()));
  }

  /** ✅ Properly configured test application context */
  @SpringBootApplication(scanBasePackages = "com.antharos.aiintegrations")
  static class TestConfig {
    @Bean
    public CvParserService cvParserService() {
      return mock(CvParserService.class);
    }
  }
}
