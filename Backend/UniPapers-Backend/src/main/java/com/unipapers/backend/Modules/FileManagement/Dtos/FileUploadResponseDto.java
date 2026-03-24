package com.unipapers.backend.Modules.FileManagement.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploadResponseDto {

    private String publicId;
    private String signedUrl;

}
