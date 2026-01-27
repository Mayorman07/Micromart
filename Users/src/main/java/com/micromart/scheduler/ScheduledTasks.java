package com.micromart.scheduler;

import com.micromart.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final UserService userService;

    @Autowired
    public ScheduledTasks(UserService userService) {
        this.userService = userService;
    }

    // This will run every 2 minutes.
//    @Scheduled(cron = "0 0/3 * * * ?")
    @Scheduled(cron = "0 0/3 * * * ?")
    public void sendWeMissedYouEmails() {
        logger.info("Running scheduled task to send we missed you email to inactive users...");
        int toBeReactivatedCount = userService.sendWeMissedYouEmails();
        logger.info("Scheduled task finished. Deactivated {} users.", toBeReactivatedCount);
    }
}

// put cron expression in application.properties, add a shedlock