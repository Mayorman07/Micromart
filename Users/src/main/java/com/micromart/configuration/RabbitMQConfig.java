package com.micromart.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- 1. User Created Constants ---
    public static final String QUEUE_NAME = "user.notification.queue";
    public static final String EXCHANGE_NAME = "user.exchange";
    public static final String ROUTING_KEY = "user.created";

    // --- 2. Password Reset Constants (NEW) ---
    public static final String PASSWORD_RESET_QUEUE = "password.reset.queue";
    public static final String ATTEMPT_QUEUE = "password-reset-attempt-queue";
    public static final String PASSWORD_RESET_ROUTING_KEY = "password_reset_routing_key";
    public static final String PASSWORD_RESET_ATTEMPT_ROUTING_KEY = "password.reset.attempt";

    // ---- Reactivation Emails ---

    public static final String REACTIVATION_QUEUE = "user.reactivation.queue";
    public static final String SEND_REACTIVATION_EMAILS_ROUTING_KEY = "user.reactivation";

    // --- Exchange (Shared) ---
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // --- User Created Queue & Binding ---
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Queue attemptQueue() {
        return new Queue(ATTEMPT_QUEUE, true);
    }

    @Bean
    public Queue reactivationQueue() {
        return new Queue(SEND_REACTIVATION_EMAILS_ROUTING_KEY, true);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Binding bindingAttempt(Queue attemptQueue, TopicExchange exchange) {
        return BindingBuilder.bind(attemptQueue)
                .to(exchange)
                .with(PASSWORD_RESET_ATTEMPT_ROUTING_KEY);
    }

    @Bean
    public Binding bindingForReactivation(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(SEND_REACTIVATION_EMAILS_ROUTING_KEY);
    }

    // --- Password Reset Queue & Binding (NEW) ---
    @Bean
    public Queue passwordResetQueue() {
        return new Queue(PASSWORD_RESET_QUEUE);
    }

    @Bean
    public Binding passwordResetBinding(Queue passwordResetQueue, TopicExchange exchange) {
        return BindingBuilder.bind(passwordResetQueue).to(exchange).with(PASSWORD_RESET_ROUTING_KEY);
    }

    // --- Converters ---
    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}