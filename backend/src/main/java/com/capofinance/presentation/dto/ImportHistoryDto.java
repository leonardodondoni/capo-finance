package com.capofinance.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportHistoryDto {
    private Long id;
    private String importType;
    private String fileName;
    private LocalDateTime importDate;
    private Integer totalRows;
    private Integer importedRows;
    private Integer skippedRows;
    private Integer errorRows;
    private String status;
    private Long accountId;
    private String accountName;
    private Long creditCardId;
    private String creditCardName;
    private String notes;
}
