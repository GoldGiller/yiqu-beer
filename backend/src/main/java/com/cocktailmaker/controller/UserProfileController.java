package com.cocktailmaker.controller;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.FlavorProfileDto;
import com.cocktailmaker.dto.UserActivityDto;
import com.cocktailmaker.dto.UserProfileDto;
import com.cocktailmaker.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserProfile(
            @PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getUserProfile(userId));
    }

    @GetMapping("/{userId}/activities")
    public ResponseEntity<ApiResponse<Page<UserActivityDto>>> getUserActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userProfileService.getUserActivities(userId, page, size));
    }

    @GetMapping("/{userId}/flavor-profile")
    public ResponseEntity<ApiResponse<FlavorProfileDto>> getFlavorProfile(
            @PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getFlavorProfile(userId));
    }
}
