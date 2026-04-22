package com.unipapers.backend.Modules.FileManagement.Repositories;

import com.unipapers.backend.Modules.FileManagement.Enums.PastPaperType;
import com.unipapers.backend.Modules.FileManagement.Enums.VerificationStatus;
import com.unipapers.backend.Modules.FileManagement.Models.PastPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PastPaperRepo extends JpaRepository<PastPaper, Long> {

    java.util.List<PastPaper> findTop10ByVerificationStatusOrderByAddedAtDesc(VerificationStatus verificationStatus);

    Optional<PastPaper> findByPublicId(String publicId);

    @Query(value = "SELECT p.* FROM past_papers p " +
            "JOIN courses c ON c.id = p.course_id " +
            "WHERE (:query IS NULL OR " +
            "LOWER(c.course_code) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.course_name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.key) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.academic_year) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:type IS NULL OR p.type = :type) " +
            "ORDER BY p.added_at DESC",
            nativeQuery = true)
    List<PastPaper> searchPastPapers(
            @Param("query") String query,
            @Param("type") String type
    );

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
