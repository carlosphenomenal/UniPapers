package com.unipapers.backend.Modules.Auth.Dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = "Identifier (email or student number) is required")
    private String identifier; // email or student number

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "fcm token is required")
    private String fcmToken;

}
