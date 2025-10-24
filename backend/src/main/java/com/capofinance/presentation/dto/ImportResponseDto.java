package com.capofinance.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResponseDto {
    private Long importId;
    private String fileName;
    private String fileHash;
    private Integer totalRows;
    private Integer importedRows;
    private Integer skippedRows;
    private Integer errorRows;
    private String status;
    private String message;
}
