package com.cocktailmaker.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecommendDto {
    private String reason;
    private List<RecipeDto> recipes;
    private String season;
    private String seasonEmoji;
}
