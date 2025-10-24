package com.capofinance.presentation.controller;

import com.capofinance.application.csv.CsvImportResult;
import com.capofinance.application.csv.CsvImportService;
import com.capofinance.presentation.dto.ImportResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for CSV imports
 * Handles extrato (bank statement) and fatura (credit card bill) uploads
 */
@RestController
@RequestMapping("/api/imports")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ImportController {

    private final CsvImportService csvImportService;

    /**
     * POST /api/imports/extrato?accountId=1
     * Upload bank statement CSV
     */
    @PostMapping(value = "/extrato", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResponseDto> importExtrato(
            @RequestParam("file") MultipartFile file,
            @RequestParam("accountId") Long accountId) {
        
        try {
            CsvImportResult result = csvImportService.importExtrato(file, accountId);
            
            ImportResponseDto response = ImportResponseDto.builder()
                    .importId(result.getImportId())
                    .fileName(result.getFileName())
                    .fileHash(result.getFileHash())
                    .totalRows(result.getTotalRows())
                    .importedRows(result.getImportedRows())
                    .skippedRows(result.getSkippedRows())
                    .errorRows(result.getErrorRows())
                    .status(result.getStatus())
                    .message(result.getMessage())
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ImportResponseDto errorResponse = ImportResponseDto.builder()
                    .fileName(file.getOriginalFilename())
                    .status("ERROR")
                    .message("Failed to import file: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * POST /api/imports/fatura?creditCardId=1
     * Upload credit card bill CSV
     */
    @PostMapping(value = "/fatura", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResponseDto> importFatura(
            @RequestParam("file") MultipartFile file,
            @RequestParam("creditCardId") Long creditCardId) {
        
        try {
            CsvImportResult result = csvImportService.importFatura(file, creditCardId);
            
            ImportResponseDto response = ImportResponseDto.builder()
                    .importId(result.getImportId())
                    .fileName(result.getFileName())
                    .fileHash(result.getFileHash())
                    .totalRows(result.getTotalRows())
                    .importedRows(result.getImportedRows())
                    .skippedRows(result.getSkippedRows())
                    .errorRows(result.getErrorRows())
                    .status(result.getStatus())
                    .message(result.getMessage())
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ImportResponseDto errorResponse = ImportResponseDto.builder()
                    .fileName(file.getOriginalFilename())
                    .status("ERROR")
                    .message("Failed to import file: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
