package com.capofinance.application.csv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of CSV import operation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsvImportResult {
    private Long importId;
    private String fileName;
    private String fileHash;
    private int totalRows;
    private int importedRows;
    private int skippedRows;
    private int errorRows;
    private String status;
    private String message;
}
