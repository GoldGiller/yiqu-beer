package com.cocktailmaker.service.impl;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.RecipeVersionDto;
import com.cocktailmaker.entity.Recipe;
import com.cocktailmaker.entity.RecipeVersion;
import com.cocktailmaker.entity.User;
import com.cocktailmaker.repository.RecipeRepository;
import com.cocktailmaker.repository.RecipeVersionRepository;
import com.cocktailmaker.repository.UserRepository;
import com.cocktailmaker.service.RecipeVersionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeVersionServiceImpl implements RecipeVersionService {

    @Autowired
    private RecipeVersionRepository versionRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public ApiResponse<RecipeVersionDto> saveVersion(Long recipeId, Long userId, String changeSummary) {
        try {
            Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
            if (recipe == null) return ApiResponse.error("配方不存在");

            Long count = versionRepository.countByRecipeId(recipeId);
            int nextVersion = count.intValue() + 1;

            Map<String, Object> snapshot = new LinkedHashMap<>();
            snapshot.put("name", recipe.getName());
            snapshot.put("description", recipe.getDescription());
            snapshot.put("mood", recipe.getMood().name());
            snapshot.put("sweetness", recipe.getSweetness());
            snapshot.put("sourness", recipe.getSourness());
            snapshot.put("alcohol", recipe.getAlcohol());
            snapshot.put("fruitiness", recipe.getFruitiness());
            snapshot.put("ingredients", recipe.getRecipeIngredients());
            snapshot.put("imageUrl", recipe.getImageUrl());

            String json = objectMapper.writeValueAsString(snapshot);
            RecipeVersion version = new RecipeVersion(recipeId, nextVersion, json, userId, changeSummary);
            version = versionRepository.save(version);

            return ApiResponse.success("版本保存成功", convertToDto(version));
        } catch (Exception e) {
            return ApiResponse.error("版本保存失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<RecipeVersionDto>> getVersions(Long recipeId) {
        try {
            List<RecipeVersion> versions = versionRepository.findByRecipeIdOrderByVersionNumberDesc(recipeId);
            List<RecipeVersionDto> dtos = versions.stream().map(this::convertToDto).collect(Collectors.toList());
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error("获取版本列表失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<RecipeVersionDto> getVersion(Long recipeId, Integer versionNumber) {
        try {
            RecipeVersion version = versionRepository.findByRecipeIdAndVersionNumber(recipeId, versionNumber).orElse(null);
            if (version == null) return ApiResponse.error("版本不存在");
            return ApiResponse.success(convertToDto(version));
        } catch (Exception e) {
            return ApiResponse.error("获取版本失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse<RecipeVersionDto> restoreVersion(Long recipeId, Integer versionNumber, Long userId) {
        try {
            RecipeVersion version = versionRepository.findByRecipeIdAndVersionNumber(recipeId, versionNumber).orElse(null);
            if (version == null) return ApiResponse.error("版本不存在");

            Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
            if (recipe == null) return ApiResponse.error("配方不存在");

            // 先保存当前版本
            saveVersion(recipeId, userId, "回滚前自动保存");

            // 恢复
            @SuppressWarnings("unchecked")
            Map<String, Object> snapshot = objectMapper.readValue(version.getSnapshotData(), Map.class);
            recipe.setName((String) snapshot.get("name"));
            recipe.setDescription((String) snapshot.get("description"));
            recipe.setSweetness((Integer) snapshot.get("sweetness"));
            recipe.setSourness((Integer) snapshot.get("sourness"));
            recipe.setAlcohol((Integer) snapshot.get("alcohol"));
            recipe.setFruitiness((Integer) snapshot.get("fruitiness"));
            recipeRepository.save(recipe);

            return ApiResponse.success("版本回滚成功", convertToDto(version));
        } catch (Exception e) {
            return ApiResponse.error("版本回滚失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<String> diffVersions(Long recipeId, Integer v1, Integer v2) {
        try {
            RecipeVersion ver1 = versionRepository.findByRecipeIdAndVersionNumber(recipeId, v1).orElse(null);
            RecipeVersion ver2 = versionRepository.findByRecipeIdAndVersionNumber(recipeId, v2).orElse(null);
            if (ver1 == null || ver2 == null) return ApiResponse.error("版本不存在");

            @SuppressWarnings("unchecked")
            Map<String, Object> s1 = objectMapper.readValue(ver1.getSnapshotData(), Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> s2 = objectMapper.readValue(ver2.getSnapshotData(), Map.class);

            StringBuilder diff = new StringBuilder();
            Set<String> allKeys = new LinkedHashSet<>();
            allKeys.addAll(s1.keySet());
            allKeys.addAll(s2.keySet());

            for (String key : allKeys) {
                Object val1 = s1.get(key);
                Object val2 = s2.get(key);
                if (!Objects.equals(val1, val2)) {
                    diff.append(String.format("【%s】: %s → %s\n", key, val1, val2));
                }
            }

            if (diff.length() == 0) diff.append("两个版本无差异");
            return ApiResponse.success(diff.toString());
        } catch (Exception e) {
            return ApiResponse.error("版本对比失败: " + e.getMessage());
        }
    }

    private RecipeVersionDto convertToDto(RecipeVersion version) {
        RecipeVersionDto dto = new RecipeVersionDto();
        dto.setId(version.getId());
        dto.setRecipeId(version.getRecipeId());
        dto.setVersionNumber(version.getVersionNumber());
        dto.setSnapshotData(version.getSnapshotData());
        dto.setChangeSummary(version.getChangeSummary());
        dto.setCreatedBy(version.getCreatedBy());
        dto.setCreatedAt(version.getCreatedAt());

        userRepository.findById(version.getCreatedBy()).ifPresent(u -> {
            dto.setCreatorName(u.getNickname() != null ? u.getNickname() : u.getUsername());
        });

        return dto;
    }
}
