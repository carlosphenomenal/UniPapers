package com.unipapers.backend.Modules.FileManagement.Controllers;


import com.unipapers.backend.Modules.FileManagement.Models.Notification;
import com.unipapers.backend.Modules.FileManagement.Services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    //get all notifications
    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    //mark as read
    @PutMapping("/read/{id}")
    public void markAsRead(@PathVariable Long id){
        notificationService.markAsRead(id);
    }

    //counting the unread
    @GetMapping("/unread/{userId}")
    public long getUnreadCount(@PathVariable Long userId){
        return notificationService.getUnreadCount(userId);
    }
}
