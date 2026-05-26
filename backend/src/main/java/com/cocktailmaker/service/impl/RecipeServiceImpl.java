package com.cocktailmaker.service.impl;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.RecipeDto;
import com.cocktailmaker.entity.Recipe;
import com.cocktailmaker.entity.User;
import com.cocktailmaker.enums.MoodType;
import com.cocktailmaker.repository.RecipeRepository;
import com.cocktailmaker.repository.UserRepository;
import com.cocktailmaker.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 配方服务实现类
 */
@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ApiResponse<RecipeDto> createRecipe(RecipeDto recipeDto, Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }

            Recipe recipe = new Recipe();
            recipe.setName(recipeDto.getName());
            recipe.setDescription(recipeDto.getDescription());
            recipe.setUser(user);
            recipe.setMood(recipeDto.getMood());
            recipe.setSweetness(recipeDto.getSweetness());
            recipe.setSourness(recipeDto.getSourness());
            recipe.setAlcohol(recipeDto.getAlcohol());
            recipe.setFruitiness(recipeDto.getFruitiness());
            recipe.setIsPublic(recipeDto.getIsPublic());

            Recipe savedRecipe = recipeRepository.save(recipe);
            RecipeDto resultDto = convertToDto(savedRecipe);
            return ApiResponse.success("配方创建成功", resultDto);
        } catch (Exception e) {
            return ApiResponse.error("创建配方失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<RecipeDto> updateRecipe(Long recipeId, RecipeDto recipeDto, Long userId) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Void> deleteRecipe(Long recipeId, Long userId) {
        try {
            recipeRepository.deleteById(recipeId);
            return ApiResponse.success("配方删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error("删除配方失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<RecipeDto> getRecipeById(Long recipeId, Long userId) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Page<RecipeDto>> getRecipes(Pageable pageable) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Page<RecipeDto>> getPublicRecipes(Pageable pageable) {
        try {
            Page<Recipe> recipes = recipeRepository.findByIsPublicTrue(pageable);
            Page<RecipeDto> recipeDtos = recipes.map(this::convertToDto);
            return ApiResponse.success(recipeDtos);
        } catch (Exception e) {
            return ApiResponse.error("获取公开配方失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<Page<RecipeDto>> getUserRecipes(Long userId, Pageable pageable) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Page<RecipeDto>> getRecipesByMood(MoodType mood, Pageable pageable) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Page<RecipeDto>> searchRecipes(String keyword, Pageable pageable) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Page<RecipeDto>> getPopularRecipes(Pageable pageable) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Page<RecipeDto>> getLatestRecipes(Pageable pageable) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Page<RecipeDto>> getRecommendedRecipes(Long userId, Pageable pageable) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<RecipeDto> generateRecipe(RecipeDto recipeDto, Long userId) {
        return createRecipe(recipeDto, userId);
    }

    @Override
    public ApiResponse<Void> incrementViewCount(Long recipeId, Long userId) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Void> likeRecipe(Long recipeId, Long userId) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Void> unlikeRecipe(Long recipeId, Long userId) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Void> favoriteRecipe(Long recipeId, Long userId) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Void> unfavoriteRecipe(Long recipeId, Long userId) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Void> rateRecipe(Long recipeId, Long userId, Integer rating, String comment) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Page<RecipeDto>> getUserFavoriteRecipes(Long userId, Pageable pageable) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Page<RecipeDto>> getUserViewHistory(Long userId, Pageable pageable) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<String> shareRecipe(Long recipeId, Long userId) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Void> batchDeleteRecipes(List<Long> recipeIds, Long userId) {
        return ApiResponse.error("未实现");
    }

    @Override
    public ApiResponse<Void> updateRecipeVisibility(Long recipeId, Boolean isPublic, Long userId) {
        return ApiResponse.error("未实现");
    }

    /**
     * 将Recipe实体转换为RecipeDto
     */
    private RecipeDto convertToDto(Recipe recipe) {
        RecipeDto dto = new RecipeDto();
        dto.setId(recipe.getId());
        dto.setName(recipe.getName());
        dto.setDescription(recipe.getDescription());
        dto.setMood(recipe.getMood());
        dto.setSweetness(recipe.getSweetness());
        dto.setSourness(recipe.getSourness());
        dto.setAlcohol(recipe.getAlcohol());
        dto.setFruitiness(recipe.getFruitiness());
        dto.setIsPublic(recipe.getIsPublic());
        dto.setViewCount(recipe.getViewCount());
        dto.setLikeCount(recipe.getLikeCount());
        dto.setFavoriteCount(recipe.getFavoriteCount());
        dto.setCommentCount(recipe.getCommentCount());
        dto.setCreatedAt(recipe.getCreatedAt());
        dto.setUpdatedAt(recipe.getUpdatedAt());
        return dto;
    }
}