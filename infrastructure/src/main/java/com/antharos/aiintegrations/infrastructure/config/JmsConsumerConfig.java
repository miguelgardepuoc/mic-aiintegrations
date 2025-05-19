package com.antharos.aiintegrations.infrastructure.config;

import jakarta.jms.ConnectionFactory;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms
public class JmsConsumerConfig {

  @Value("${job-offer.event.host}")
  private String host;

  @Value("${job-offer.event.port}")
  private int port;

  @Value("${job-offer.event.user}")
  private String username;

  @Value("${job-offer.event.password}")
  private String password;

  @Bean
  public ConnectionFactory consumerConnectionFactory() {
    String url = String.format("amqp://%s:%d", host, port);
    return new JmsConnectionFactory(username, password, url);
  }

  @Bean
  public MessageConverter consumerJacksonJmsMessageConverter() {
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("_type");
    return converter;
  }

  @Bean
  public JmsTemplate consumerJmsTemplate() {
    JmsTemplate template = new JmsTemplate(consumerConnectionFactory());
    template.setMessageConverter(consumerJacksonJmsMessageConverter());
    template.setPubSubDomain(true);
    return template;
  }

  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(consumerConnectionFactory());
    factory.setMessageConverter(consumerJacksonJmsMessageConverter());
    factory.setPubSubDomain(true);
    factory.setClientId("consumer-${subscription.name}");
    factory.setSubscriptionDurable(true);
    return factory;
  }
}
