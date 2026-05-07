package com.unipapers.backend.Modules.Notification.Controllers;


import com.unipapers.backend.Modules.Notification.Dtos.NotificationResponseDto;
import com.unipapers.backend.Modules.Notification.Dtos.SendNotificationRequestDto;
import com.unipapers.backend.Modules.Notification.Enums.NotificationType;
import com.unipapers.backend.Modules.Notification.Services.NotificationService;
import com.unipapers.backend.Utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    //get all notifications
//    @GetMapping("/get")
//    public List<NotificationResponseDto> getNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
//        return notificationService.getUserNotifications(userDetails.id());
//    }

    @GetMapping("/get")
    public List<NotificationResponseDto> getNotifications(@RequestParam("id") Long id) {
        return notificationService.getUserNotifications(id);
    }

    //mark a notification as read
    @PutMapping("/mark-as-read/{publicId}")
    public void markAsRead(@PathVariable String publicId){
        notificationService.markAsRead(publicId);
    }

    //counting the unread
    @GetMapping("/count-unread")
    public long getUnreadCount(@AuthenticationPrincipal CustomUserDetails userDetails){
        return notificationService.getUnreadCount(userDetails.id());
    }

    @PostMapping("/send")
    public void sendNotification(@RequestBody SendNotificationRequestDto dto){
        notificationService.sendNotification(
                dto.getUserPublicId(),
                dto.getTitle(),
                dto.getMessage(),
                dto.getNotificationType() != null ? NotificationType.valueOf(dto.getNotificationType()) : NotificationType.GENERAL);
    }

}
