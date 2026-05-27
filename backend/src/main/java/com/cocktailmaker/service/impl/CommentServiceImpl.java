package com.cocktailmaker.service.impl;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.CommentDto;
import com.cocktailmaker.entity.Comment;
import com.cocktailmaker.entity.CommentLike;
import com.cocktailmaker.entity.Recipe;
import com.cocktailmaker.entity.User;
import com.cocktailmaker.repository.CommentLikeRepository;
import com.cocktailmaker.repository.CommentRepository;
import com.cocktailmaker.repository.RecipeRepository;
import com.cocktailmaker.repository.UserRepository;
import com.cocktailmaker.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public ApiResponse<CommentDto> createComment(CommentDto dto, Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return ApiResponse.error("用户不存在");

            Recipe recipe = recipeRepository.findById(dto.getRecipeId()).orElse(null);
            if (recipe == null) return ApiResponse.error("配方不存在");

            Comment comment = new Comment(user, recipe, dto.getContent());
            comment = commentRepository.save(comment);
            recipe.incrementCommentCount();
            recipeRepository.save(recipe);

            return ApiResponse.success("评论成功", convertToDto(comment, userId));
        } catch (Exception e) {
            return ApiResponse.error("评论失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse<CommentDto> createReply(Long parentId, CommentDto dto, Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return ApiResponse.error("用户不存在");

            Comment parent = commentRepository.findById(parentId).orElse(null);
            if (parent == null) return ApiResponse.error("父评论不存在");

            Recipe recipe = parent.getRecipe();
            Comment reply = new Comment(user, recipe, dto.getContent());
            reply.setParent(parent);
            reply = commentRepository.save(reply);
            recipe.incrementCommentCount();
            recipeRepository.save(recipe);

            return ApiResponse.success("回复成功", convertToDto(reply, userId));
        } catch (Exception e) {
            return ApiResponse.error("回复失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<Page<CommentDto>> getRecipeComments(Long recipeId, Long userId, int page, int size) {
        try {
            PageRequest pr = PageRequest.of(page, size);
            Page<Comment> comments = commentRepository.findByRecipeIdAndParentIsNullOrderByCreatedAtDesc(recipeId, pr);
            Page<CommentDto> dtoPage = comments.map(c -> buildCommentTree(c, userId));
            return ApiResponse.success(dtoPage);
        } catch (Exception e) {
            return ApiResponse.error("获取评论失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> likeComment(Long commentId, Long userId) {
        try {
            if (commentLikeRepository.existsByUserIdAndCommentId(userId, commentId)) {
                return ApiResponse.error("已点赞");
            }
            commentLikeRepository.save(new CommentLike(userId, commentId));
            Comment comment = commentRepository.findById(commentId).orElse(null);
            if (comment != null) {
                comment.setLikeCount((comment.getLikeCount() == null ? 0 : comment.getLikeCount()) + 1);
                commentRepository.save(comment);
            }
            return ApiResponse.success("点赞成功", null);
        } catch (Exception e) {
            return ApiResponse.error("点赞失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> unlikeComment(Long commentId, Long userId) {
        try {
            commentLikeRepository.findByUserIdAndCommentId(userId, commentId)
                    .ifPresent(cl -> commentLikeRepository.delete(cl));
            Comment comment = commentRepository.findById(commentId).orElse(null);
            if (comment != null && comment.getLikeCount() != null && comment.getLikeCount() > 0) {
                comment.setLikeCount(comment.getLikeCount() - 1);
                commentRepository.save(comment);
            }
            return ApiResponse.success("取消点赞成功", null);
        } catch (Exception e) {
            return ApiResponse.error("取消点赞失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteComment(Long commentId, Long userId) {
        try {
            Comment comment = commentRepository.findById(commentId).orElse(null);
            if (comment == null) return ApiResponse.error("评论不存在");
            if (!comment.getUser().getId().equals(userId)) return ApiResponse.error("无权删除");
            comment.setIsDeleted(true);
            comment.setContent("[该评论已删除]");
            commentRepository.save(comment);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    private CommentDto convertToDto(Comment comment, Long currentUserId) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setNickname(comment.getUser().getNickname());
        dto.setAvatar(comment.getUser().getAvatar());
        dto.setRecipeId(comment.getRecipe().getId());
        dto.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        dto.setContent(comment.getContent());
        dto.setLikeCount(comment.getLikeCount());
        dto.setIsDeleted(comment.getIsDeleted());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        if (currentUserId != null) {
            dto.setLikedByMe(commentLikeRepository.existsByUserIdAndCommentId(currentUserId, comment.getId()));
        }
        return dto;
    }

    private CommentDto buildCommentTree(Comment comment, Long currentUserId) {
        CommentDto dto = convertToDto(comment, currentUserId);
        dto.setDepth(0);
        List<Comment> replies = comment.getReplies();
        if (replies != null && !replies.isEmpty()) {
            dto.setReplies(replies.stream()
                    .map(r -> {
                        CommentDto replyDto = convertToDto(r, currentUserId);
                        replyDto.setDepth(1);
                        return replyDto;
                    })
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
