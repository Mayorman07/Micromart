package com.micromart.messaging;

import com.micromart.configuration.RabbitMQConfig;
import com.micromart.models.data.UserCreatedEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagePublisher {
    private final RabbitTemplate rabbitTemplate;

    public void sendUserCreatedEvent(UserCreatedEventDto eventDto) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                eventDto
        );
        System.out.println("🐇 Message sent to RabbitMQ: " + eventDto.getEmail());
    }
}