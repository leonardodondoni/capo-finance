package com.capofinance.presentation.controller;

import com.capofinance.domain.CategoryEntity;
import com.capofinance.domain.SubcategoryEntity;
import com.capofinance.infrastructure.CategoryRepository;
import com.capofinance.infrastructure.SubcategoryRepository;
import com.capofinance.presentation.dto.CategoryDto;
import com.capofinance.presentation.dto.SubcategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for categories and subcategories
 * Read-only endpoints for listing
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;

    /**
     * GET /api/categories
     * List all categories
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        List<CategoryDto> dtos = categories.stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/categories/{id}/subcategories
     * List subcategories for a category
     */
    @GetMapping("/{categoryId}/subcategories")
    public ResponseEntity<List<SubcategoryDto>> getSubcategoriesByCategory(@PathVariable Long categoryId) {
        List<SubcategoryEntity> subcategories = subcategoryRepository.findByCategoryIdOrderBySortOrder(categoryId);
        List<SubcategoryDto> dtos = subcategories.stream()
                .map(this::toSubcategoryDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/categories/subcategories
     * List all subcategories
     */
    @GetMapping("/subcategories")
    public ResponseEntity<List<SubcategoryDto>> getAllSubcategories() {
        List<SubcategoryEntity> subcategories = subcategoryRepository.findAll();
        List<SubcategoryDto> dtos = subcategories.stream()
                .map(this::toSubcategoryDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private CategoryDto toCategoryDto(CategoryEntity entity) {
        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .type(entity.getType().name())
                .build();
    }

    private SubcategoryDto toSubcategoryDto(SubcategoryEntity entity) {
        return SubcategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .categoryId(entity.getCategoryId())
                .categoryName(categoryRepository.findById(entity.getCategoryId())
                        .map(CategoryEntity::getName)
                        .orElse(null))
                .build();
    }
}
