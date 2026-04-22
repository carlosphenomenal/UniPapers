package com.unipapers.backend.Modules.FileManagement.Repositories;

import com.unipapers.backend.Modules.FileManagement.Models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.beans.JavaBean;
import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);

    long countByUserIdAndIsReadFalse(Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
