package com.unipapers.backend.Modules.FileManagement.Services;

import com.unipapers.backend.Exceptions.CustomExceptions.PastPaperNotFoundException;
import com.unipapers.backend.Modules.FileManagement.Models.PastPaper;
import com.unipapers.backend.Modules.FileManagement.Repositories.PastPaperRepo;
import com.unipapers.backend.Utils.R2StorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileDeleteService {

    private final PastPaperRepo pastPaperRepo;
    private final R2StorageService r2StorageService;

    @Transactional
    public void deletePaper(String pastPaperPublicId) {
        PastPaper pastPaper = pastPaperRepo.findByPublicId(pastPaperPublicId)
                .orElseThrow(() -> new PastPaperNotFoundException("PastPaper not found with publicId: " + pastPaperPublicId));

        String key = pastPaper.getKey();
        if (key == null || key.isBlank()) {
            throw new PastPaperNotFoundException("File key not found for pastPaper publicId: " + pastPaperPublicId);
        }

        r2StorageService.delete(key);
        pastPaperRepo.delete(pastPaper);
    }
}
