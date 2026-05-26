package com.cocktailmaker.repository;

import com.cocktailmaker.entity.RecipeVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeVersionRepository extends JpaRepository<RecipeVersion, Long> {

    List<RecipeVersion> findByRecipeIdOrderByVersionNumberDesc(Long recipeId);

    Optional<RecipeVersion> findByRecipeIdAndVersionNumber(Long recipeId, Integer versionNumber);

    Long countByRecipeId(Long recipeId);

    void deleteByRecipeId(Long recipeId);
}
