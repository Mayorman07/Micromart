package com.micromart.scheduler;

import com.micromart.entities.User;
import com.micromart.messaging.MessagePublisher;
import com.micromart.messaging.ReactivationEvent;
import com.micromart.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReactivationScheduler {

    private final UserRepository userRepository;
    private final MessagePublisher messagePublisher;

    // Run every day at 10:00 AM
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendWeMissYouEmails() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgo = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, -60);
        Date ninetyDaysAgo = cal.getTime();
        List<User> inactiveUsers = userRepository.findUsersForReactivation(thirtyDaysAgo, ninetyDaysAgo);

        if (inactiveUsers.isEmpty()) {
            return;
        }
        List<ReactivationEvent> eventsToSend = new ArrayList<>();

        for (User user : inactiveUsers) {
            user.setLastReactivationEmailSentDate(new Date());
            eventsToSend.add(new ReactivationEvent(user.getEmail(), user.getFirstName()));
        }

        userRepository.saveAll(inactiveUsers);
        for (ReactivationEvent event : eventsToSend) {
            messagePublisher.sendReactivationEvent(event);
        }
    }
}