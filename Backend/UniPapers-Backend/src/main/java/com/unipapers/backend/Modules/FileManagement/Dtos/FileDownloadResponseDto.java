package com.unipapers.backend.Modules.FileManagement.Dtos;

import java.time.Instant;

public record FileDownloadResponseDto(
        String key,
        String signedUrl,
        Instant expiresAt
) {
}
