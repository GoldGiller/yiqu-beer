package com.cocktailmaker.entity;

import com.cocktailmaker.enums.ActivityType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_activities")
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;

    @Column(name = "target_type", nullable = false)
    private String targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "JSON")
    private String metadata;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }

    public UserActivity() {}

    public UserActivity(Long userId, ActivityType type, String targetType, Long targetId, String summary) {
        this.userId = userId;
        this.activityType = type;
        this.targetType = targetType;
        this.targetId = targetId;
        this.summary = summary;
    }
}
