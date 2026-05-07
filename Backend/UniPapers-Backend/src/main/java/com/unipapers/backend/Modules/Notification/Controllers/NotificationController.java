package com.unipapers.backend.Modules.Notification.Controllers;


import com.unipapers.backend.Modules.Notification.Dtos.NotificationResponseDto;
import com.unipapers.backend.Modules.Notification.Dtos.SendNotificationRequestDto;
import com.unipapers.backend.Modules.Notification.Enums.NotificationType;
import com.unipapers.backend.Modules.Notification.Services.NotificationService;
import com.unipapers.backend.Utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Get all notifications for the authenticated user
    @GetMapping("/get")
    public ResponseEntity<List<NotificationResponseDto>> getNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userDetails.id()));
    }

    //mark a notification as read
    @PutMapping("/mark-as-read/{publicId}")
    public ResponseEntity<?> markAsRead(@PathVariable String publicId){

        notificationService.markAsRead(publicId);
        return ResponseEntity.ok().body(Map.of("message", "Notification marked as read"));

    }

    //counting the unread
    @GetMapping("/count-unread")
    public long getUnreadCount(@AuthenticationPrincipal CustomUserDetails userDetails){
        return notificationService.getUnreadCount(userDetails.id());
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody SendNotificationRequestDto dto){
        notificationService.sendNotification(
                dto.getUserPublicId(),
                dto.getTitle(),
                dto.getMessage(),
                dto.getNotificationType() != null ? NotificationType.valueOf(dto.getNotificationType()) : NotificationType.GENERAL);
        return ResponseEntity.ok().body(Map.of("message", "Notification sent successfully"));
    }


    @PostMapping("/broadcast")
    public ResponseEntity<?> sendBroadcastNotification(@RequestBody SendNotificationRequestDto dto){
        notificationService.sendBroadcastNotification(
                dto.getTitle(),
                dto.getMessage(),
                dto.getNotificationType() != null ? NotificationType.valueOf(dto.getNotificationType()) : NotificationType.GENERAL);
        return ResponseEntity.ok().body(Map.of("message", "Notification broadcast successfully"));
    }

}
