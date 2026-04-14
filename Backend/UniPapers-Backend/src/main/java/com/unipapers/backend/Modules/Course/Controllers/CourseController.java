package com.unipapers.backend.Modules.Course.Controllers;

import com.unipapers.backend.Modules.Course.Dtos.CourseResponseDto;
import com.unipapers.backend.Modules.Course.Dtos.CreateCourseDto;
import com.unipapers.backend.Modules.Course.Services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/get")
    public ResponseEntity<List<CourseResponseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @PostMapping("/add")
    public ResponseEntity<CourseResponseDto> createCourse(@RequestBody CreateCourseDto createCourseDto) {
        return ResponseEntity.ok(courseService.createCourse(createCourseDto));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String publicId) {
        courseService.deleteCourse(publicId);
        return ResponseEntity.ok().build();
    }
}
