package com.capofinance.application.csv;

import com.capofinance.domain.*;
import com.capofinance.infrastructure.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Main CSV import service
 * Orchestrates parsing, categorization, and persistence
 */
@Service
@RequiredArgsConstructor
public class CsvImportService {

    private final ExtratoParser extratoParser;
    private final FaturaParser faturaParser;
    private final AutoCategorizationService autoCategorizationService;
    
    private final ImportRepository importRepository;
    private final TransactionRepository transactionRepository;
    private final PersonRepository personRepository;

    /**
     * Import bank statement CSV (extrato)
     */
    @Transactional
    public CsvImportResult importExtrato(MultipartFile file, Long accountId) throws IOException {
        // Step 1: Calculate file hash
        String fileHash = calculateFileHash(file.getInputStream());
        file.getInputStream().reset(); // Reset stream for reading

        // Step 2: Check if already imported
        if (importRepository.existsByFileHash(fileHash)) {
            return CsvImportResult.builder()
                    .fileName(file.getOriginalFilename())
                    .fileHash(fileHash)
                    .status("SKIPPED")
                    .message("File already imported previously")
                    .build();
        }

        // Step 3: Parse CSV
        List<ParsedTransaction> parsedTransactions = extratoParser.parse(file.getInputStream());

        // Step 4: Create import record
        ImportEntity importEntity = ImportEntity.builder()
                .importType(ImportEntity.ImportType.EXTRATO)
                .fileName(file.getOriginalFilename())
                .fileHash(fileHash)
                .accountId(accountId)
                .totalRows(parsedTransactions.size())
                .build();
        importEntity = importRepository.save(importEntity);

        // Step 5: Process and save transactions
        int imported = 0;
        int skipped = 0;
        int errors = 0;

        for (ParsedTransaction parsed : parsedTransactions) {
            try {
                // Auto-categorize
                autoCategorizationService.categorize(parsed);

                // Find person ID
                Long personId = findPersonId(parsed.getDetectedPersonName());

                // Create transaction entity
                TransactionEntity transaction = TransactionEntity.builder()
                        .sourceType(TransactionEntity.SourceType.EXTRATO)
                        .importId(importEntity.getId())
                        .transactionDate(parsed.getTransactionDate())
                        .description(parsed.getDescription())
                        .amount(parsed.getAmount())
                        .balanceAfter(parsed.getBalanceAfter())
                        .accountId(accountId)
                        .personId(personId)
                        .categoryId(parsed.getDetectedCategoryId())
                        .subcategoryId(parsed.getDetectedSubcategoryId())
                        .transactionType(
                            "INCOME".equals(parsed.getTransactionType()) 
                                ? TransactionEntity.TransactionType.INCOME 
                                : TransactionEntity.TransactionType.EXPENSE
                        )
                        .build();

                // Save with duplicate handling
                try {
                    transactionRepository.save(transaction);
                    imported++;
                } catch (Exception e) {
                    // Likely duplicate due to unique constraint
                    skipped++;
                }
            } catch (Exception e) {
                errors++;
                System.err.println("Error processing transaction: " + e.getMessage());
            }
        }

        // Step 6: Update import record with stats
        importEntity.setImportedRows(imported);
        importEntity.setSkippedRows(skipped);
        importEntity.setErrorRows(errors);
        importEntity.setStatus(errors > 0 ? ImportEntity.ImportStatus.PARTIAL : ImportEntity.ImportStatus.SUCCESS);
        importRepository.save(importEntity);

        return CsvImportResult.builder()
                .importId(importEntity.getId())
                .fileName(file.getOriginalFilename())
                .fileHash(fileHash)
                .totalRows(parsedTransactions.size())
                .importedRows(imported)
                .skippedRows(skipped)
                .errorRows(errors)
                .status(importEntity.getStatus().name())
                .message("Import completed successfully")
                .build();
    }

    /**
     * Import credit card bill CSV (fatura)
     */
    @Transactional
    public CsvImportResult importFatura(MultipartFile file, Long creditCardId) throws IOException {
        // Step 1: Calculate file hash
        String fileHash = calculateFileHash(file.getInputStream());
        file.getInputStream().reset(); // Reset stream for reading

        // Step 2: Check if already imported
        if (importRepository.existsByFileHash(fileHash)) {
            return CsvImportResult.builder()
                    .fileName(file.getOriginalFilename())
                    .fileHash(fileHash)
                    .status("SKIPPED")
                    .message("File already imported previously")
                    .build();
        }

        // Step 3: Parse CSV
        List<ParsedTransaction> parsedTransactions = faturaParser.parse(file.getInputStream());

        // Step 4: Create import record
        ImportEntity importEntity = ImportEntity.builder()
                .importType(ImportEntity.ImportType.FATURA)
                .fileName(file.getOriginalFilename())
                .fileHash(fileHash)
                .creditCardId(creditCardId)
                .totalRows(parsedTransactions.size())
                .build();
        importEntity = importRepository.save(importEntity);

        // Step 5: Process and save transactions
        int imported = 0;
        int skipped = 0;
        int errors = 0;

        for (ParsedTransaction parsed : parsedTransactions) {
            try {
                // Auto-categorize
                autoCategorizationService.categorize(parsed);

                // Find person ID
                Long personId = findPersonId(parsed.getDetectedPersonName());

                // Create transaction entity
                TransactionEntity transaction = TransactionEntity.builder()
                        .sourceType(TransactionEntity.SourceType.FATURA)
                        .importId(importEntity.getId())
                        .transactionDate(parsed.getTransactionDate())
                        .description(parsed.getDescription())
                        .amount(parsed.getAmount())
                        .installmentInfo(parsed.getInstallmentInfo())
                        .cardHolder(parsed.getCardHolder())
                        .creditCardId(creditCardId)
                        .personId(personId)
                        .categoryId(parsed.getDetectedCategoryId())
                        .subcategoryId(parsed.getDetectedSubcategoryId())
                        .transactionType(TransactionEntity.TransactionType.EXPENSE)
                        .build();

                // Save with duplicate handling
                try {
                    transactionRepository.save(transaction);
                    imported++;
                } catch (Exception e) {
                    // Likely duplicate due to unique constraint
                    skipped++;
                }
            } catch (Exception e) {
                errors++;
                System.err.println("Error processing transaction: " + e.getMessage());
            }
        }

        // Step 6: Update import record with stats
        importEntity.setImportedRows(imported);
        importEntity.setSkippedRows(skipped);
        importEntity.setErrorRows(errors);
        importEntity.setStatus(errors > 0 ? ImportEntity.ImportStatus.PARTIAL : ImportEntity.ImportStatus.SUCCESS);
        importRepository.save(importEntity);

        return CsvImportResult.builder()
                .importId(importEntity.getId())
                .fileName(file.getOriginalFilename())
                .fileHash(fileHash)
                .totalRows(parsedTransactions.size())
                .importedRows(imported)
                .skippedRows(skipped)
                .errorRows(errors)
                .status(importEntity.getStatus().name())
                .message("Import completed successfully")
                .build();
    }

    private String calculateFileHash(InputStream inputStream) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            
            byte[] hashBytes = digest.digest();
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private Long findPersonId(String personName) {
        return personRepository.findByNameIgnoreCase(personName)
                .map(PersonEntity::getId)
                .orElseGet(() -> {
                    // Default to Leonardo if person not found
                    return personRepository.findByNameIgnoreCase("Leonardo")
                            .map(PersonEntity::getId)
                            .orElse(1L); // Fallback to ID 1
                });
    }
}
