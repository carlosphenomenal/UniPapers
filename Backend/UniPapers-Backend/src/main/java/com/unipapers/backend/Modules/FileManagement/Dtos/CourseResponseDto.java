package com.unipapers.backend.Modules.FileManagement.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDto {
    private String publicId;
    private String courseCode;
    private String courseName;
    private Instant createdAt;
    private Instant updatedAt;
}
