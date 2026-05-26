package com.cocktailmaker.service;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.CommentDto;
import org.springframework.data.domain.Page;

public interface CommentService {

    ApiResponse<CommentDto> createComment(CommentDto dto, Long userId);

    ApiResponse<CommentDto> createReply(Long parentId, CommentDto dto, Long userId);

    ApiResponse<Page<CommentDto>> getRecipeComments(Long recipeId, Long userId, int page, int size);

    ApiResponse<Void> likeComment(Long commentId, Long userId);

    ApiResponse<Void> unlikeComment(Long commentId, Long userId);

    ApiResponse<Void> deleteComment(Long commentId, Long userId);
}
