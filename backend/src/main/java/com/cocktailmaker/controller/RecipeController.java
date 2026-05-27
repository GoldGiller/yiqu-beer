package com.cocktailmaker.controller;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.RecipeDto;
import com.cocktailmaker.enums.MoodType;

import com.cocktailmaker.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 配方控制器
 */
@RestController
@RequestMapping("/recipes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    /**
     * 创建配方
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RecipeDto>> createRecipe(
            @Valid @RequestBody RecipeDto recipeDto,
            @RequestHeader("X-User-Id") Long userId) {
        ApiResponse<RecipeDto> response = recipeService.createRecipe(recipeDto, userId);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    /**
     * 获取配方详情
     */
    @GetMapping("/{recipeId}")
    public ResponseEntity<ApiResponse<RecipeDto>> getRecipeById(
            @PathVariable Long recipeId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        ApiResponse<RecipeDto> response = recipeService.getRecipeById(recipeId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新配方
     */
    @PutMapping("/{recipeId}")
    public ResponseEntity<ApiResponse<RecipeDto>> updateRecipe(
            @PathVariable Long recipeId,
            @Valid @RequestBody RecipeDto recipeDto,
            @RequestHeader("X-User-Id") Long userId) {
        ApiResponse<RecipeDto> response = recipeService.updateRecipe(recipeId, recipeDto, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除配方
     */
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<ApiResponse<Void>> deleteRecipe(
            @PathVariable Long recipeId,
            @RequestHeader("X-User-Id") Long userId) {
        ApiResponse<Void> response = recipeService.deleteRecipe(recipeId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取公开配方列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RecipeDto>>> getPublicRecipes(
            @PageableDefault(size = 20) Pageable pageable) {
        ApiResponse<Page<RecipeDto>> response = recipeService.getPublicRecipes(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户的配方
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<RecipeDto>>> getUserRecipes(
            @RequestHeader("X-User-Id") Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        ApiResponse<Page<RecipeDto>> response = recipeService.getUserRecipes(userId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据心情获取配方
     */
    @GetMapping("/mood/{mood}")
    public ResponseEntity<ApiResponse<Page<RecipeDto>>> getRecipesByMood(
            @PathVariable MoodType mood,
            @PageableDefault(size = 20) Pageable pageable) {
        ApiResponse<Page<RecipeDto>> response = recipeService.getRecipesByMood(mood, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 搜索配方
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<RecipeDto>>> searchRecipes(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        ApiResponse<Page<RecipeDto>> response = recipeService.searchRecipes(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取热门配方
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<Page<RecipeDto>>> getPopularRecipes(
            @PageableDefault(size = 20) Pageable pageable) {
        ApiResponse<Page<RecipeDto>> response = recipeService.getPopularRecipes(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取最新配方
     */
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<Page<RecipeDto>>> getLatestRecipes(
            @PageableDefault(size = 20) Pageable pageable) {
        ApiResponse<Page<RecipeDto>> response = recipeService.getLatestRecipes(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取推荐配方
     */
    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<Page<RecipeDto>>> getRecommendedRecipes(
            @RequestHeader("X-User-Id") Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        ApiResponse<Page<RecipeDto>> response = recipeService.getRecommendedRecipes(userId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 生成配方
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<RecipeDto>> generateRecipe(
            @Valid @RequestBody RecipeDto recipeDto,
            @RequestHeader("X-User-Id") Long userId) {
        ApiResponse<RecipeDto> response = recipeService.generateRecipe(recipeDto, userId);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    /**
     * 点赞配方
     */
    @PostMapping("/{recipeId}/like")
    public ResponseEntity<ApiResponse<Void>> likeRecipe(
            @PathVariable Long recipeId,
            @RequestHeader("X-User-Id") Long userId) {
        ApiResponse<Void> response = recipeService.likeRecipe(recipeId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 取消点赞配方
     */
    @DeleteMapping("/{recipeId}/like")
    public ResponseEntity<ApiResponse<Void>> unlikeRecipe(
            @PathVariable Long recipeId,
            @RequestHeader("X-User-Id") Long userId) {
        ApiResponse<Void> response = recipeService.unlikeRecipe(recipeId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 收藏配方
     */
    @PostMapping("/{recipeId}/favorite")
    public ResponseEntity<ApiResponse<Void>> favoriteRecipe(
            @PathVariable Long recipeId,
            @RequestHeader("X-User-Id") Long userId) {
        ApiResponse<Void> response = recipeService.favoriteRecipe(recipeId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 取消收藏配方
     */
    @DeleteMapping("/{recipeId}/favorite")
    public ResponseEntity<ApiResponse<Void>> unfavoriteRecipe(
            @PathVariable Long recipeId,
            @RequestHeader("X-User-Id") Long userId) {
        ApiResponse<Void> response = recipeService.unfavoriteRecipe(recipeId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 评分配方
     */
    @PostMapping("/{recipeId}/rate")
    public ResponseEntity<ApiResponse<Void>> rateRecipe(
            @PathVariable Long recipeId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment) {
        ApiResponse<Void> response = recipeService.rateRecipe(recipeId, userId, rating, comment);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户的收藏配方
     */
    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<Page<RecipeDto>>> getUserFavoriteRecipes(
            @RequestHeader("X-User-Id") Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        ApiResponse<Page<RecipeDto>> response = recipeService.getUserFavoriteRecipes(userId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户的浏览历史
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<RecipeDto>>> getUserViewHistory(
            @RequestHeader("X-User-Id") Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        ApiResponse<Page<RecipeDto>> response = recipeService.getUserViewHistory(userId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 分享配方
     */
    @GetMapping("/{recipeId}/share")
    public ResponseEntity<ApiResponse<String>> shareRecipe(
            @PathVariable Long recipeId,
            @RequestHeader("X-User-Id") Long userId) {
        ApiResponse<String> response = recipeService.shareRecipe(recipeId, userId);
        return ResponseEntity.ok(response);
    }
}