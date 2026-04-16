package com.unipapers.backend.Modules.Course.Repositories;

import com.unipapers.backend.Modules.Course.Models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepo extends JpaRepository<Course, Long> {
    Optional<Course> findByPublicId(String publicId);
}
