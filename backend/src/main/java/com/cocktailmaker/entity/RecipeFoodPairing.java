package com.cocktailmaker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "recipe_food_pairings")
public class RecipeFoodPairing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @Column(name = "food_pairing_id", nullable = false)
    private Long foodPairingId;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }

    public RecipeFoodPairing() {}

    public RecipeFoodPairing(Long recipeId, Long foodPairingId) {
        this.recipeId = recipeId;
        this.foodPairingId = foodPairingId;
    }
}
