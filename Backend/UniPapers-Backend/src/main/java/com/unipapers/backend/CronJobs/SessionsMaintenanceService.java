package com.unipapers.backend.CronJobs;

import com.unipapers.backend.Modules.Auth.Repositories.SessionRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class SessionsMaintenanceService {

    private final SessionRepo sessionRepo;

    public SessionsMaintenanceService(SessionRepo sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    @Scheduled(cron = "0 0 4 * * *") // Every day at 4 AM
    public void deleteOldSessions(){
        Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS);

        sessionRepo.deleteByRefreshTokenExpiresAtBefore(cutoff);
    }
}
