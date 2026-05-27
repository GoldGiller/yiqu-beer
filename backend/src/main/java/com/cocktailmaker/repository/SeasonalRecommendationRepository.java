package com.cocktailmaker.repository;

import com.cocktailmaker.entity.SeasonalRecommendation;
import com.cocktailmaker.enums.SeasonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeasonalRecommendationRepository extends JpaRepository<SeasonalRecommendation, Long> {

    List<SeasonalRecommendation> findBySeasonAndActiveTrueOrderByPriorityDesc(SeasonType season);

    void deleteByRecipeId(Long recipeId);
}
