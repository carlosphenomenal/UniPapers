package com.unipapers.backend.Modules.FileManagement.Repositories;

import com.unipapers.backend.Modules.FileManagement.Models.PastPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PastPaperRepo extends JpaRepository<PastPaper, Long> {

    @Modifying
    @Query("UPDATE PastPaper p SET p.uploadStatus = 'UPLOADED' WHERE p.publicId = :publicId")
    int markAsUploaded(@Param("publicId") String publicId);
}
