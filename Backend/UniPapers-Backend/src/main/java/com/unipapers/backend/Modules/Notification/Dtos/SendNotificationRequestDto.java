package com.unipapers.backend.Modules.Notification.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequestDto {
    private String userPublicId;
    private String title;
    private String message;
    private String notificationType;
}
