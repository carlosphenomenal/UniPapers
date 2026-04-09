package com.unipapers.backend.Modules.FileManagement.Repositories;

import com.unipapers.backend.Modules.FileManagement.Enums.PastPaperType;
import com.unipapers.backend.Modules.FileManagement.Models.PastPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PastPaperRepo extends JpaRepository<PastPaper, Long> {

    Optional<PastPaper> findByPublicId(String publicId);

    @Modifying
    @Query("UPDATE PastPaper p SET p.uploadStatus = 'UPLOADED' WHERE p.publicId = :publicId")
    int markAsUploaded(@Param("publicId") String publicId);

    boolean existsByFileHash(@Param("fileHash") String fileHash);

    @Query("SELECT COUNT(p) FROM PastPaper p " +
            "JOIN p.course c " +
            "WHERE c.courseName = :courseName " +
            "AND p.academicYear = :academicYear " +
            "AND p.type = :pastPaperType " +
            "AND p.yearOfStudy = :yearOfStudy " +
            "AND p.semester = :semester " +
            "AND p.verificationStatus = 'UNVERIFIED'")
    long countUnverifiedByMetadata(
            @Param("courseName") String courseName,
            @Param("academicYear") String academicYear,
            @Param("yearOfStudy") int yearOfStudy,
            @Param("semester") int semester,
            @Param("pastPaperType") PastPaperType pastPaperType
    );
}
