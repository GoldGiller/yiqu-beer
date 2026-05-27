package com.cocktailmaker.controller;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.FoodPairingDto;
import com.cocktailmaker.dto.RecommendDto;
import com.cocktailmaker.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/collaborative")
    public ResponseEntity<ApiResponse<RecommendDto>> getCollaborative(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getCollaborativeRecommendations(userId, limit));
    }

    @GetMapping("/seasonal")
    public ResponseEntity<ApiResponse<RecommendDto>> getSeasonal(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getSeasonalRecommendations(limit));
    }

    @GetMapping("/food-pairings/{recipeId}")
    public ResponseEntity<ApiResponse<List<FoodPairingDto>>> getFoodPairings(
            @PathVariable Long recipeId) {
        return ResponseEntity.ok(recommendationService.getFoodPairings(recipeId));
    }

    @PostMapping("/interactions")
    public ResponseEntity<ApiResponse<Void>> recordInteraction(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long recipeId,
            @RequestParam String type) {
        return ResponseEntity.ok(recommendationService.recordInteraction(userId, recipeId, type));
    }
}
