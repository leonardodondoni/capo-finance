package com.capofinance.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubcategoryDto {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
}
