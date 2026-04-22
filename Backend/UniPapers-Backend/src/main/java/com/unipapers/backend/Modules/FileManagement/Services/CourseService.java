package com.unipapers.backend.Modules.FileManagement.Services;

import com.unipapers.backend.Exceptions.CustomExceptions.CourseNotFoundException;
import com.unipapers.backend.Modules.Course.Models.Course;
import com.unipapers.backend.Modules.Course.Repositories.CourseRepo;
import com.unipapers.backend.Modules.FileManagement.Dtos.CourseResponseDto;
import com.unipapers.backend.Modules.FileManagement.Dtos.CreateCourseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepo courseRepo;

    public List<CourseResponseDto> getAllCourses() {
        return courseRepo.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public CourseResponseDto createCourse(CreateCourseDto createCourseDto) {
        Course course = Course.builder()
                .courseCode(createCourseDto.getCourseCode())
                .courseName(createCourseDto.getCourseName())
                .build();
        
        Course savedCourse = courseRepo.save(course);
        return mapToResponseDto(savedCourse);
    }

    public void deleteCourse(String publicId) {
        Course course = courseRepo.findByPublicId(publicId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));
        courseRepo.delete(course);
    }

    private CourseResponseDto mapToResponseDto(Course course) {
        return CourseResponseDto.builder()
                .publicId(course.getPublicId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .build();
    }
}
