package com.micromart.messaging;

import com.micromart.configuration.RabbitMQConfig;
import com.micromart.models.data.UserCreatedEventDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public MessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUserCreatedEvent(UserCreatedEventDto eventDto) {
        // "Exchange", "Routing Key", "Data"
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                eventDto
        );
        System.out.println("🐇 Message sent to RabbitMQ: " + eventDto.getEmail());
    }
}