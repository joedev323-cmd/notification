package com.example.notificationdemo.sender;

public interface EmailSender {

    void send(String to, String subject, String message);
}