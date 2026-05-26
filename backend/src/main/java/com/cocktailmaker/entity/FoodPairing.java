package com.cocktailmaker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "food_pairings")
public class FoodPairing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "食物名称不能为空")
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @Size(max = 10)
    private String emoji;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 50)
    private String category;

    public FoodPairing() {}

    public FoodPairing(String name, String emoji, String description, String category) {
        this.name = name;
        this.emoji = emoji;
        this.description = description;
        this.category = category;
    }
}
