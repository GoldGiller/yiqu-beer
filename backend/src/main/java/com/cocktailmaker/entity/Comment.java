package com.cocktailmaker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @NotBlank(message = "评论内容不能为空")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<Comment> replies = new ArrayList<>();

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Column(name = "recipe_id", insertable = false, updatable = false)
    private Long recipeId;

    @Column(name = "parent_id", insertable = false, updatable = false)
    private Long parentId;

    public Comment() {}

    public Comment(User user, Recipe recipe, String content) {
        this.user = user;
        this.recipe = recipe;
        this.content = content;
    }
}
