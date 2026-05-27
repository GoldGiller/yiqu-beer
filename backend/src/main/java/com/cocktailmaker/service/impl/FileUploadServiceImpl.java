package com.cocktailmaker.service.impl;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.entity.Recipe;
import com.cocktailmaker.repository.RecipeRepository;
import com.cocktailmaker.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${app.storage.local.path:./uploads}")
    private String uploadPath;

    @Value("${app.storage.local.url-prefix:http://localhost:8080/api/uploads/}")
    private String urlPrefix;

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    @Autowired
    private RecipeRepository recipeRepository;

    @Override
    public ApiResponse<String> uploadImage(MultipartFile file, Long userId) {
        return doUpload(file, "general");
    }

    @Override
    public ApiResponse<String> uploadRecipeImage(MultipartFile file, Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe == null) return ApiResponse.error("配方不存在");

        ApiResponse<String> result = doUpload(file, "recipes");
        if (result.isSuccess()) {
            recipe.setImageUrl(result.getData());
            recipeRepository.save(recipe);
        }
        return result;
    }

    private ApiResponse<String> doUpload(MultipartFile file, String subDir) {
        try {
            if (file.isEmpty()) return ApiResponse.error("文件为空");
            if (file.getSize() > MAX_SIZE) return ApiResponse.error("文件大小不能超过5MB");

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ApiResponse.error("只允许上传图片文件");
            }

            String ext = getExtension(file.getOriginalFilename());
            if (!ext.matches("^(jpg|jpeg|png|gif|webp)$")) {
                return ApiResponse.error("不支持的图片格式，支持: jpg, jpeg, png, gif, webp");
            }

            Path dir = Paths.get(uploadPath, subDir);
            Files.createDirectories(dir);

            String filename = UUID.randomUUID().toString() + "." + ext;
            Path filePath = dir.resolve(filename);
            file.transferTo(filePath.toFile());

            String url = urlPrefix + subDir + "/" + filename;
            return ApiResponse.success("上传成功", url);
        } catch (Exception e) {
            return ApiResponse.error("上传失败: " + e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "jpg";
        int idx = filename.lastIndexOf('.');
        return idx < 0 ? "jpg" : filename.substring(idx + 1).toLowerCase();
    }
}
