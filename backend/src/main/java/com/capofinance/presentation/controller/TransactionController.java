package com.capofinance.presentation.controller;

import com.capofinance.domain.TransactionEntity;
import com.capofinance.infrastructure.TransactionRepository;
import com.capofinance.infrastructure.PersonRepository;
import com.capofinance.infrastructure.CategoryRepository;
import com.capofinance.infrastructure.SubcategoryRepository;
import com.capofinance.presentation.dto.TransactionDto;
import com.capofinance.presentation.dto.TransactionUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for transactions
 * CRUD operations and categorization
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final PersonRepository personRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;

    /**
     * GET /api/transactions/uncategorized
     * List all transactions without category
     */
    @GetMapping("/uncategorized")
    public ResponseEntity<List<TransactionDto>> getUncategorized() {
        List<TransactionEntity> transactions = transactionRepository.findUncategorizedTransactions();
        List<TransactionDto> dtos = transactions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/transactions/search?q=uber
     * Search transactions by description
     */
    @GetMapping("/search")
    public ResponseEntity<List<TransactionDto>> search(@RequestParam("q") String query) {
        List<TransactionEntity> transactions = transactionRepository.searchByDescription(query);
        List<TransactionDto> dtos = transactions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/transactions
     * List all transactions (with optional limit)
     */
    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAll(@RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<TransactionEntity> transactions = transactionRepository.findAll();
        List<TransactionDto> dtos = transactions.stream()
                .limit(limit)
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * PATCH /api/transactions/{id}
     * Update transaction category/notes
     */
    @PatchMapping("/{id}")
    public ResponseEntity<TransactionDto> updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionUpdateDto updateDto) {
        
        return transactionRepository.findById(id)
                .map(transaction -> {
                    if (updateDto.getCategoryId() != null) {
                        transaction.setCategoryId(updateDto.getCategoryId());
                    }
                    if (updateDto.getSubcategoryId() != null) {
                        transaction.setSubcategoryId(updateDto.getSubcategoryId());
                    }
                    if (updateDto.getNotes() != null) {
                        transaction.setNotes(updateDto.getNotes());
                    }
                    
                    TransactionEntity saved = transactionRepository.save(transaction);
                    return ResponseEntity.ok(toDto(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private TransactionDto toDto(TransactionEntity entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .sourceType(entity.getSourceType().name())
                .importId(entity.getImportId())
                .transactionDate(entity.getTransactionDate())
                .description(entity.getDescription())
                .amount(entity.getAmount())
                .balanceAfter(entity.getBalanceAfter())
                .installmentInfo(entity.getInstallmentInfo())
                .cardHolder(entity.getCardHolder())
                .transactionType(entity.getTransactionType().name())
                .accountId(entity.getAccountId())
                .creditCardId(entity.getCreditCardId())
                .personId(entity.getPersonId())
                .personName(entity.getPersonId() != null 
                        ? personRepository.findById(entity.getPersonId()).map(p -> p.getName()).orElse(null)
                        : null)
                .categoryId(entity.getCategoryId())
                .categoryName(entity.getCategoryId() != null 
                        ? categoryRepository.findById(entity.getCategoryId()).map(c -> c.getName()).orElse(null)
                        : null)
                .subcategoryId(entity.getSubcategoryId())
                .subcategoryName(entity.getSubcategoryId() != null 
                        ? subcategoryRepository.findById(entity.getSubcategoryId()).map(s -> s.getName()).orElse(null)
                        : null)
                .notes(entity.getNotes())
                .build();
    }
}
