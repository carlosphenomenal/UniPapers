package com.unipapers.backend.Common.Models;

import com.github.f4b6a3.ulid.UlidCreator;
import com.unipapers.backend.Common.Enums.Role;
import com.unipapers.backend.Modules.Auth.Models.EmailVerificationCode;
import com.unipapers.backend.Modules.Auth.Models.Session;
import com.unipapers.backend.Modules.FileManagement.Models.PastPaper;
import com.unipapers.backend.Modules.Program.Models.Program;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String publicId;

    private String firstName;

    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Builder.Default
    private boolean isEmailVerified = false;

    @Column(nullable = false, unique = true)
    private Long studentNumber;

    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    private Integer yearOfStudy;

    private Integer semester;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    foreignKey = @ForeignKey(
                            name = "fk_user_roles_user_id",
                            foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                    )
            )
    )
    @Column(name = "role")
    private Set<Role> roles = Set.of(Role.USER);

    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PastPaper> pastPapers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Session> sessions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<EmailVerificationCode> emailVerificationCodes;

    private Instant addedAt;
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        // Generate a public ID if not provided
        if (publicId == null) {
            publicId = UlidCreator.getUlid().toString();
        }
        // Trim string fields
        if (email != null) {
            email = email.trim().toLowerCase();
        }
        if (firstName != null) {
            firstName = firstName.trim();
        }
        if (lastName != null) {
            lastName = lastName.trim();
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

        // Set the default role to USER if not provided
        if(roles.isEmpty()){
            roles.add(Role.USER);
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

}
