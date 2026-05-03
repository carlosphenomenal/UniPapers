package com.unipapers.backend.Modules.Auth.Dtos;

import com.unipapers.backend.Common.Validators.MakerereEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailRequestDto {
    @NotBlank(message = "Email is required")
    @MakerereEmail(message = "This is not a valid Makerere email")
    private String email;

    @NotBlank(message = "Verification code is required")
    private String verificationCode;
}
