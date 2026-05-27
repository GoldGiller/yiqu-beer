package com.cocktailmaker.entity;

import com.cocktailmaker.enums.MoodType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 配方实体类
 */
@Data
@Entity
@Table(name = "recipes")
public class Recipe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "配方名称不能为空")
    @Size(max = 200, message = "配方名称长度不能超过200个字符")
    @Column(nullable = false)
    private String name;

    @Size(max = 1000, message = "配方描述长度不能超过1000个字符")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "用户ID不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "心情不能为空")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MoodType mood;

    private String imageUrl;

    @Column(nullable = false)
    private Integer sweetness = 5;

    @Column(nullable = false)
    private Integer sourness = 5;

    @Column(nullable = false)
    private Integer alcohol = 5;

    @Column(nullable = false)
    private Integer fruitiness = 5;

    @Column(nullable = false)
    private Boolean isPublic = true;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    private Integer favoriteCount = 0;

    @Column(nullable = false)
    private Integer commentCount = 0;

    // 关联关系
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    // Constructors
    public Recipe() {
    }

    public Recipe(String name, User user, MoodType mood) {
        this.name = name;
        this.user = user;
        this.mood = mood;
    }



    // Helper methods
    public Double getAverageRating() {
        return 0.0;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementFavoriteCount() {
        this.favoriteCount++;
    }

    public void decrementFavoriteCount() {
        if (this.favoriteCount > 0) {
            this.favoriteCount--;
        }
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }
}