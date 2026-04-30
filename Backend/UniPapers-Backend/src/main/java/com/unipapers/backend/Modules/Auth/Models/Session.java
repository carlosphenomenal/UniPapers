package com.unipapers.backend.Modules.Auth.Models;

import com.unipapers.backend.Common.Models.User;
import com.unipapers.backend.Modules.Auth.Enums.RevokedReason;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "user_sessions",
        indexes = {
                @Index(name = "idx_sessions_user_id", columnList = "user_id"),
                @Index(name = "idx_sessions_refresh_expiry", columnList = "refresh_token_expires_at"),
                @Index(name = "idx_public_session_id", columnList = "public_session_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "public_session_id", unique = true, nullable = false, length = 32)
    private String publicSessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_token_hash", nullable = false)
    private String refreshTokenHash;

    @Column(name = "refresh_token_expires_at", nullable = false)
    private Instant refreshTokenExpiresAt;

    @Column(name = "device_id", length = 64)
    private String deviceId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "revoked", nullable = false)
    @Builder.Default
    private Boolean revoked = false;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "last_activity_at")
    private Instant lastActivityAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "revoked_reason", length = 50)
    private RevokedReason revokedReason;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.lastActivityAt = Instant.now();
    }

}
