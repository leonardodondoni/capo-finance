package com.capofinance.infrastructure;

import com.capofinance.domain.SubcategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubcategoryRepository extends JpaRepository<SubcategoryEntity, Long> {
    
    // By category
    List<SubcategoryEntity> findByCategoryId(Long categoryId);
    List<SubcategoryEntity> findByCategoryIdOrderBySortOrder(Long categoryId);
    List<SubcategoryEntity> findByCategoryIdAndIsActiveTrue(Long categoryId);
    
    // Active subcategories
    List<SubcategoryEntity> findByIsActiveTrue();
    List<SubcategoryEntity> findByIsActiveTrueOrderByName();
    
    // Search
    Optional<SubcategoryEntity> findByCategoryIdAndNameIgnoreCase(Long categoryId, String name);
    List<SubcategoryEntity> findByNameContainingIgnoreCase(String name);
    
    // Most used subcategories (by transaction count) - simplificada
    @Query("SELECT s FROM SubcategoryEntity s " +
           "WHERE s.isActive = true " +
           "ORDER BY s.sortOrder")
    List<SubcategoryEntity> findMostUsedSubcategories();
    
    // Subcategories by category type
    @Query("SELECT s FROM SubcategoryEntity s " +
           "WHERE s.category.type = :categoryType AND s.isActive = true " +
           "ORDER BY s.category.sortOrder, s.sortOrder")
    List<SubcategoryEntity> findByCategoryType(@Param("categoryType") String categoryType);
}
