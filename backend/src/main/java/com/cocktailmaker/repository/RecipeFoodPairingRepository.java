package com.cocktailmaker.repository;

import com.cocktailmaker.entity.RecipeFoodPairing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeFoodPairingRepository extends JpaRepository<RecipeFoodPairing, Long> {

    List<RecipeFoodPairing> findByRecipeId(Long recipeId);

    void deleteByRecipeId(Long recipeId);
}
