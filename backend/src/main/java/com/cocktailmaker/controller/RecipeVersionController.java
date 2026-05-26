package com.cocktailmaker.controller;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.RecipeVersionDto;
import com.cocktailmaker.service.RecipeVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecipeVersionController {

    @Autowired
    private RecipeVersionService versionService;

    @PostMapping("/{recipeId}/versions")
    public ResponseEntity<ApiResponse<RecipeVersionDto>> saveVersion(
            @PathVariable Long recipeId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) String summary) {
        return ResponseEntity.ok(versionService.saveVersion(recipeId, userId,
                summary != null ? summary : "手动保存版本"));
    }

    @GetMapping("/{recipeId}/versions")
    public ResponseEntity<ApiResponse<List<RecipeVersionDto>>> getVersions(
            @PathVariable Long recipeId) {
        return ResponseEntity.ok(versionService.getVersions(recipeId));
    }

    @GetMapping("/{recipeId}/versions/{versionNumber}")
    public ResponseEntity<ApiResponse<RecipeVersionDto>> getVersion(
            @PathVariable Long recipeId,
            @PathVariable Integer versionNumber) {
        return ResponseEntity.ok(versionService.getVersion(recipeId, versionNumber));
    }

    @PostMapping("/{recipeId}/versions/{versionNumber}/restore")
    public ResponseEntity<ApiResponse<RecipeVersionDto>> restoreVersion(
            @PathVariable Long recipeId,
            @PathVariable Integer versionNumber,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(versionService.restoreVersion(recipeId, versionNumber, userId));
    }

    @GetMapping("/{recipeId}/versions/diff")
    public ResponseEntity<ApiResponse<String>> diffVersions(
            @PathVariable Long recipeId,
            @RequestParam Integer v1,
            @RequestParam Integer v2) {
        return ResponseEntity.ok(versionService.diffVersions(recipeId, v1, v2));
    }
}
