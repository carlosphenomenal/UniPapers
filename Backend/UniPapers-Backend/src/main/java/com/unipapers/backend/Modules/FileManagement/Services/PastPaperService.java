package com.unipapers.backend.Modules.FileManagement.Services;

import com.unipapers.backend.Modules.Course.Models.Course;
import com.unipapers.backend.Modules.FileManagement.Dtos.PastPaperResponseDto;
import com.unipapers.backend.Modules.FileManagement.Enums.PastPaperType;
import com.unipapers.backend.Modules.FileManagement.Models.PastPaper;
import com.unipapers.backend.Modules.FileManagement.Repositories.PastPaperRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PastPaperService {
    private final PastPaperRepo pastPaperRepo;

    // TODO: Add cache to this method to improve performance
    // TODO: Refactor this method to use cursor based pagination to improve performance and user experience
    public List<PastPaperResponseDto> getAllPastPapers(String query, String filter) {
        String normalizedQuery = normalizeQuery(query);
        PastPaperType filterType = parseFilter(filter);

        return pastPaperRepo.searchPastPapers(normalizedQuery, filterType != null ? filterType.name() : null).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private String normalizeQuery(String query) {
        if (query == null) {
            return null;
        }

        String trimmed = query.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private PastPaperType parseFilter(String filter) {
        if (filter == null || filter.trim().isEmpty()) {
            return null;
        }

        try {
            return PastPaperType.valueOf(filter.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid filter value. Allowed values are: EXAM, TEST, ASSIGNMENT, NOTES");
        }
    }

    private PastPaperResponseDto mapToDto(PastPaper pastPaper) {
        Course course = pastPaper.getCourse();

        return PastPaperResponseDto.builder()
                .id(pastPaper.getPublicId())
                .academicYear(pastPaper.getAcademicYear())
                .courseCode(course != null ? course.getCourseCode() : null)
                .courseName(course != null ? course.getCourseName() : null)
                .type(pastPaper.getType() != null ? pastPaper.getType().name() : null)
                .build();
    }

}