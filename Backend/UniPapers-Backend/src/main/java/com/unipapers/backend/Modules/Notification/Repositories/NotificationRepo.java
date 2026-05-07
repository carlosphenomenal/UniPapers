package com.unipapers.backend.Modules.Notification.Repositories;

import com.unipapers.backend.Modules.Notification.Models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepo extends JpaRepository<Notification, Long> {

    long countByUserIdAndReadFalse(Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Notification> findByPublicId(String notificationPublicId);
}
