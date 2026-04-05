package com.unipapers.backend.Modules.FileManagement.Dtos;

public record FileDownloadResponseDto(
        String key,
        String signedUrl
) {
}
