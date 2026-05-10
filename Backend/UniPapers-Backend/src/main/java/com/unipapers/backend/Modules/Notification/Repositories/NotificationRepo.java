package com.unipapers.backend.Modules.Notification.Repositories;

import com.unipapers.backend.Modules.Notification.Models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepo extends JpaRepository<Notification, Long> {

    long countByUserIdAndReadFalse(Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Notification> findByPublicId(String notificationPublicId);

    @Modifying(clearAutomatically = true)
    @Query("update Notification n set n.read = true where n.user.id = :userId and n.read = false")
    int markAllAsReadByUserId(@Param("userId") Long userId);

    long deleteByPublicIdAndUserId(String publicId, Long userId);
}
