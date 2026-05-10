package com.unipapers.backend.Modules.FileManagement.Controllers;

import com.unipapers.backend.Modules.FileManagement.Dtos.FileDownloadResponseDto;
import com.unipapers.backend.Modules.FileManagement.Dtos.FileUploadDto;
import com.unipapers.backend.Modules.FileManagement.Dtos.FileUploadResponseDto;
import com.unipapers.backend.Modules.FileManagement.Services.FileDeleteService;
import com.unipapers.backend.Modules.FileManagement.Services.FileDownloadService;
import com.unipapers.backend.Modules.FileManagement.Services.FileUploadService;
import com.unipapers.backend.Utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileUploadService fileUploadService;
    private final FileDeleteService fileDeleteService;
    private final FileDownloadService fileDownloadService;

    // This endpoint is used to initialize the upload process by creating a database record for the past paper and
    // generating a pre-signed URL for the frontend to upload the file directly to the bucket.
    @PostMapping("/init-upload")
    public ResponseEntity<?> initializeUploadFile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody FileUploadDto fileUploadDto) throws IOException {
        return ResponseEntity.ok(fileUploadService.initializeUploadFile(fileUploadDto, customUserDetails.id()));
    }

    // When the frontend checks the expiration time of the pre-signed url, and it is expired, it will use this
    // endpoint to generate a new pre-signed url for the frontend to upload the file to the bucket.
    @GetMapping("/presign-upload/{pastPaperPublicId}")
    public ResponseEntity<FileUploadResponseDto> presignUploadByPastPaperPublicId(@PathVariable String pastPaperPublicId) {
        return ResponseEntity.ok(fileUploadService.getPresignedUploadByPublicId(pastPaperPublicId));
    }

    // This endpoint can be used to confirm the upload after the frontend has uploaded the file to the bucket using the pre-signed URL.
    // You can use this endpoint to update the database record associated with the past paper, marking it as "uploaded"
    @PutMapping("/confirm-upload/{pastPaperPublicId}")
    public ResponseEntity<?> confirmUpload(@PathVariable String pastPaperPublicId) {
        fileUploadService.markAsUploaded(pastPaperPublicId);
        return ResponseEntity.ok().build();
    }

    // Endpoint to get the presigned download URL for a past paper
    @GetMapping("/presign-download/{pastPaperPublicId}")
    public ResponseEntity<FileDownloadResponseDto> presignDownloadByPastPaperPublicId(
            @PathVariable String pastPaperPublicId
    ) {
        return ResponseEntity.ok(fileDownloadService.getPresignedDownloadByPublicId(pastPaperPublicId));
    }

    // Endpoint to delete a past paper
    @DeleteMapping("/{pastPaperPublicId}")
    public ResponseEntity<Void> delete(@PathVariable String pastPaperPublicId) {
        fileDeleteService.deletePaper(pastPaperPublicId);
        return ResponseEntity.noContent().build();
    }

}
