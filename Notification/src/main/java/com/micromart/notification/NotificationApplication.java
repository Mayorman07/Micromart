package com.micromart.notification;

import com.micromart.notification.channels.NotificationChannel;
import com.micromart.notification.factory.NotificationFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class NotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationApplication.class, args);
	}

	@Bean
	public CommandLineRunner testSms(NotificationFactory notificationFactory) {
		return args -> {
			System.out.println("Triggering SMS Test inside Notification Service...");

			NotificationChannel channel = notificationFactory.getChannel("SMS");
			String myMobileNumber = "+2347040544232";

			channel.sendNotification(
					myMobileNumber,
					"Ignored Subject",
					"Micromart Alert: Microservice SMS is working!"
			);
		};
	}

}
