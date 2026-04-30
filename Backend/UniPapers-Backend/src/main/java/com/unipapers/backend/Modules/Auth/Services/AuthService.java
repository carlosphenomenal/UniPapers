package com.unipapers.backend.Modules.Auth.Services;

import com.unipapers.backend.Modules.Auth.Dtos.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final EmailService emailService;

    public Object signup(SignupRequestDto signupRequestDto) {
        // Check if a user with the same student number or email exists
        // Register the user in the db

        // Generate a random 6-digit code (simple implementation for now)
        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Send a code to the email
        emailService.sendVerificationCode(signupRequestDto.getEmail(), verificationCode);

        return "Verification code sent to " + signupRequestDto.getEmail();
    }
}
