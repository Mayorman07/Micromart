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

    // 2. New queue for future use (Good idea!)
    public static final String PASSWORD_RESET_QUEUE = "password.reset.queue";
    public static final String ROUTING_KEY_RESET = "password.reset";

    @Bean
    TopicExchange userEventsExchange() {
        return new TopicExchange(USER_EVENTS_EXCHANGE);
    }

    @Bean
    Queue userCreatedEmailQueue() {
        // durable = true means queue survives broker restart
        return new Queue(USER_CREATED_QUEUE, true);
    }

    @Bean
    Queue passwordResetEmailQueue() {
        return new Queue(PASSWORD_RESET_QUEUE, true);
    }

    // --- Bindings ---

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

    // --- Converter ---

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {

        return new Jackson2JsonMessageConverter();
    }
}
