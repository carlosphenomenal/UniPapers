package com.unipapers.backend.Modules.FileManagement.Controllers;

import com.unipapers.backend.Modules.FileManagement.Dtos.CourseResponseDto;
import com.unipapers.backend.Modules.FileManagement.Dtos.CreateCourseDto;
import com.unipapers.backend.Modules.FileManagement.Services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("fileManagementCourseController")
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @PostMapping
    public ResponseEntity<CourseResponseDto> createCourse(@RequestBody CreateCourseDto createCourseDto) {
        return ResponseEntity.ok(courseService.createCourse(createCourseDto));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String publicId) {
        courseService.deleteCourse(publicId);
        return ResponseEntity.noContent().build();
    }
}
