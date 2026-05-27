package com.cocktailmaker.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserActivityDto {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String activityType;
    private String targetType;
    private Long targetId;
    private String summary;
    private String activityIcon;
    private LocalDateTime createdAt;
}
