package com.unipapers.backend.Modules.FileManagement.Dtos;

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

    private String coursePublicId;
    private String courseName;
    private String fileName;
    private String fileHash;
    private String pastPaperType;
    private String academicYear;
    private int yearOfStudy;
    private int semester;
    private List<String> topicsNames;

}
