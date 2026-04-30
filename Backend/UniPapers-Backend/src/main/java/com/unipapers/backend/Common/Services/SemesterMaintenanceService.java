package com.unipapers.backend.Common.Services;

import com.unipapers.backend.Common.Repositories.UserRepo;
import com.unipapers.backend.Utils.SemesterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SemesterMaintenanceService {
    private final UserRepo userRepo;

    @Transactional
    @Scheduled(cron = "0 0 3 1 * *")
    public void updateSemesterForAllUsers() {
        int semester = SemesterUtils.currentSemester();
        userRepo.updateAllSemesters(semester);
    }
}

