package com.unipapers.backend.Modules.Program.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramResponseDto {
    private String publicId;
    private String programCode;
    private String programName;
    private int durationYears;
}

