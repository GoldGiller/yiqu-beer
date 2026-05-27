package com.cocktailmaker.controller;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/uploads")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/image")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(fileUploadService.uploadImage(file, userId));
    }

    @PostMapping("/recipe/{recipeId}/image")
    public ResponseEntity<ApiResponse<String>> uploadRecipeImage(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long recipeId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(fileUploadService.uploadRecipeImage(file, recipeId, userId));
    }
}
