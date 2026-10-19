package com.example.notificationdemo.sender;

public interface NotificationSender {
    void send(String to, String subject, String message);
}
