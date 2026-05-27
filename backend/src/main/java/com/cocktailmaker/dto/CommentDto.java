package com.cocktailmaker.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private Long recipeId;
    private Long parentId;
    private String content;
    private Integer likeCount;
    private Boolean isDeleted;
    private Boolean likedByMe;
    private Integer depth;
    private List<CommentDto> replies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
