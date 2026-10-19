package com.example.notificationdemo.scheduler;

import com.example.notificationdemo.entity.Notification;
import com.example.notificationdemo.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

    private final NotificationService notificationService;

    public NotificationScheduler(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(fixedDelay = 30000)
    public void processDueNotifications() {
        for (Notification notification :
                notificationService.findDueNotifications()) {

            notificationService.sendNotification(
                    notification.getId());
        }
    }
}