package com.cocktailmaker.dto;

import com.cocktailmaker.enums.MoodType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 配方数据传输对象
 */
@Data

public class RecipeDto {
    
    private Long id;
    private String name;
    private String description;
    private UserDto user;
    private MoodType mood;
    private String imageUrl;
    private Integer sweetness;
    private Integer sourness;
    private Integer alcohol;
    private Integer fruitiness;
    private Boolean isPublic;
    private Integer viewCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer commentCount;
    private Double averageRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RecipeIngredientDto> ingredients;
    private List<RecipeStepDto> steps;
    private List<TagDto> tags;
    private Boolean isLiked;
    private Boolean isFavorited;
    private Integer userRating;
    
    // Constructors
    public RecipeDto() {
    }
    

    
    /**
     * 配方材料DTO
     */
    public static class RecipeIngredientDto {
        private Long id;
        private IngredientDto ingredient;
        private String amount;
        private String unit;
        private Integer orderNum;
        private String notes;
    }

    
    /**
     * 配方步骤DTO
     */
    public static class RecipeStepDto {
        private Long id;
        private Integer stepNumber;
        private String description;
        private String imageUrl;
        private Integer durationSeconds;
        private String temperature;
        private String technique;
    }

    
    /**
     * 材料DTO
     */
    public static class IngredientDto {
        private Long id;
        private String name;
        private String type;
        private String emoji;
        private String description;
        private Double alcoholContent;
    }

    
    /**
     * 标签DTO
     */
    public static class TagDto {
        private Long id;
        private String name;
        private String description;
        private String color;

    }
}