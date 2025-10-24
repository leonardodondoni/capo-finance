package com.capofinance.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionUpdateDto {
    private Long categoryId;
    private Long subcategoryId;
    private String notes;
}
