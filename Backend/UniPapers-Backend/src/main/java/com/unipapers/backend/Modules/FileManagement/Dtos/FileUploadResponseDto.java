package com.unipapers.backend.Modules.FileManagement.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploadResponseDto {

    private String publicId;
    private String signedUrl;
    private Instant expiresAt;

}
