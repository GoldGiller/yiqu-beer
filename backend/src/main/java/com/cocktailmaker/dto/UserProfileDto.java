package com.cocktailmaker.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserProfileDto {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
    private String bio;
    private String location;
    private Integer recipeCount;
    private Integer favoriteCount;
    private Integer likeCount;
    private Integer followerCount;
    private Integer followingCount;
    private Integer totalViews;
    private Double avgSweetness;
    private Double avgSourness;
    private Double avgAlcohol;
    private Double avgFruitiness;
    private List<RecipeDto> recentRecipes;
    private List<UserActivityDto> recentActivities;
    private LocalDateTime createdAt;
}
