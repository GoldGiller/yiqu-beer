package com.cocktailmaker.repository;

import com.cocktailmaker.entity.Recipe;
import com.cocktailmaker.entity.User;
import com.cocktailmaker.enums.MoodType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 配方数据访问层
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * 根据用户查找配方
     */
    Page<Recipe> findByUser(User user, Pageable pageable);

    /**
     * 根据用户和心情查找配方
     */
    Page<Recipe> findByUserAndMood(User user, MoodType mood, Pageable pageable);

    /**
     * 根据心情查找公开配方
     */
    Page<Recipe> findByMoodAndIsPublicTrue(MoodType mood, Pageable pageable);

    /**
     * 查找所有公开配方
     */
    Page<Recipe> findByIsPublicTrue(Pageable pageable);

    /**
     * 根据名称搜索公开配方
     */
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true AND (r.name LIKE %:keyword% OR r.description LIKE %:keyword%)")
    Page<Recipe> searchPublicRecipes(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 获取热门配方（按点赞数排序）
     */
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true ORDER BY r.likeCount DESC, r.viewCount DESC")
    Page<Recipe> findPopularRecipes(Pageable pageable);

    /**
     * 获取最新配方
     */
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true ORDER BY r.createdAt DESC")
    Page<Recipe> findLatestRecipes(Pageable pageable);

    /**
     * 获取用户的配方数量
     */
    @Query("SELECT COUNT(r) FROM Recipe r WHERE r.user = :user")
    Long countByUser(@Param("user") User user);

    /**
     * 获取最近创建的配方
     */
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true AND r.createdAt > :date ORDER BY r.createdAt DESC")
    List<Recipe> findRecentRecipes(@Param("date") LocalDateTime date);

    /**
     * 获取推荐配方（用户未浏览过的）
     */
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true AND r.id NOT IN (SELECT v.recipe.id FROM ViewHistory v WHERE v.user = :user) ORDER BY r.likeCount DESC")
    Page<Recipe> findRecommendedRecipes(@Param("user") User user, Pageable pageable);

    /**
     * 根据材料搜索配方
     */
    @Query("SELECT DISTINCT r FROM Recipe r JOIN r.recipeIngredients ri JOIN ri.ingredient i WHERE r.isPublic = true AND i.name LIKE %:ingredientName%")
    Page<Recipe> findByIngredientName(@Param("ingredientName") String ingredientName, Pageable pageable);

    /**
     * 统计配方数量
     */
    @Query("SELECT COUNT(r) FROM Recipe r WHERE r.isPublic = true")
    Long countPublicRecipes();

    /**
     * 统计今日新增的配方数量
     */
    @Query("SELECT COUNT(r) FROM Recipe r WHERE r.createdAt >= :startOfDay AND r.createdAt < :endOfDay")
    Long countTodayRecipes(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}