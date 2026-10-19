package com.example.notificationdemo.service;

import com.example.notificationdemo.entity.Notification;
import com.example.notificationdemo.dto.NotificationRequest;
import com.example.notificationdemo.repository.NotificationRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final JavaMailSender mailSender;

    public NotificationService(
            NotificationRepository repository,
            JavaMailSender mailSender) {
        this.repository = repository;
        this.mailSender = mailSender;
    }

    public Notification create(NotificationRequest request) {
        Instant now = Instant.now();

        if (!request.reminderAt().isBefore(request.deadlineAt())) {
            throw new IllegalArgumentException(
                    "Reminder time must be before the deadline"
            );
        }

        if (request.deadlineAt().isBefore(now)) {
            throw new IllegalArgumentException(
                    "Deadline must be in the future"
            );
        }

        Notification notification = new Notification();
        notification.setRecipient(request.recipient());
        notification.setSubject(request.subject());
        notification.setMessage(request.message());
        notification.setDeadlineAt(request.deadlineAt());
        notification.setReminderAt(request.reminderAt());
        notification.setStatus("PENDING");
        notification.setAttempts(0);

        return repository.save(notification);
    }

    public List<Notification> findDueNotifications() {
        return repository.findByStatusAndReminderAtLessThanEqual(
                "PENDING", Instant.now());
    }

    @Transactional
    public void sendNotification(Long id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Notification not found"));

        if (!"PENDING".equals(notification.getStatus())
                || notification.getReminderAt().isAfter(Instant.now())) {
            return;
        }

        notification.setAttempts(notification.getAttempts() + 1);

        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(notification.getRecipient());
            email.setSubject(notification.getSubject());
            email.setText(
                    notification.getMessage()
                            + "\n\nDeadline: "
                            + notification.getDeadlineAt());

            mailSender.send(email);

            notification.setStatus("SENT");
            notification.setSentAt(Instant.now());
            notification.setLastError(null);

        } catch (Exception ex) {
            notification.setLastError(
                    ex.getMessage() == null
                            ? "Email sending failed"
                            : ex.getMessage());

            // Leave it pending so a later scheduler run can retry.
        }


    }
    @Transactional
    public boolean claimNotification(Long id) {
        return repository.claimNotification(id, Instant.now()) == 1;
    }
}