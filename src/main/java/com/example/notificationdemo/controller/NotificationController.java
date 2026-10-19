package com.example.notificationdemo.controller;

import com.example.notificationdemo.dto.NotificationRequest;
import com.example.notificationdemo.entity.Notification;
import com.example.notificationdemo.service.NotificationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Notification create(
            @Valid @RequestBody NotificationRequest request) {
        return notificationService.create(request);
    }
}