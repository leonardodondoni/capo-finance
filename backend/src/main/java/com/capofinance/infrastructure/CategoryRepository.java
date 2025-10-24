package com.capofinance.infrastructure;

import com.capofinance.domain.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    
    // By type
    List<CategoryEntity> findByType(CategoryEntity.CategoryType type);
    List<CategoryEntity> findByTypeOrderBySortOrder(CategoryEntity.CategoryType type);
    
    // Active categories
    List<CategoryEntity> findByIsActiveTrue();
    List<CategoryEntity> findByIsActiveTrueOrderBySortOrder();
    
    // Search
    Optional<CategoryEntity> findByNameIgnoreCase(String name);
    List<CategoryEntity> findByNameContainingIgnoreCase(String name);
    
    // Expense categories (most used)
    @Query("SELECT c FROM CategoryEntity c WHERE c.type = 'EXPENSE' AND c.isActive = true ORDER BY c.sortOrder")
    List<CategoryEntity> findActiveExpenseCategories();
    
    // Income categories
    @Query("SELECT c FROM CategoryEntity c WHERE c.type = 'INCOME' AND c.isActive = true ORDER BY c.sortOrder")
    List<CategoryEntity> findActiveIncomeCategories();
    
    // Category with subcategory count
    @Query("SELECT c, COUNT(s) FROM CategoryEntity c " +
           "LEFT JOIN SubcategoryEntity s ON s.category = c " +
           "WHERE c.isActive = true " +
           "GROUP BY c ORDER BY c.sortOrder")
    List<Object[]> findActiveCategoriesWithSubcategoryCount();
}
