package com.micromart.notification.configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQConfig {

    public static final String USER_EVENTS_EXCHANGE = "user.exchange";
    public static final String USER_CREATED_QUEUE = "user.notification.queue";
    public static final String ROUTING_KEY_CREATED = "user.created";
    public static final String PASSWORD_RESET_QUEUE = "password.reset.queue";
    public static final String ROUTING_KEY_RESET = "password.reset";
    public static final String USER_REACTIVATION_QUEUE = "user.reactivation.queue";
    public static final String USER_REACTIVATION_CREATED = "user.reactivation";

    public static final String PAYMENT_EXCHANGE = "payment_exchange";
    public static final String PAYMENT_NOTIFICATION_QUEUE = "payment.notification.queue";
    public static final String PAYMENT_ROUTING_KEY = "payment_status_key";

    // ---  Dead Letter Constants ---
    public static final String NOTIFICATION_DLX = "notification.dlx";
    public static final String NOTIFICATION_DLQ = "notification.dlq";
    public static final String DLQ_ROUTING_KEY = "notification.deadletter.key";

    // --- Exchanges ---
    @Bean
    TopicExchange userEventsExchange() { return new TopicExchange(USER_EVENTS_EXCHANGE); }

    @Bean
    TopicExchange paymentExchange() { return new TopicExchange(PAYMENT_EXCHANGE); }

    @Bean
    DirectExchange deadLetterExchange() { return new DirectExchange(NOTIFICATION_DLX); }

    // --- Queues (Configured with DLX) ---

    @Bean
    Queue userCreatedEmailQueue() {
        return QueueBuilder.durable(USER_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", NOTIFICATION_DLX)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    Queue passwordResetEmailQueue() {
        return QueueBuilder.durable(PASSWORD_RESET_QUEUE)
                .withArgument("x-dead-letter-exchange", NOTIFICATION_DLX)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    Queue userReactivationQueue() {
        return QueueBuilder.durable(USER_REACTIVATION_QUEUE)
                .withArgument("x-dead-letter-exchange", NOTIFICATION_DLX)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    Queue paymentNotificationQueue() {
        return QueueBuilder.durable(PAYMENT_NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", NOTIFICATION_DLX)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    Queue deadLetterQueue() {
        return new Queue(NOTIFICATION_DLQ, true);
    }

    // --- Bindings ---

    @Bean
    Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    Binding paymentBinding(Queue paymentNotificationQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentNotificationQueue)
                .to(paymentExchange)
                .with(PAYMENT_ROUTING_KEY);
    }

    @Bean
    Binding userCreatedBinding(Queue userCreatedEmailQueue, TopicExchange userEventsExchange) {
        return BindingBuilder.bind(userCreatedEmailQueue)
                .to(userEventsExchange)
                .with(ROUTING_KEY_CREATED);
    }

    @Bean
    Binding passwordResetBinding(Queue passwordResetEmailQueue, TopicExchange userEventsExchange) {
        return BindingBuilder.bind(passwordResetEmailQueue)
                .to(userEventsExchange)
                .with(ROUTING_KEY_RESET);
    }

    @Bean
    Binding userReactivatedBinding(Queue userReactivationQueue, TopicExchange userEventsExchange) {
        return BindingBuilder.bind(userReactivationQueue)
                .to(userEventsExchange)
                .with(USER_REACTIVATION_CREATED);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}