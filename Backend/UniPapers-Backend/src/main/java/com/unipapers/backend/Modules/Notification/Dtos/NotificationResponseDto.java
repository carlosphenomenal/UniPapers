package com.unipapers.backend.Modules.Notification.Dtos;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private String publicId;
    private String title;
    private String message;
    private String notificationType;
    private boolean read;
    private Instant createdAt;
}
