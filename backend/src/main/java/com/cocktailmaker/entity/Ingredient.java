package com.cocktailmaker.entity;

import com.cocktailmaker.enums.IngredientType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 材料实体类
 */
@Data
@Entity
@Table(name = "ingredients")
public class Ingredient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "材料名称不能为空")
    @Size(max = 100, message = "材料名称长度不能超过100个字符")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "材料类型不能为空")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IngredientType type;

    private String emoji;

    @Size(max = 500, message = "描述长度不能超过500个字符")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "alcohol_content", precision = 3, scale = 1)
    private Double alcoholContent = 0.0;

    @Column(name = "calories_per_100ml")
    private Integer caloriesPer100ml = 0;

    // 关联关系
    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    // Constructors
    public Ingredient() {
    }

    public Ingredient(String name, IngredientType type) {
        this.name = name;
        this.type = type;
    }


}