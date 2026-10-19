package com.example.notificationdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NotificationDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				NotificationDemoApplication.class, args);
	}
}