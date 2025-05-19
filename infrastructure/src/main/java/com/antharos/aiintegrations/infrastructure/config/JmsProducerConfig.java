package com.antharos.aiintegrations.infrastructure.config;

import jakarta.jms.ConnectionFactory;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsProducerConfig {

  @Value("${event:host}")
  private String host;

  @Value("${event.port}")
  private int port;

  @Value("${ai-integrations.event.user}")
  private String username;

  @Value("${ai-integrations.event.password}")
  private String password;

  @Value("${ai-integrations.topic.name}")
  private String topicName;

  @Bean
  public ConnectionFactory producerConnectionFactory() {
    String url = String.format("amqp://%s:%d", host, port);
    return new JmsConnectionFactory(username, password, url);
  }

  @Bean
  public MessageConverter producerJacksonJmsMessageConverter() {
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("_type");
    return converter;
  }

  @Bean
  public JmsTemplate producerJmsTemplate() {
    JmsTemplate template = new JmsTemplate(producerConnectionFactory());
    template.setMessageConverter(producerJacksonJmsMessageConverter());
    template.setPubSubDomain(true);
    return template;
  }
}
