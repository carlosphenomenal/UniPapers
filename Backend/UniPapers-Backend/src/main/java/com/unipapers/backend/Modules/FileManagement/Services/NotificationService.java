package com.unipapers.backend.Modules.FileManagement.Services;

import com.unipapers.backend.Modules.FileManagement.Models.Notification;
import com.unipapers.backend.Modules.FileManagement.Repositories.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepo notificationRepo;


    //creating the notification
    public void createNotification(Long userId, String message) {
        notificationRepo.save(Notification.builder()
                .userId(userId)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public List<Notification> getNotifications(Long userId) {
        return notificationRepo.findByUserId(userId);
    }


    //user notifications when made
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    //marking as read
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setRead(true);
        notificationRepo.save(notification);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepo.countByUserIdAndIsReadFalse(userId);
    }

    //count of unread 
}
