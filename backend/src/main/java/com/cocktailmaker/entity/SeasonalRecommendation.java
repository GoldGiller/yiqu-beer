package com.cocktailmaker.entity;

import com.cocktailmaker.enums.SeasonType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "seasonal_recommendations")
public class SeasonalRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeasonType season;

    private Integer priority = 0;

    private Boolean active = true;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }

    public SeasonalRecommendation() {}

    public SeasonalRecommendation(Long recipeId, SeasonType season, Integer priority) {
        this.recipeId = recipeId;
        this.season = season;
        this.priority = priority;
    }
}
