package com.cocktailmaker.service;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.RecipeVersionDto;

import java.util.List;

public interface RecipeVersionService {

    ApiResponse<RecipeVersionDto> saveVersion(Long recipeId, Long userId, String changeSummary);

    ApiResponse<List<RecipeVersionDto>> getVersions(Long recipeId);

    ApiResponse<RecipeVersionDto> getVersion(Long recipeId, Integer versionNumber);

    ApiResponse<RecipeVersionDto> restoreVersion(Long recipeId, Integer versionNumber, Long userId);

    ApiResponse<String> diffVersions(Long recipeId, Integer v1, Integer v2);
}
