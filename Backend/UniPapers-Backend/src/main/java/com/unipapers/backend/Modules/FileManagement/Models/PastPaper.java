package com.unipapers.backend.Modules.FileManagement.Models;

import com.github.f4b6a3.ulid.UlidCreator;
import com.unipapers.backend.Modules.FileManagement.Enums.PastPaperType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "past_papers")
public class PastPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    private PastPaperType type;

    private String academicYear;

    private int yearOfStudy;

    private int semester;

    private String fileUrl;

    @OneToMany(mappedBy = "pastPaper", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Topic> topics;

    private Instant addedAt;

    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        // Generate a public ID if not provided
        if (publicId == null) {
            publicId = UlidCreator.getUlid().toString();
        }
        // Validate semester
        if (semester != 1 && semester != 2) {
            throw new IllegalArgumentException("Semester must be 1 or 2");
        }
        // Validate year of study
        if (yearOfStudy < 1 || yearOfStudy > 5) {
            throw new IllegalArgumentException("Year of study must be between 1 and 5");
        }
        // Set the addedAt and updatedAt timestamps
        addedAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

}
