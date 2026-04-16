package com.unipapers.backend.Modules.FileManagement.Models;

import com.github.f4b6a3.ulid.UlidCreator;
import com.unipapers.backend.Common.Models.User;
import com.unipapers.backend.Modules.Course.Models.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "programs", indexes = {
        @Index(name = "idx_program_public_id", columnList = "publicId"),
        @Index(name = "idx_program_name", columnList = "programName")
})
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String publicId;

    @Column(nullable = false, unique = true)
    private String programName; // e.g. "Bachelor of Science in Software Engineering"

    @Column(nullable = false, unique = true)
    private String programCode; // e.g. "BSSE"

    @Column(nullable = false)
    private int durationYears; // e.g., 3 or 4

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Course> courses;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<User> users;

    @PrePersist
    public void generateUlid() {
        if (publicId == null) {
            publicId = UlidCreator.getUlid().toString();
        }
    }
}
