package com.unipapers.backend.Modules.Notification.Models;

import com.github.f4b6a3.ulid.UlidCreator;
import com.unipapers.backend.Common.Models.User;
import com.unipapers.backend.Modules.Notification.Enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_notifications_user_id",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
            )
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationType notificationType = NotificationType.GENERAL;

    private String title;

    private String message;

    @Builder.Default
    private boolean read = false;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    public void prePersist() {

        this.createdAt = Instant.now();
        if (publicId == null) {
            publicId = UlidCreator.getUlid().toString();
        }

    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

}
