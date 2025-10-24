package com.capofinance.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private String sourceType;
    private Long importId;
    private LocalDateTime transactionDate;
    private String description;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String installmentInfo;
    private String cardHolder;
    private String transactionType;
    private Long accountId;
    private Long creditCardId;
    private Long personId;
    private String personName;
    private Long categoryId;
    private String categoryName;
    private Long subcategoryId;
    private String subcategoryName;
    private String notes;
}
