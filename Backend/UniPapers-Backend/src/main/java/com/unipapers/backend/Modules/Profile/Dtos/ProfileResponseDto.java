package com.unipapers.backend.Modules.Profile.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDto {
    private String fullName;
    private String email;
    private String studentNumber;
    private String programme;
    private int yearOfStudy;
    private int semester;
    private int uploadedPastPapersCount;
}
