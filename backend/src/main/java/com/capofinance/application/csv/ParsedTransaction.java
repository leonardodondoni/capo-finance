package com.capofinance.application.csv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO representing a parsed transaction row from CSV
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedTransaction {
    private LocalDateTime transactionDate;
    private String description;
    private BigDecimal amount;
    private BigDecimal balanceAfter; // For extrato
    private String installmentInfo; // For fatura
    private String cardHolder; // For fatura
    private String detectedPersonName;
    private Long detectedCategoryId;
    private Long detectedSubcategoryId;
    private String transactionType; // INCOME, EXPENSE
}
