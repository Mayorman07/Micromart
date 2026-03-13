package com.micromart.Order.configuration;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // The main exchange where all microservices will publish their events
    public static final String MICROMART_EXCHANGE = "micromart.exchange";

    @Bean
    public TopicExchange micromartExchange() {
        return new TopicExchange(MICROMART_EXCHANGE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
