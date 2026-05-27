package com.cocktailmaker.service;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.FoodPairingDto;
import com.cocktailmaker.dto.RecipeDto;
import com.cocktailmaker.dto.RecommendDto;

import java.util.List;

public interface RecommendationService {

    ApiResponse<RecommendDto> getCollaborativeRecommendations(Long userId, int limit);

    ApiResponse<RecommendDto> getSeasonalRecommendations(int limit);

    ApiResponse<List<FoodPairingDto>> getFoodPairings(Long recipeId);

    ApiResponse<Void> recordInteraction(Long userId, Long recipeId, String interactionType);
}
