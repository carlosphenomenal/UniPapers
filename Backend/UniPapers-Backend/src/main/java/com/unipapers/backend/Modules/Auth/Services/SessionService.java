package com.unipapers.backend.Modules.Auth.Services;

import com.unipapers.backend.Common.Models.User;
import com.unipapers.backend.Common.Repositories.UserRepo;
import com.unipapers.backend.Configurations.Security.JwtService;
import com.unipapers.backend.Exceptions.CustomExceptions.InvalidRefreshTokenException;
import com.unipapers.backend.Modules.Auth.Dtos.LoginResponseDto;
import com.unipapers.backend.Modules.Auth.Enums.RevokedReason;
import com.unipapers.backend.Modules.Auth.Models.Session;
import com.unipapers.backend.Modules.Auth.Repositories.SessionRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionService {

    private final JwtService jwtService;
    private final SessionRepo sessionRepo;
    private final UserRepo userRepo;

    public LoginResponseDto refreshSession(String refreshToken) {

        // Parse the refresh token JWT
        Claims claims;
        try {
            claims = jwtService.extractClaims(refreshToken);
        } catch (ExpiredJwtException ex) {
            log.warn("Refresh token expired for subject: {}", ex.getClaims().getSubject());
            throw new InvalidRefreshTokenException("Refresh token has expired. Please log in again.");
        } catch (JwtException ex) {
            log.warn("Invalid refresh token JWT: {}", ex.getMessage());
            throw new InvalidRefreshTokenException("Invalid refresh token.");
        }

        long studentNumber = Long.parseLong(claims.getSubject());

        // Load the user
        User user = userRepo.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new InvalidRefreshTokenException("User not found."));

        // Hash the token and look up the active session
        String tokenHash = hashToken(refreshToken);
        Optional<Session> sessionOpt = sessionRepo.findByRefreshTokenHashAndRevokedFalse(tokenHash);

        if (sessionOpt.isEmpty()) {
            // The hash doesn't match any active session — token reuse detected.
            // Revoke ALL active sessions for this user to force re-login everywhere.
            log.warn("Refresh token reuse detected for user {}. Revoking all active sessions.", studentNumber);
            revokeAllUserSessions(user, RevokedReason.TOKEN_REUSE);
            throw new InvalidRefreshTokenException("Refresh token has already been used. All sessions have been revoked for security. Please log in again.");
        }

        Session session = sessionOpt.get();

        // Verify the session's refresh expiry hasn't passed
        if (session.getRefreshTokenExpiresAt().isBefore(Instant.now())) {
            log.info("Session {} refresh expiry passed for user {}", session.getPublicSessionId(), studentNumber);
            session.setRevoked(true);
            session.setRevokedAt(Instant.now());
            session.setRevokedReason(RevokedReason.TOKEN_EXPIRED);
            sessionRepo.save(session);
            throw new InvalidRefreshTokenException("Session has expired. Please log in again.");
        }


        // Generate new refresh token
        String newRefreshToken = jwtService.generateRefreshToken(Long.toString(studentNumber));

        // Update the session with the new hash
        session.setRefreshTokenHash(hashToken(newRefreshToken));
        session.setLastActivityAt(Instant.now());
        sessionRepo.save(session);

        // Generate new access token
        Map<String, Object> accessTokenClaims = buildAccessTokenClaims(user);
        String newAccessToken = jwtService.generateAccessToken(Long.toString(user.getStudentNumber()), accessTokenClaims);

        log.info("Rotated refresh token for user {}, sessionId={}", studentNumber, session.getPublicSessionId());

        return new LoginResponseDto(
                user.getPublicId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                newAccessToken,
                newRefreshToken,
                session.getDeviceId(),
                user.isEmailVerified(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toList())
        );
    }


    /**
     * Builds the extra claims map included in the access token payload.
     */
    private Map<String, Object> buildAccessTokenClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("publicId", user.getPublicId());
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
        return claims;
    }


    /**
     * Produces an SHA-256 hex digest of the given token.
     */
    public static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Revokes all active (non-revoked) sessions for a given user.
     * Used when token reuse is detected to protect the user's account.
     */
    private void revokeAllUserSessions(User user, RevokedReason reason) {
        List<Session> activeSessions = sessionRepo.findByUserIdAndRevokedFalse(user.getId());
        Instant now = Instant.now();
        activeSessions.forEach(s -> {
            s.setRevoked(true);
            s.setRevokedAt(now);
            s.setRevokedReason(reason);
        });
        sessionRepo.saveAll(activeSessions);
        log.info("Revoked {} active session(s) for user {} due to {}", activeSessions.size(), user.getStudentNumber(), reason);
    }


    public void updateFcmToken(String deviceId, String fcmToken) {
        Session session = sessionRepo.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalStateException("Session not found for device ID: " + deviceId));
        session.setFcmToken(fcmToken);
        sessionRepo.save(session);
    }
}
