package com.unipapers.backend.Modules.FileManagement.Controllers;

import com.unipapers.backend.Modules.FileManagement.Dtos.FileDownloadResponseDto;
import com.unipapers.backend.Modules.FileManagement.Dtos.FileUploadDto;
import com.unipapers.backend.Modules.FileManagement.Services.FileDeleteService;
import com.unipapers.backend.Modules.FileManagement.Services.FileDownloadService;
import com.unipapers.backend.Modules.FileManagement.Services.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileUploadService fileUploadService;
    private final FileDeleteService fileDeleteService;
    private final FileDownloadService fileDownloadService;

    @PostMapping("/init-upload")
    public ResponseEntity<?> initializeUploadFile(@RequestBody FileUploadDto fileUploadDto) throws IOException {
        return ResponseEntity.ok(fileUploadService.initializeUploadFile(fileUploadDto));
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
