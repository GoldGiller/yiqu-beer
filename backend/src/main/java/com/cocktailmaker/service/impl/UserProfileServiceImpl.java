package com.cocktailmaker.service.impl;

import com.cocktailmaker.dto.*;
import com.cocktailmaker.entity.*;
import com.cocktailmaker.repository.*;
import com.cocktailmaker.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserActivityRepository activityRepository;

    @Override
    public ApiResponse<UserProfileDto> getUserProfile(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return ApiResponse.error("用户不存在");

            UserProfileDto dto = new UserProfileDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
            dto.setEmail(user.getEmail());
            dto.setAvatar(user.getAvatar());
            dto.setCreatedAt(user.getCreatedAt());

            if (user.getProfile() != null) {
                dto.setBio(user.getProfile().getBio());
                dto.setLocation(user.getProfile().getLocation());
            }

            // 统计
            List<Recipe> userRecipes = recipeRepository.findByUser(user, Pageable.unpaged()).getContent();
            dto.setRecipeCount(userRecipes.size());
            dto.setTotalViews(userRecipes.stream().mapToInt(r -> r.getViewCount() != null ? r.getViewCount() : 0).sum());

            // 风味平均值
            if (!userRecipes.isEmpty()) {
                dto.setAvgSweetness(userRecipes.stream().mapToDouble(r -> r.getSweetness() != null ? r.getSweetness() : 5).average().orElse(5));
                dto.setAvgSourness(userRecipes.stream().mapToDouble(r -> r.getSourness() != null ? r.getSourness() : 5).average().orElse(5));
                dto.setAvgAlcohol(userRecipes.stream().mapToDouble(r -> r.getAlcohol() != null ? r.getAlcohol() : 5).average().orElse(5));
                dto.setAvgFruitiness(userRecipes.stream().mapToDouble(r -> r.getFruitiness() != null ? r.getFruitiness() : 5).average().orElse(5));
            }

            // 最近配方
            dto.setRecentRecipes(userRecipes.stream()
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .limit(6)
                    .map(this::convertRecipeToDto)
                    .collect(Collectors.toList()));

            // 最近活动
            List<UserActivity> activities = activityRepository.findTop20ByUserIdOrderByCreatedAtDesc(userId);
            dto.setRecentActivities(activities.stream()
                    .map(this::convertActivityToDto)
                    .collect(Collectors.toList()));

            return ApiResponse.success(dto);
        } catch (Exception e) {
            return ApiResponse.error("获取用户信息失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<Page<UserActivityDto>> getUserActivities(Long userId, int page, int size) {
        try {
            Page<UserActivity> activities = activityRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
            Page<UserActivityDto> dtos = activities.map(this::convertActivityToDto);
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error("获取活动记录失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<FlavorProfileDto> getFlavorProfile(Long userId) {
        try {
            List<Recipe> recipes = recipeRepository.findByUser(
                    userRepository.findById(userId).orElseThrow(), Pageable.unpaged()).getContent();

            FlavorProfileDto dto = new FlavorProfileDto();
            dto.setUserId(userId);
            dto.setTotalRecipesCreated(recipes.size());

            if (recipes.isEmpty()) {
                dto.setAvgSweetness(0.0);
                dto.setAvgSourness(0.0);
                dto.setAvgAlcohol(0.0);
                dto.setAvgFruitiness(0.0);
                dto.setDominantFlavor("平衡");
            } else {
                dto.setAvgSweetness(Math.round(recipes.stream().mapToDouble(r -> r.getSweetness() != null ? r.getSweetness() : 5).average().orElse(5) * 10.0) / 10.0);
                dto.setAvgSourness(Math.round(recipes.stream().mapToDouble(r -> r.getSourness() != null ? r.getSourness() : 5).average().orElse(5) * 10.0) / 10.0);
                dto.setAvgAlcohol(Math.round(recipes.stream().mapToDouble(r -> r.getAlcohol() != null ? r.getAlcohol() : 5).average().orElse(5) * 10.0) / 10.0);
                dto.setAvgFruitiness(Math.round(recipes.stream().mapToDouble(r -> r.getFruitiness() != null ? r.getFruitiness() : 5).average().orElse(5) * 10.0) / 10.0);

                double max = Math.max(Math.max(dto.getAvgSweetness(), dto.getAvgSourness()),
                        Math.max(dto.getAvgAlcohol(), dto.getAvgFruitiness()));
                if (max == dto.getAvgSweetness()) dto.setDominantFlavor("甜味主导");
                else if (max == dto.getAvgSourness()) dto.setDominantFlavor("酸味主导");
                else if (max == dto.getAvgAlcohol()) dto.setDominantFlavor("烈酒主导");
                else dto.setDominantFlavor("果味主导");
            }

            Map<String, Double> dist = new LinkedHashMap<>();
            dist.put("甜度", dto.getAvgSweetness());
            dist.put("酸度", dto.getAvgSourness());
            dist.put("酒精度", dto.getAvgAlcohol());
            dist.put("果味", dto.getAvgFruitiness());
            dto.setFlavorDistribution(dist);

            return ApiResponse.success(dto);
        } catch (Exception e) {
            return ApiResponse.error("获取风味画像失败: " + e.getMessage());
        }
    }

    private RecipeDto convertRecipeToDto(Recipe recipe) {
        RecipeDto dto = new RecipeDto();
        dto.setId(recipe.getId());
        dto.setName(recipe.getName());
        dto.setImageUrl(recipe.getImageUrl());
        dto.setMood(recipe.getMood());
        dto.setSweetness(recipe.getSweetness());
        dto.setSourness(recipe.getSourness());
        dto.setAlcohol(recipe.getAlcohol());
        dto.setFruitiness(recipe.getFruitiness());
        dto.setLikeCount(recipe.getLikeCount());
        dto.setViewCount(recipe.getViewCount());
        dto.setCreatedAt(recipe.getCreatedAt());
        return dto;
    }

    private UserActivityDto convertActivityToDto(UserActivity activity) {
        UserActivityDto dto = new UserActivityDto();
        dto.setId(activity.getId());
        dto.setUserId(activity.getUserId());
        dto.setActivityType(activity.getActivityType().name());
        dto.setTargetType(activity.getTargetType());
        dto.setTargetId(activity.getTargetId());
        dto.setSummary(activity.getSummary());
        dto.setCreatedAt(activity.getCreatedAt());
        dto.setActivityIcon(getActivityIcon(activity.getActivityType().name()));
        return dto;
    }

    private String getActivityIcon(String type) {
        switch (type) {
            case "CREATE_RECIPE": return "🍸";
            case "LIKE_RECIPE": return "❤️";
            case "FAVORITE_RECIPE": return "⭐";
            case "COMMENT_RECIPE": return "💬";
            case "RATE_RECIPE": return "🌟";
            case "FOLLOW_USER": return "👤";
            case "VERSION_RESTORE": return "🔄";
            default: return "📌";
        }
    }
}
