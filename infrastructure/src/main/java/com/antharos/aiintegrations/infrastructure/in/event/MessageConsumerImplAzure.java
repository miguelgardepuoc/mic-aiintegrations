package com.antharos.aiintegrations.infrastructure.in.event;

import com.antharos.aiintegrations.application.CvParserService;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("prod")
@Slf4j
@Component
public class MessageConsumerImplAzure extends AbstractMessageConsumer {

  private final ServiceBusProcessorClient processorClient;

  public MessageConsumerImplAzure(
      @Value("${consumer.event.connection-string}") String connectionString,
      @Value("${consumer.topic.name}") String topicName,
      @Value("${consumer.subscription.name}") String subscriptionName,
      CvParserService cvParserService) {

    super(cvParserService);

    this.processorClient =
        new ServiceBusClientBuilder()
            .connectionString(connectionString)
            .processor()
            .topicName(topicName)
            .subscriptionName(subscriptionName)
            .processMessage(
                context -> {
                  String body = context.getMessage().getBody().toString();
                  processMessageText(body);
                })
            .processError(
                errorContext ->
                    log.error(
                        "Error from Azure Service Bus: {}",
                        errorContext.getException().getMessage()))
            .buildProcessorClient();

    this.processorClient.start();
    log.info("Azure Service Bus processor started");
  }

  @PreDestroy
  public void cleanup() {
    if (processorClient != null) {
      processorClient.close();
    }
  }
}
