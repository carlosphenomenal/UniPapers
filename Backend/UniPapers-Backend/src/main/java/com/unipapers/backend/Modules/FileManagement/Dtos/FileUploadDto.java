package com.unipapers.backend.Modules.FileManagement.Dtos;

import com.unipapers.backend.Modules.FileManagement.Enums.PastPaperType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadDto {

    private String courseCode;
    private String courseName;
    private PastPaperType type;
    private String academicYear;
    private int yearOfStudy;
    private int semester;
    private List<String> topicsNames;

}
