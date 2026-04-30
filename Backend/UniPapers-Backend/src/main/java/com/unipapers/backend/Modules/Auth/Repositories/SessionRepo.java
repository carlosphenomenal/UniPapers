package com.unipapers.backend.Modules.Auth.Repositories;

import com.unipapers.backend.Common.Models.User;
import com.unipapers.backend.Modules.Auth.Models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepo extends JpaRepository<Session, Long> {
    long countByUserAndRevokedFalse(User user);

    Optional<Session> findFirstByUserAndRevokedFalseOrderByCreatedAtAsc(User user);

    void deleteByRefreshTokenExpiresAtBefore(Instant refreshTokenExpiresAtBefore);

    Optional<Session> findByRefreshTokenHashAndRevokedFalse(String tokenHash);

    List<Session> findByUserIdAndRevokedFalse(Long userId);
}
