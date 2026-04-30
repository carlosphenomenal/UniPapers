package com.unipapers.backend.Modules.Auth.Services;

import com.unipapers.backend.Common.Models.User;
import com.unipapers.backend.Common.Repositories.UserRepo;
import com.unipapers.backend.Modules.FileManagement.Models.Program;
import com.unipapers.backend.Modules.FileManagement.Repositories.ProgramRepo;
import com.unipapers.backend.Modules.Auth.Dtos.SignupRequestDto;
import com.unipapers.backend.Utils.SemesterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.unipapers.backend.Modules.Auth.Models.EmailVerificationCode;
import com.unipapers.backend.Modules.Auth.Repositories.EmailVerificationCodeRepo;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final EmailService emailService;
    private final UserRepo userRepo;
    private final ProgramRepo programRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationCodeRepo emailVerificationCodeRepo;

    @Value("${auth.verification-code.expiry-minutes:15}")
    private long verificationCodeExpiryMinutes;

    public Object signup(SignupRequestDto signupRequestDto) {
        // Check if a user with the same student number or email exists
        if (userRepo.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists with email: " + signupRequestDto.getEmail());
        }
        if (userRepo.findByStudentNumber(signupRequestDto.getStudentNumber()).isPresent()) {
            throw new IllegalArgumentException("User already exists with student number: " + signupRequestDto.getStudentNumber());
        }

        Program program = programRepo.findByPublicId(signupRequestDto.getProgrammePublicId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Program not found with publicId: " + signupRequestDto.getProgrammePublicId()
                ));

        // Generate a random 6-digit code (simple implementation for now)
        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Register the user in the db
        User user = User.builder()
                .firstName(signupRequestDto.getFirstName())
                .lastName(signupRequestDto.getLastName())
                .email(signupRequestDto.getEmail())
                .studentNumber(signupRequestDto.getStudentNumber())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .program(program)
                .yearOfStudy(signupRequestDto.getYearOfStudy())
                .semester(SemesterUtils.currentSemester())
                .build();

        userRepo.save(user);

        EmailVerificationCode codeRecord = EmailVerificationCode.builder()
                .user(user)
                .code(verificationCode)
                .expiresAt(Instant.now().plusSeconds(verificationCodeExpiryMinutes * 60L))
                .build();
        emailVerificationCodeRepo.save(codeRecord);

        // Send a code to the email
        emailService.sendVerificationCode(signupRequestDto.getEmail(), verificationCode);

        return "Verification code sent to " + signupRequestDto.getEmail();
    }

    public Object verifyEmail(String email, String verificationCode) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        if (user.isEmailVerified()) {
            return "Email already verified for " + email;
        }

        Instant now = Instant.now();
        emailVerificationCodeRepo.deleteByUserAndExpiresAtBefore(user, now);

        EmailVerificationCode codeRecord = emailVerificationCodeRepo
                .findTopByUserAndCodeOrderByCreatedAtDesc(user, verificationCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification code for " + email));

        if (codeRecord.getExpiresAt().isBefore(now)) {
            emailVerificationCodeRepo.delete(codeRecord);
            throw new IllegalArgumentException("Verification code expired for " + email);
        }

        user.setEmailVerified(true);
        userRepo.save(user);
        emailVerificationCodeRepo.delete(codeRecord);

        return "Email verified for " + email;
    }
}
