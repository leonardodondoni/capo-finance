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
public class CreditCardDto {
    private Long id;
    private String name;
    private String lastFourDigits;
    private String cardBrand;
    private Long personId;
    private String personName;
    private Boolean isShared;
    private BigDecimal creditLimit;
    private Integer billingDay;
    private Integer dueDay;
    private Boolean isActive;
}
