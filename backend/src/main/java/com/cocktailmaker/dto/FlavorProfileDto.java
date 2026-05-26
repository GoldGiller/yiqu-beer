package com.cocktailmaker.dto;

import lombok.Data;
import java.util.Map;

@Data
public class FlavorProfileDto {
    private Long userId;
    private Double avgSweetness;
    private Double avgSourness;
    private Double avgAlcohol;
    private Double avgFruitiness;
    private Integer totalRecipesCreated;
    private String dominantFlavor;
    private Map<String, Double> flavorDistribution;
}
