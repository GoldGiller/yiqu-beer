package com.cocktailmaker.repository;

import com.cocktailmaker.entity.FoodPairing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodPairingRepository extends JpaRepository<FoodPairing, Long> {

    List<FoodPairing> findByCategory(String category);
}
