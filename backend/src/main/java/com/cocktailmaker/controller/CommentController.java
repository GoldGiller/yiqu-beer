package com.cocktailmaker.controller;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.CommentDto;
import com.cocktailmaker.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentDto>> createComment(
            @Valid @RequestBody CommentDto dto,
            @RequestHeader("X-User-Id") Long userId) {
        ApiResponse<CommentDto> response = commentService.createComment(dto, userId);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/{commentId}/reply")
    public ResponseEntity<ApiResponse<CommentDto>> createReply(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDto dto,
            @RequestHeader("X-User-Id") Long userId) {
        ApiResponse<CommentDto> response = commentService.createReply(commentId, dto, userId);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<ApiResponse<Page<CommentDto>>> getRecipeComments(
            @PathVariable Long recipeId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(commentService.getRecipeComments(recipeId, userId, page, size));
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<ApiResponse<Void>> likeComment(
            @PathVariable Long commentId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(commentService.likeComment(commentId, userId));
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<ApiResponse<Void>> unlikeComment(
            @PathVariable Long commentId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(commentService.unlikeComment(commentId, userId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(commentService.deleteComment(commentId, userId));
    }
}
