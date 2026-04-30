package com.unipapers.backend.Modules.Auth.Services;

import com.github.f4b6a3.ulid.UlidCreator;
import com.unipapers.backend.Common.Models.User;
import com.unipapers.backend.Common.Repositories.UserRepo;
import com.unipapers.backend.Modules.Auth.Dtos.LoginRequestDto;
import com.unipapers.backend.Modules.Auth.Dtos.LoginResponseDto;
import com.unipapers.backend.Modules.FileManagement.Models.Program;
import com.unipapers.backend.Modules.FileManagement.Repositories.ProgramRepo;
import com.unipapers.backend.Modules.Auth.Dtos.SignupRequestDto;
import com.unipapers.backend.Utils.SemesterUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.unipapers.backend.Modules.Auth.Models.EmailVerificationCode;
import com.unipapers.backend.Modules.Auth.Models.Session;
import com.unipapers.backend.Modules.Auth.Repositories.EmailVerificationCodeRepo;
import com.unipapers.backend.Modules.Auth.Repositories.SessionRepo;
import com.unipapers.backend.Configurations.Security.JwtService;
import com.unipapers.backend.Utils.CustomUserDetails;
import com.unipapers.backend.Modules.Auth.Enums.RevokedReason;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final EmailService emailService;
    private final UserRepo userRepo;
    private final ProgramRepo programRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationCodeRepo emailVerificationCodeRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionRepo sessionRepo;

    @Value("${auth.verification-code.expiry-minutes:15}")
    private long verificationCodeExpiryMinutes;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

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

    public Object resendVerificationCode(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        if (user.isEmailVerified()) {
            return "Email already verified for " + email;
        }

        // Generate a new code
        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Invalidate old codes
        Instant now = Instant.now();
        emailVerificationCodeRepo.deleteByUserAndExpiresAtBefore(user, now);

        EmailVerificationCode codeRecord = EmailVerificationCode.builder()
                .user(user)
                .code(verificationCode)
                .expiresAt(now.plusSeconds(verificationCodeExpiryMinutes * 60L))
                .build();
        emailVerificationCodeRepo.save(codeRecord);

        // Send the new code to the email
        emailService.sendVerificationCode(email, verificationCode);

        return "New verification code sent to " + email;
    }

    public LoginResponseDto login(LoginRequestDto request){
        // Store the credentials sent in the request in an Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword());

        // Authenticate the credentials
        Authentication auth = authenticationManager.authenticate(authentication);

        if (!(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("Unexpected principal type: " + Objects.requireNonNull(auth.getPrincipal()).getClass().getName());
        }

        String studentNumber = Long.toString(userDetails.studentNumber());
        Map<String, Object> claims = Map.of(
                "roles", userDetails.roles(),
                "email", userDetails.email(),
                "publicId", userDetails.publicId()
        );

        String accessToken = jwtService.generateAccessToken(studentNumber, claims);
        String refreshToken = jwtService.generateRefreshToken(studentNumber);

        User user = userRepo.findByStudentNumber(userDetails.studentNumber())
                .orElseThrow(() -> new IllegalStateException("User not found for student number: " + studentNumber));

        long activeSessions = sessionRepo.countByUserAndRevokedFalse(user);
        if (activeSessions >= 5) {
            Instant now = Instant.now();
            sessionRepo.findFirstByUserAndRevokedFalseOrderByCreatedAtAsc(user)
                    .ifPresent(session -> {
                        session.setRevoked(true);
                        session.setRevokedAt(now);
                        session.setRevokedReason(RevokedReason.CREATE_ROOM_FOR_OTHER_SESSIONS);
                        sessionRepo.save(session);
                    });
        }

        String refreshTokenHash = SessionService.hashToken(refreshToken);
        Instant refreshTokenExpiresAt = jwtService.extractExpirationDate(refreshToken);
        String deviceId = generateUuidV7();

        Session session = Session.builder()
                .publicSessionId(UlidCreator.getUlid().toString())
                .user(user)
                .refreshTokenHash(refreshTokenHash)
                .refreshTokenExpiresAt(refreshTokenExpiresAt)
                .deviceId(deviceId)
                .revoked(false)
                .build();
        sessionRepo.save(session);

        return LoginResponseDto.builder()
                .publicId(userDetails.publicId())
                .email(userDetails.email())
                .firstName(userDetails.firstName())
                .lastName(userDetails.lastName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .deviceId(deviceId)
                .isEmailVerified(userDetails.emailVerified())
                .roles(userDetails.roles())
                .build();
    }

    public void logout(String refreshToken) {
        String tokenHash = SessionService.hashToken(refreshToken);
        Optional<Session> sessionOpt = sessionRepo.findByRefreshTokenHashAndRevokedFalse(tokenHash);

        if (sessionOpt.isEmpty()) {
            // Token doesn't match any active session — could be already logged out or invalid.
            // We still return success to avoid leaking session state.
            log.info("Logout requested but no active session found for the provided refresh token.");
            return;
        }

        Session session = sessionOpt.get();
        session.setRevoked(true);
        session.setRevokedAt(Instant.now());
        session.setRevokedReason(RevokedReason.LOGOUT);
        sessionRepo.save(session);
        log.info("User logged out. Session {} revoked for user {}.",
                session.getPublicSessionId(), session.getUser().getStudentNumber());
    }

    // ==================== HELPER METHODS ====================

    private String generateUuidV7() {
        long timestampMs = Instant.now().toEpochMilli() & 0xFFFFFFFFFFFFL;
        int randA = SECURE_RANDOM.nextInt(1 << 12);
        long randB = SECURE_RANDOM.nextLong() & 0x3FFFFFFFFFFFFFFFL;

        long mostSigBits = (timestampMs << 16) | (0x7L << 12) | randA;
        long leastSigBits = (0x2L << 62) | randB;

        return new UUID(mostSigBits, leastSigBits).toString();
    }
}
