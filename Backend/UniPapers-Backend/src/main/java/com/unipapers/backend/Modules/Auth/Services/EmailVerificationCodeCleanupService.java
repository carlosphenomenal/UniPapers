package com.unipapers.backend.Modules.Auth.Services;

import com.unipapers.backend.Modules.Auth.Repositories.EmailVerificationCodeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EmailVerificationCodeCleanupService {
    private final EmailVerificationCodeRepo emailVerificationCodeRepo;

    @Transactional
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteExpiredVerificationCodes() {
        emailVerificationCodeRepo.deleteByExpiresAtBefore(Instant.now());
    }
}

