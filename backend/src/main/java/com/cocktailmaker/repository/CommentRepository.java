package com.cocktailmaker.repository;

import com.cocktailmaker.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByRecipeIdAndParentIsNullOrderByCreatedAtDesc(Long recipeId, Pageable pageable);

    List<Comment> findByRecipeIdAndParentIsNullOrderByCreatedAtDesc(Long recipeId);

    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.user WHERE c.recipe.id = :recipeId ORDER BY c.createdAt DESC")
    List<Comment> findByRecipeIdWithUser(@Param("recipeId") Long recipeId);

    Long countByRecipeId(Long recipeId);

    @Query("SELECT c FROM Comment c WHERE c.recipe.id = :recipeId AND c.parent.id IS NULL ORDER BY c.likeCount DESC, c.createdAt DESC")
    Page<Comment> findTopLevelByRecipe(@Param("recipeId") Long recipeId, Pageable pageable);

    Optional<Comment> findByIdAndUserId(Long id, Long userId);

    void deleteByRecipeId(Long recipeId);
}
