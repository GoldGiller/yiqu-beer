package com.cocktailmaker.service;

import com.cocktailmaker.dto.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    ApiResponse<String> uploadImage(MultipartFile file, Long userId);

    ApiResponse<String> uploadRecipeImage(MultipartFile file, Long recipeId, Long userId);
}
