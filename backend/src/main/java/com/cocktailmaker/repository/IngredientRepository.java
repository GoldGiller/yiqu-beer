package com.cocktailmaker.repository;

import com.cocktailmaker.entity.Ingredient;
import com.cocktailmaker.enums.IngredientType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 材料数据访问层
 */
@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /**
     * 根据类型查找材料
     */
    List<Ingredient> findByType(IngredientType type);

    /**
     * 根据类型分页查找材料
     */
    Page<Ingredient> findByType(IngredientType type, Pageable pageable);

    /**
     * 根据名称查找材料
     */
    Optional<Ingredient> findByName(String name);

    /**
     * 检查材料名称是否存在
     */
    Boolean existsByName(String name);

    /**
     * 根据名称模糊搜索材料
     */
    @Query("SELECT i FROM Ingredient i WHERE i.name LIKE %:keyword%")
    Page<Ingredient> searchByName(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查找含酒精的材料
     */
    @Query("SELECT i FROM Ingredient i WHERE i.alcoholContent > 0 ORDER BY i.alcoholContent DESC")
    List<Ingredient> findAlcoholicIngredients();

    /**
     * 查找热门材料（按使用频率排序）
     */
    @Query("SELECT i, COUNT(ri) as usageCount FROM Ingredient i JOIN i.recipeIngredients ri GROUP BY i ORDER BY usageCount DESC")
    List<Object[]> findPopularIngredients();

    /**
     * 根据类型和名称搜索材料
     */
    @Query("SELECT i FROM Ingredient i WHERE (:type IS NULL OR i.type = :type) AND (:keyword IS NULL OR i.name LIKE %:keyword%)")
    Page<Ingredient> findByTypeAndName(@Param("type") IngredientType type, @Param("keyword") String keyword, Pageable pageable);
}