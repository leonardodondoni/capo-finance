package com.capofinance.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySpendingDto {
    private String month; // YYYY-MM
    private String categoryName;
    private String subcategoryName;
    private BigDecimal totalAmount;
    private Long transactionCount;
    private BigDecimal averageAmount;
}
