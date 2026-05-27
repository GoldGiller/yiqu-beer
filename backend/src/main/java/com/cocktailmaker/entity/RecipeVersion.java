package com.cocktailmaker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "recipe_versions")
public class RecipeVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "snapshot_data", nullable = false, columnDefinition = "JSON")
    private String snapshotData;

    @Column(name = "change_summary", columnDefinition = "TEXT")
    private String changeSummary;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Transient
    private String creatorName;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }

    public RecipeVersion() {}

    public RecipeVersion(Long recipeId, Integer versionNumber, String snapshotData, Long createdBy, String changeSummary) {
        this.recipeId = recipeId;
        this.versionNumber = versionNumber;
        this.snapshotData = snapshotData;
        this.createdBy = createdBy;
        this.changeSummary = changeSummary;
    }
}
