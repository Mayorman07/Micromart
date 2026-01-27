package com.micromart.scheduler;

import com.micromart.entities.User;
import com.micromart.messaging.MessagePublisher;
import com.micromart.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ReactivationScheduler {

    @Autowired
    private UserRepository userRepository;
    @Autowired private MessagePublisher messagePublisher;

    // Run every day at 10:00 AM
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendWeMissYouEmails() {

        Calendar cal = Calendar.getInstance();

        // 1. Calculate dates
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgo = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, -60); // Go back another 60 (total 90)
        Date ninetyDaysAgo = cal.getTime();

        // 2. Find the users
        List<User> inactiveUsers = userRepository.findUsersForReactivation(thirtyDaysAgo, ninetyDaysAgo);

        // 3. Process them
        for (User user : inactiveUsers) {
            // Update the "Last Sent" date immediately so we don't pick them up tomorrow
            user.setLastReactivationEmailSentDate(new Date());
            userRepository.save(user);

            // Create event (Reuse your existing pattern!)
            ReactivationEvent event = new ReactivationEvent(user.getEmail(), user.getFirstName());

            // Push to RabbitMQ
            messagePublisher.sendReactivationEvent(event);
        }
    }
}