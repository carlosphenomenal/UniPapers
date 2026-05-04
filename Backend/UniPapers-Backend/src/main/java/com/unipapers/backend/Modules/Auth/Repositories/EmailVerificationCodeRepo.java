package com.unipapers.backend.Modules.Auth.Repositories;

import com.unipapers.backend.Common.Models.User;
import com.unipapers.backend.Modules.Auth.Models.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.Instant;

@Repository
public interface EmailVerificationCodeRepo extends JpaRepository<EmailVerificationCode, Long> {
    Optional<EmailVerificationCode> findTopByUserAndCodeOrderByCreatedAtDesc(User user, String code);
    void deleteByUserAndExpiresAtBefore(User user, Instant expiresAt);
    void deleteByExpiresAtBefore(Instant expiresAt);

    @Modifying
    @Query("DELETE FROM EmailVerificationCode e WHERE e.user = :user")
    void deleteAllByUserAndFlush(User user);
}
