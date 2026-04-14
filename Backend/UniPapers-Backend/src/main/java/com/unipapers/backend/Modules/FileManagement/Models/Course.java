package com.unipapers.backend.Modules.FileManagement.Models;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "courses", indexes = {
        @Index(name = "idx_course_public_id", columnList = "publicId"),
        @Index(name = "idx_course_code", columnList = "courseCode"),
        @Index(name = "idx_course_name", columnList = "courseName")
})
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String publicId;

    @Column(nullable = false, unique = true)
    private String courseCode;

    @Column(nullable = false, unique = true)
    private String courseName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PastPaper> pastPaper;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Topic> topics;

    @PrePersist
    public void generateUlid() {
        if (publicId == null) {
            publicId = UlidCreator.getUlid().toString();
        }
    }

}
