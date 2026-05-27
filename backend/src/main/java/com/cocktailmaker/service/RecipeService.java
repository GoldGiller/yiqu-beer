package com.cocktailmaker.service;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.RecipeDto;
import com.cocktailmaker.enums.MoodType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 配方服务接口
 */
public interface RecipeService {
    
    /**
     * 创建配方
     */
    ApiResponse<RecipeDto> createRecipe(RecipeDto recipeDto, Long userId);
    
    /**
     * 更新配方
     */
    ApiResponse<RecipeDto> updateRecipe(Long recipeId, RecipeDto recipeDto, Long userId);
    
    /**
     * 删除配方
     */
    ApiResponse<Void> deleteRecipe(Long recipeId, Long userId);
    
    /**
     * 获取配方详情
     */
    ApiResponse<RecipeDto> getRecipeById(Long recipeId, Long userId);
    
    /**
     * 获取配方列表
     */
    ApiResponse<Page<RecipeDto>> getRecipes(Pageable pageable);
    
    /**
     * 获取公开配方列表
     */
    ApiResponse<Page<RecipeDto>> getPublicRecipes(Pageable pageable);
    
    /**
     * 获取用户的配方
     */
    ApiResponse<Page<RecipeDto>> getUserRecipes(Long userId, Pageable pageable);
    
    /**
     * 根据心情获取配方
     */
    ApiResponse<Page<RecipeDto>> getRecipesByMood(MoodType mood, Pageable pageable);
    
    /**
     * 搜索配方
     */
    ApiResponse<Page<RecipeDto>> searchRecipes(String keyword, Pageable pageable);
    
    /**
     * 获取热门配方
     */
    ApiResponse<Page<RecipeDto>> getPopularRecipes(Pageable pageable);
    
    /**
     * 获取最新配方
     */
    ApiResponse<Page<RecipeDto>> getLatestRecipes(Pageable pageable);
    
    /**
     * 获取推荐配方
     */
    ApiResponse<Page<RecipeDto>> getRecommendedRecipes(Long userId, Pageable pageable);
    
    /**
     * 生成配方
     */
    ApiResponse<RecipeDto> generateRecipe(RecipeDto recipeDto, Long userId);
    
    /**
     * 增加浏览次数
     */
    ApiResponse<Void> incrementViewCount(Long recipeId, Long userId);
    
    /**
     * 点赞配方
     */
    ApiResponse<Void> likeRecipe(Long recipeId, Long userId);
    
    /**
     * 取消点赞配方
     */
    ApiResponse<Void> unlikeRecipe(Long recipeId, Long userId);
    
    /**
     * 收藏配方
     */
    ApiResponse<Void> favoriteRecipe(Long recipeId, Long userId);
    
    /**
     * 取消收藏配方
     */
    ApiResponse<Void> unfavoriteRecipe(Long recipeId, Long userId);
    
    /**
     * 评分配方
     */
    ApiResponse<Void> rateRecipe(Long recipeId, Long userId, Integer rating, String comment);
    
    /**
     * 获取用户的收藏配方
     */
    ApiResponse<Page<RecipeDto>> getUserFavoriteRecipes(Long userId, Pageable pageable);
    
    /**
     * 获取用户的浏览历史
     */
    ApiResponse<Page<RecipeDto>> getUserViewHistory(Long userId, Pageable pageable);
    
    /**
     * 分享配方
     */
    ApiResponse<String> shareRecipe(Long recipeId, Long userId);
    
    /**
     * 批量删除配方
     */
    ApiResponse<Void> batchDeleteRecipes(List<Long> recipeIds, Long userId);
    
    /**
     * 更新配方可见性
     */
    ApiResponse<Void> updateRecipeVisibility(Long recipeId, Boolean isPublic, Long userId);
}