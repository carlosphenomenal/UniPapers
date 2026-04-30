package com.unipapers.backend.Utils;

import java.time.LocalDate;

public final class SemesterUtils {
    private SemesterUtils() {
    }

    public static int currentSemester() {
        int month = LocalDate.now().getMonthValue();
        return month >= 8 ? 1 : 2;
    }
}

