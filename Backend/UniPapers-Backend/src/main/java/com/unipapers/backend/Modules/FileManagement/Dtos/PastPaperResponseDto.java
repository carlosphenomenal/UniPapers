package com.unipapers.backend.Modules.FileManagement.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PastPaperResponseDto {
    private String id;
    private String academicYear;
    private String courseCode;
    private String courseName;
    private String type;
}
