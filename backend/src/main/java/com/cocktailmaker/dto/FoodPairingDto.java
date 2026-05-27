package com.cocktailmaker.dto;

import lombok.Data;

@Data
public class FoodPairingDto {
    private Long id;
    private String name;
    private String emoji;
    private String description;
    private String category;
}
