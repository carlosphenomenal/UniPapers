package com.unipapers.backend.Modules.FileManagement.Controllers;

import com.unipapers.backend.Configurations.Cloudflare.R2Properties;
import com.unipapers.backend.Modules.FileManagement.Dtos.FileUploadDto;
import com.unipapers.backend.Modules.FileManagement.Services.FileUploadService;
import com.unipapers.backend.Utils.R2StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final R2StorageService storageService;
    private final R2Properties props;
    private final FileUploadService fileUploadService;

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

    @PostMapping("/presign-upload")
    public ResponseEntity<Map<String, String>> presignUpload(
            @RequestParam String filename,
            @RequestParam String contentType) {

        String key = UUID.randomUUID() + "_" + filename;
        String uploadUrl = storageService.presignedUploadUrl(key, contentType, Duration.ofMinutes(15));
        String publicUrl = props.getPublicUrl() + "/" + key;

        return ResponseEntity.ok(Map.of(
                "key", key,
                "uploadUrl", uploadUrl,   // frontend PUTs directly to this
                "publicUrl", publicUrl    // final URL to store in your DB
        ));
    }

    @GetMapping("/download/{key}")
    public ResponseEntity<byte[]> download(@PathVariable String key) {
        byte[] data = storageService.download(key);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                .body(data);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        storageService.delete(key);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/presign/{key}")
    public ResponseEntity<String> presign(@PathVariable String key) {
        URL url = storageService.presignedUrl(key, Duration.ofMinutes(15));
        return ResponseEntity.ok(url.toString());
    }
}
