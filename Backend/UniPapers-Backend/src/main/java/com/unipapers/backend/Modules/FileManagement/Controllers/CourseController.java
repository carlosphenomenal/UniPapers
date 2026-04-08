package com.unipapers.backend.Modules.FileManagement.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class CourseController {
    private final CourseService courseService;

        return ResponseEntity.ok(courseService.getAllCourses());
    }

    }

    }
}