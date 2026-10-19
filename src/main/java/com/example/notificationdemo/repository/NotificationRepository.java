package com.example.notificationdemo.repository;

import com.example.notificationdemo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByStatusAndReminderAtLessThanEqual(
            String status, Instant now);

    @Modifying
    @Query("""
        update Notification n
        set n.status = 'PROCESSING',
            n.processingAt = :now,
            n.attempts = n.attempts + 1
        where n.id = :id
          and n.status = 'PENDING'
          and n.reminderAt <= :now
        """)
    int claimNotification(
            @Param("id") Long id,
            @Param("now") Instant now);
}