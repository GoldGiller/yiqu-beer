package com.cocktailmaker.service.impl;

import com.cocktailmaker.dto.*;
import com.cocktailmaker.entity.*;
import com.cocktailmaker.enums.ActivityType;
import com.cocktailmaker.enums.SeasonType;
import com.cocktailmaker.repository.*;
import com.cocktailmaker.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private FoodPairingRepository foodPairingRepository;
    @Autowired
    private RecipeFoodPairingRepository recipeFoodPairingRepository;
    @Autowired
    private SeasonalRecommendationRepository seasonalRepository;
    @Autowired
    private UserActivityRepository activityRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public ApiResponse<RecommendDto> getCollaborativeRecommendations(Long userId, int limit) {
        try {
            RecommendDto dto = new RecommendDto();

            // Item-based CF: 找到用户喜欢/收藏的配方,推荐类似的其他热门配方
            List<UserActivity> userActs = activityRepository.findTop20ByUserIdOrderByCreatedAtDesc(userId);

            Set<Long> likedRecipeIds = userActs.stream()
                    .filter(a -> "LIKE_RECIPE".equals(a.getActivityType().name()) || "FAVORITE_RECIPE".equals(a.getActivityType().name()))
                    .map(UserActivity::getTargetId)
                    .collect(Collectors.toSet());

            Set<Long> interactedIds = userActs.stream()
                    .map(UserActivity::getTargetId)
                    .collect(Collectors.toSet());

            // 推荐高分用户未交互的配方
            List<Recipe> candidates = recipeRepository.findPopularRecipes(PageRequest.of(0, limit * 3))
                    .getContent().stream()
                    .filter(r -> !interactedIds.contains(r.getId()))
                    .collect(Collectors.toList());

            List<RecipeDto> recommended = candidates.stream()
                    .limit(limit)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            dto.setRecipes(recommended);
            dto.setReason(likedRecipeIds.isEmpty() ? "为你精选的高分配方" : "根据你的喜好推荐");
            return ApiResponse.success(dto);
        } catch (Exception e) {
            return ApiResponse.error("获取推荐失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<RecommendDto> getSeasonalRecommendations(int limit) {
        try {
            RecommendDto dto = new RecommendDto();
            SeasonType season = getCurrentSeason();
            dto.setSeason(season.name());
            dto.setSeasonEmoji(getSeasonEmoji(season));

            List<SeasonalRecommendation> seasonal = seasonalRepository
                    .findBySeasonAndActiveTrueOrderByPriorityDesc(season);

            List<RecipeDto> recipes = new ArrayList<>();
            for (SeasonalRecommendation sr : seasonal) {
                if (recipes.size() >= limit) break;
                recipeRepository.findById(sr.getRecipeId()).ifPresent(r -> recipes.add(convertToDto(r)));
            }

            // 不足时用热门补充
            if (recipes.size() < limit) {
                recipeRepository.findPopularRecipes(PageRequest.of(0, limit - recipes.size()))
                        .getContent().stream()
                        .filter(r -> recipes.stream().noneMatch(existing -> existing.getId().equals(r.getId())))
                        .map(this::convertToDto)
                        .forEach(recipes::add);
            }

            dto.setRecipes(recipes);
            dto.setReason(getSeasonalDescription(season));
            return ApiResponse.success(dto);
        } catch (Exception e) {
            return ApiResponse.error("获取季节推荐失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<FoodPairingDto>> getFoodPairings(Long recipeId) {
        try {
            List<RecipeFoodPairing> pairings = recipeFoodPairingRepository.findByRecipeId(recipeId);
            List<FoodPairingDto> dtos = new ArrayList<>();

            if (pairings.isEmpty()) {
                // 根据配方的口味推荐搭配
                Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
                if (recipe != null) {
                    return ApiResponse.success(getAutoPairings(recipe));
                }
            }

            for (RecipeFoodPairing rfp : pairings) {
                foodPairingRepository.findById(rfp.getFoodPairingId()).ifPresent(fp -> {
                    FoodPairingDto dto = new FoodPairingDto();
                    dto.setId(fp.getId());
                    dto.setName(fp.getName());
                    dto.setEmoji(fp.getEmoji());
                    dto.setDescription(fp.getDescription());
                    dto.setCategory(fp.getCategory());
                    dtos.add(dto);
                });
            }

            if (dtos.isEmpty() && recipeRepository.findById(recipeId).isPresent()) {
                return ApiResponse.success(getAutoPairings(recipeRepository.findById(recipeId).get()));
            }

            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error("获取搭配失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<Void> recordInteraction(Long userId, Long recipeId, String interactionType) {
        try {
            UserActivity activity = new UserActivity();
            activity.setUserId(userId);
            activity.setActivityType(ActivityType.valueOf(interactionType));
            activity.setTargetType("RECIPE");
            activity.setTargetId(recipeId);
            activity.setSummary(interactionType + " recipe #" + recipeId);
            activityRepository.save(activity);
            return ApiResponse.success("记录成功", null);
        } catch (Exception e) {
            return ApiResponse.error("记录失败: " + e.getMessage());
        }
    }

    private SeasonType getCurrentSeason() {
        Month month = LocalDate.now().getMonth();
        if (month == Month.DECEMBER) return SeasonType.CHRISTMAS;
        if (month == Month.FEBRUARY && LocalDate.now().getDayOfMonth() <= 14) return SeasonType.VALENTINE;
        if (month == Month.OCTOBER) return SeasonType.HALLOWEEN;
        if (month == Month.JANUARY || month == Month.FEBRUARY) return SeasonType.WINTER;
        if (month.getValue() >= 3 && month.getValue() <= 5) return SeasonType.SPRING;
        if (month.getValue() >= 6 && month.getValue() <= 8) return SeasonType.SUMMER;
        return SeasonType.AUTUMN;
    }

    private String getSeasonEmoji(SeasonType season) {
        switch (season) {
            case SPRING: return "🌸";
            case SUMMER: return "☀️";
            case AUTUMN: return "🍂";
            case WINTER: return "❄️";
            case CHRISTMAS: return "🎄";
            case VALENTINE: return "💝";
            case HALLOWEEN: return "🎃";
            case NEW_YEAR: return "🎉";
            case MOON_FESTIVAL: return "🌕";
            default: return "🍸";
        }
    }

    private String getSeasonalDescription(SeasonType season) {
        switch (season) {
            case SUMMER: return "炎炎夏日，来杯清爽的鸡尾酒解暑吧！";
            case WINTER: return "寒冷冬日，温暖的热饮鸡尾酒正当时";
            case SPRING: return "春暖花开，清新的花香鸡尾酒唤醒味蕾";
            case CHRISTMAS: return "圣诞佳节，蛋奶酒和热红酒的浪漫时光";
            case VALENTINE: return "浪漫情人节，草莓和玫瑰味的鸡尾酒甜蜜满分";
            default: return "应季精选，品味当季最佳鸡尾酒";
        }
    }

    private List<FoodPairingDto> getAutoPairings(Recipe recipe) {
        List<FoodPairing> all = foodPairingRepository.findAll();
        List<FoodPairingDto> result = new ArrayList<>();

        double sweetness = recipe.getSweetness() != null ? recipe.getSweetness() : 5;
        double alcohol = recipe.getAlcohol() != null ? recipe.getAlcohol() : 5;

        // 根据甜度和酒精度智能匹配
        for (FoodPairing fp : all) {
            boolean match = false;
            if (sweetness >= 7 && ("甜点".equals(fp.getCategory()))) match = true;
            if (alcohol >= 7 && ("肉类".equals(fp.getCategory()) || "海鲜".equals(fp.getCategory()))) match = true;
            if (sweetness <= 4 && ("小吃".equals(fp.getCategory()) || "沙拉".equals(fp.getCategory()))) match = true;

            if (match || result.size() < 3) {
                FoodPairingDto dto = new FoodPairingDto();
                dto.setId(fp.getId());
                dto.setName(fp.getName());
                dto.setEmoji(fp.getEmoji());
                dto.setDescription(fp.getDescription());
                dto.setCategory(fp.getCategory());
                result.add(dto);
            }
            if (result.size() >= 4) break;
        }

        return result;
    }

    private RecipeDto convertToDto(Recipe recipe) {
        RecipeDto dto = new RecipeDto();
        dto.setId(recipe.getId());
        dto.setName(recipe.getName());
        dto.setDescription(recipe.getDescription());
        dto.setMood(recipe.getMood());
        dto.setImageUrl(recipe.getImageUrl());
        dto.setSweetness(recipe.getSweetness());
        dto.setSourness(recipe.getSourness());
        dto.setAlcohol(recipe.getAlcohol());
        dto.setFruitiness(recipe.getFruitiness());
        dto.setLikeCount(recipe.getLikeCount());
        dto.setViewCount(recipe.getViewCount());
        dto.setFavoriteCount(recipe.getFavoriteCount());
        dto.setCommentCount(recipe.getCommentCount());
        dto.setCreatedAt(recipe.getCreatedAt());
        return dto;
    }
}
