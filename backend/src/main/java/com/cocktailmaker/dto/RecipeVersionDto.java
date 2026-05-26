package com.cocktailmaker.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RecipeVersionDto {
    private Long id;
    private Long recipeId;
    private Integer versionNumber;
    private String snapshotData;
    private String changeSummary;
    private Long createdBy;
    private String creatorName;
    private LocalDateTime createdAt;
}
