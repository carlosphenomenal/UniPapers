package com.unipapers.backend.Modules.FileManagement.Services;

import com.github.f4b6a3.ulid.UlidCreator;
import com.unipapers.backend.Exceptions.CustomExceptions.CourseNotFoundException;
import com.unipapers.backend.Modules.FileManagement.Dtos.FileUploadDto;
import com.unipapers.backend.Modules.FileManagement.Dtos.FileUploadResponseDto;
import com.unipapers.backend.Modules.FileManagement.Enums.PastPaperType;
import com.unipapers.backend.Modules.FileManagement.Models.Course;
import com.unipapers.backend.Modules.FileManagement.Models.PastPaper;
import com.unipapers.backend.Modules.FileManagement.Models.Topic;
import com.unipapers.backend.Modules.FileManagement.Repositories.CourseRepo;
import com.unipapers.backend.Modules.FileManagement.Repositories.PastPaperRepo;
import com.unipapers.backend.Utils.R2StorageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final CourseRepo courseRepo;
    private final R2StorageService r2StorageService;
    private final PastPaperRepo pastPaperRepo;

    @Transactional
    public FileUploadResponseDto initializeUploadFile(FileUploadDto fileUploadDto) throws BadRequestException {

        //Fetch the associated course
        Course course = courseRepo.findByPublicId(fileUploadDto.getCoursePublicId())
                .orElseThrow(() -> new CourseNotFoundException("Course for id " + fileUploadDto.getCoursePublicId() + " not found"));


        if (fileUploadDto.getFileName() == null || fileUploadDto.getFileName().isEmpty()) {
            throw new BadRequestException("File name is required");
        }
        if(fileUploadDto.getAcademicYear() == null || fileUploadDto.getAcademicYear().isEmpty()){
            throw new BadRequestException("Academic year is required");
        }

        // Generate object key to be used to store the file in the bucket
        String key = generateKey(
                fileUploadDto.getFileName(),
                course.getCourseName(),
                fileUploadDto.getAcademicYear()
        );

        // Handle past paper type
        PastPaperType type;
        try {
            type = PastPaperType.valueOf(fileUploadDto.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid past paper type: " + fileUploadDto.getType());
        }


        // Create a past paper object
        PastPaper pastPaper = PastPaper.builder()
                .course(course)
                .type(type)
                .academicYear(fileUploadDto.getAcademicYear())
                .yearOfStudy(fileUploadDto.getYearOfStudy())
                .semester(fileUploadDto.getSemester())
                .fileBucketName(key)
                .build();

        // Save the past paper to the db
        PastPaper savedPastPaper = pastPaperRepo.save(pastPaper);

        // Create topics
        List<Topic> topics = new ArrayList<>();

        for (String topicName : fileUploadDto.getTopicsNames()){
            // Create a topic object for each entity
            Topic topic = Topic.builder()
                    .course(course)
                    .pastPaper(savedPastPaper)
                    .topicName(topicName)
                    .build();
            // Add the topic to the list of the lists created
            topics.add(topic);
        }

        // Map the topics to the past paper and save the paper to db
        savedPastPaper.setTopics(topics);
        pastPaperRepo.save(savedPastPaper);

        // Create the signed URL for the client to upload the file directly to the bucket
        String signedUrl = r2StorageService.presignedUploadUrl(
                key,
                "application/pdf",  //set content type to PDF since we only accept PDF files
                Duration.ofMinutes(5)          // 5 minutes expiry
        );

        return FileUploadResponseDto.builder()
                .publicId(savedPastPaper.getPublicId())
                .signedUrl(signedUrl)
                .build();

    }

    public void markAsUploaded(String pastPaperPublicId){

        int updatedCount = pastPaperRepo.markAsUploaded(pastPaperPublicId);

        if (updatedCount == 0) {
            throw new EntityNotFoundException("PastPaper not found with publicId: " + pastPaperPublicId);
        }

    }

    //======== HELPER METHODS =======//
    public static String generateKey(
            String originalFilename,
            String course,
            String year
    ) {
        // Handle nullable parameters
        String safeYear = year != null ? normalize(year) : "unknown-year";

        String extension = extractExtension(originalFilename);
        String safeName = sanitizeFilename(originalFilename);
        String id = generateId(); // UUID or ULID

        return String.format(
                "past-papers/%s/%s/%s-%s%s",
                course,
                safeYear,
                id,
                safeName,
                extension
        );
    }

    private static String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    private static String sanitizeFilename(String filename) {
        if (filename == null) return "file";

        // Remove extension
        String name = filename.replaceAll("\\.[^.]+$", "");

        // Normalize (remove accents)
        name = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        // Replace unwanted chars
        name = name.replaceAll("[^a-zA-Z0-9]", "-")
                .replaceAll("-{2,}", "-")
                .toLowerCase(Locale.ROOT);

        // Trim length
        return name.length() > 50 ? name.substring(0, 50) : name;
    }

    private static String normalize(String input) {
        if (input == null) return "unknown";

        return input.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "-");
    }

    private static String generateId() {
        // Generate ULID
        return UlidCreator.getUlid().toString();
    }

}
