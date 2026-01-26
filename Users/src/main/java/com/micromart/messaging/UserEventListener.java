package com.micromart.messaging;

import com.micromart.entities.User;
import com.micromart.models.data.PasswordResetEventDto;
import com.micromart.repositories.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserEventListener {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessagePublisher messagePublisher;
    @RabbitListener(queues = "password-reset-attempt-queue")
    public void handlePasswordResetAttempt(PasswordResetRequestEvent event) {
        String email = event.getEmail();

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);

        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiryDate(cal.getTime());
        userRepository.save(user);
        PasswordResetEventDto emailEvent = new PasswordResetEventDto(
                user.getEmail(),
                user.getFirstName(),
                token
        );

        messagePublisher.sendPasswordResetEvent(emailEvent);
    }
}
