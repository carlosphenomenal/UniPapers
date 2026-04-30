package com.unipapers.backend.Modules.Auth.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String publicId;
    private String email;
    private String firstName;
    private String lastName;
    private String accessToken;
    private String refreshToken;
    private String deviceId;
    private boolean isEmailVerified;
    List<String> roles;
}
