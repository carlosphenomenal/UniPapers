package com.unipapers.backend.Modules.FileManagement.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public List<Course> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());

    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.createCourse(course));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();

    }
}