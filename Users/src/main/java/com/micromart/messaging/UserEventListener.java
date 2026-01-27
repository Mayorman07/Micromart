package com.micromart.messaging;

import com.micromart.entities.User;
import com.micromart.repositories.UserRepository;
import com.micromart.utils.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.micromart.models.data.PasswordResetEventDto;

import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final  UserRepository userRepository;
    private final MessagePublisher messagePublisher;
    private final TokenService tokenService;
    @RabbitListener(queues = "password-reset-attempt-queue")
    public void handlePasswordResetAttempt(PasswordResetRequestEvent event) {
        String email = event.getEmail();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) return;

        User user = userOptional.get();
        String token = tokenService.generateToken();
        Date expiryDate = tokenService.calculateExpiryDate(15);

        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiryDate(expiryDate);

        userRepository.save(user);
        PasswordResetEventDto emailEvent = new PasswordResetEventDto(
                user.getEmail(),
                user.getFirstName(),
                token
        );

        messagePublisher.sendPasswordResetEvent(emailEvent);
    }
}