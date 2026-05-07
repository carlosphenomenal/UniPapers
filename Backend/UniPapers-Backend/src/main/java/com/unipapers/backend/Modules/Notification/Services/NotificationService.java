package com.unipapers.backend.Modules.Notification.Services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.unipapers.backend.Common.Models.User;
import com.unipapers.backend.Common.Repositories.UserRepo;
import com.unipapers.backend.Exceptions.CustomExceptions.NotificationNotFoundException;
import com.unipapers.backend.Modules.Auth.Models.Session;
import com.unipapers.backend.Modules.Notification.Dtos.NotificationResponseDto;
import com.unipapers.backend.Modules.Notification.Enums.NotificationType;
import com.unipapers.backend.Modules.Notification.Models.Notification;
import com.unipapers.backend.Modules.Notification.Repositories.NotificationRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepo notificationRepo;
    private final UserRepo userRepo;

    // Send a notification to a user's device using Firebase Cloud Messaging (FCM)
    @Transactional
    public void sendNotification(User user, String title, String message, NotificationType notificationType){
        // Save the notification to the database
        saveNotificationToDatabase(user, title, message, notificationType);

        // Send push notifications to all devices connected to the user's account using Firebase Cloud Messaging (FCM)
        sendPushNotification(user, title, message);
    }

    // Overload the sendNotification method to also use the user id instead of the user object
    @Transactional
    public void sendNotification(String userPublicId, String title, String message, NotificationType notificationType){
        // Fetch the user from the database
        User user = userRepo.findByPublicId(userPublicId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userPublicId));

        // Save the notification to the database
        saveNotificationToDatabase(user, title, message, notificationType);

        // Send push notifications to all devices connected to the user's account using Firebase Cloud Messaging (FCM)
        sendPushNotification(user, title, message);
    }

    // user notifications when made
    // TODO: Implement pagination and caching for improved performance
    public List<NotificationResponseDto> getUserNotifications(Long userId) {

        List<NotificationResponseDto> notificationResponseDtos = new ArrayList<>();

        List<Notification> notifications = notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
        notifications
                .forEach(notification -> notificationResponseDtos.add(NotificationResponseDto.builder()
                        .publicId(notification.getPublicId())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .notificationType(notification.getNotificationType().toString())
                        .read(notification.isRead())
                        .createdAt(notification.getCreatedAt())
                        .build()));

        return notificationResponseDtos;
    }

    //marking as read
    public void markAsRead(String notificationPublicId) {
        Notification notification = notificationRepo.findByPublicId(notificationPublicId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));

        notification.setRead(true);
        notificationRepo.save(notification);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepo.countByUserIdAndReadFalse(userId);
    }


    // ============= HELPER METHODS ============

    private void saveNotificationToDatabase(
            User user,
            String title,
            String message,
            NotificationType notificationType){
        notificationRepo.save(Notification.builder()
                .user(user)
                .notificationType(notificationType)
                .title(title)
                .message(message)
                .build());
    }

    private void sendPushNotification(User user, String title, String message){
        List<Session> sessions = user.getSessions();
        sessions.forEach(session -> {
            Message fireBaseMessage = Message.builder()
                    .setToken(session.getFcmToken())
                    .setNotification(
                            com.google.firebase.messaging.Notification.builder()
                                    .setTitle(title)
                                    .setBody(message)
                                    .build()
                    )
                    .build();

            try {
                FirebaseMessaging.getInstance().send(fireBaseMessage);
            } catch (FirebaseMessagingException e) {
                if (e.getMessagingErrorCode() ==
                        MessagingErrorCode.UNREGISTERED) {

                    log.error("Firebase token is invalid: {}, user might have reinstalled the app or cleared app data", session.getFcmToken());
                }
            }
        });
    }

}
