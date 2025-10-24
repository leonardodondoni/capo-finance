package com.capofinance.application.csv;

import com.capofinance.infrastructure.CategoryRepository;
import com.capofinance.infrastructure.SubcategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Auto-categorization service based on description keywords
 */
@Service
@RequiredArgsConstructor
public class AutoCategorizationService {

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;

    // Keyword -> (Category name, Subcategory name)
    private static final Map<String, CategoryMapping> KEYWORD_MAPPINGS = new HashMap<>();

    static {
        // Transportation
        KEYWORD_MAPPINGS.put("uber", new CategoryMapping("Basic Needs", "Transportation"));
        KEYWORD_MAPPINGS.put("99", new CategoryMapping("Basic Needs", "Transportation"));
        KEYWORD_MAPPINGS.put("99taxi", new CategoryMapping("Basic Needs", "Transportation"));

        // Pet Care
        KEYWORD_MAPPINGS.put("petlove", new CategoryMapping("Basic Needs", "Pet Care"));
        KEYWORD_MAPPINGS.put("veterinaria", new CategoryMapping("Basic Needs", "Pet Care"));
        KEYWORD_MAPPINGS.put("vet", new CategoryMapping("Basic Needs", "Pet Care"));
        
        // Health
        KEYWORD_MAPPINGS.put("farmacia", new CategoryMapping("Basic Needs", "Health"));
        KEYWORD_MAPPINGS.put("clinica", new CategoryMapping("Basic Needs", "Health"));
        KEYWORD_MAPPINGS.put("rd saude", new CategoryMapping("Basic Needs", "Health"));
        KEYWORD_MAPPINGS.put("biomedic", new CategoryMapping("Basic Needs", "Health"));

        // Subscriptions
        KEYWORD_MAPPINGS.put("amazon", new CategoryMapping("Leisure", "Subscriptions"));
        KEYWORD_MAPPINGS.put("ifood club", new CategoryMapping("Leisure", "Subscriptions"));
        KEYWORD_MAPPINGS.put("youtube", new CategoryMapping("Leisure", "Subscriptions"));
        KEYWORD_MAPPINGS.put("apple.com", new CategoryMapping("Leisure", "Subscriptions"));
        KEYWORD_MAPPINGS.put("google", new CategoryMapping("Leisure", "Subscriptions"));
        KEYWORD_MAPPINGS.put("netflix", new CategoryMapping("Leisure", "Subscriptions"));
        KEYWORD_MAPPINGS.put("spotify", new CategoryMapping("Leisure", "Subscriptions"));

        // Restaurants & Bars
        KEYWORD_MAPPINGS.put("restaurante", new CategoryMapping("Leisure", "Restaurants & Bars"));
        KEYWORD_MAPPINGS.put("bar", new CategoryMapping("Leisure", "Restaurants & Bars"));
        KEYWORD_MAPPINGS.put("ifood", new CategoryMapping("Leisure", "Restaurants & Bars"));

        // Hobbies (Sports/Fitness)
        KEYWORD_MAPPINGS.put("fitness", new CategoryMapping("Leisure", "Hobbies"));
        KEYWORD_MAPPINGS.put("sport", new CategoryMapping("Leisure", "Hobbies"));
        KEYWORD_MAPPINGS.put("academia", new CategoryMapping("Leisure", "Hobbies"));
        KEYWORD_MAPPINGS.put("moinhos", new CategoryMapping("Leisure", "Hobbies"));

        // Personal Care
        KEYWORD_MAPPINGS.put("oboticario", new CategoryMapping("Basic Needs", "Personal Care"));
        KEYWORD_MAPPINGS.put("natura", new CategoryMapping("Basic Needs", "Personal Care"));
        KEYWORD_MAPPINGS.put("costura", new CategoryMapping("Basic Needs", "Clothing"));

        // Utilities
        KEYWORD_MAPPINGS.put("estadual de dist", new CategoryMapping("Basic Needs", "Utilities"));
        KEYWORD_MAPPINGS.put("ceee", new CategoryMapping("Basic Needs", "Utilities"));
        KEYWORD_MAPPINGS.put("dmae", new CategoryMapping("Basic Needs", "Utilities"));

        // Income categories
        KEYWORD_MAPPINGS.put("pix recebido", new CategoryMapping("PIX", "PIX Received"));
        KEYWORD_MAPPINGS.put("transferencia recebida", new CategoryMapping("PIX", "PIX Received"));
    }

    public void categorize(ParsedTransaction transaction) {
        String lowerDesc = transaction.getDescription().toLowerCase();

        // Try to find a matching keyword
        for (Map.Entry<String, CategoryMapping> entry : KEYWORD_MAPPINGS.entrySet()) {
            if (lowerDesc.contains(entry.getKey().toLowerCase())) {
                CategoryMapping mapping = entry.getValue();
                
                // Find category ID
                categoryRepository.findByNameIgnoreCase(mapping.categoryName)
                        .ifPresent(category -> {
                            transaction.setDetectedCategoryId(category.getId());
                            
                            // Find subcategory ID
                            subcategoryRepository.findByCategoryIdAndNameIgnoreCase(
                                    category.getId(), 
                                    mapping.subcategoryName
                            ).ifPresent(subcategory -> 
                                transaction.setDetectedSubcategoryId(subcategory.getId())
                            );
                        });
                
                return; // Stop at first match
            }
        }

        // If no match, leave as null for manual categorization
    }

    private static class CategoryMapping {
        String categoryName;
        String subcategoryName;

        CategoryMapping(String categoryName, String subcategoryName) {
            this.categoryName = categoryName;
            this.subcategoryName = subcategoryName;
        }
    }
}
