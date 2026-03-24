package com.unipapers.backend.Modules.FileManagement.Repositories;

import com.unipapers.backend.Modules.FileManagement.Models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepo extends JpaRepository<Course, Long> {
    Optional<Course> findByPublicId(String publicId);
}
