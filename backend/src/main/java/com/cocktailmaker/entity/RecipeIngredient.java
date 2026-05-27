package com.cocktailmaker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 配方材料关联实体类
 */
@Data
@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "配方不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @NotNull(message = "材料不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @NotBlank(message = "用量不能为空")
    @Column(nullable = false)
    private String amount;

    @Column(nullable = false)
    private String unit = "ml";

    @Column(name = "order_num", nullable = false)
    private Integer orderNum = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Constructors
    public RecipeIngredient() {
    }

    public RecipeIngredient(Recipe recipe, Ingredient ingredient, String amount) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.amount = amount;
    }


}