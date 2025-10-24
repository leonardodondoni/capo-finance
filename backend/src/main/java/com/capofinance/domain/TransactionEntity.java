package com.capofinance.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"transaction_date", "description", "amount", "source_type", "account_id", "credit_card_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Source information
    @Column(name = "source_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @Column(name = "import_id")
    private Long importId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_id", insertable = false, updatable = false)
    private ImportEntity importRecord;

    // Transaction identification
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    // Account/Card linkage
    @Column(name = "account_id")
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private AccountEntity account;

    @Column(name = "credit_card_id")
    private Long creditCardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_card_id", insertable = false, updatable = false)
    private CreditCardEntity creditCard;

    // Classification
    @Column(name = "category_id")
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private CategoryEntity category;

    @Column(name = "subcategory_id")
    private Long subcategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id", insertable = false, updatable = false)
    private SubcategoryEntity subcategory;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", insertable = false, updatable = false)
    private PersonEntity person;

    // Additional fields from CSVs
    @Column(name = "balance_after", precision = 14, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "installment_info", length = 50)
    private String installmentInfo;

    @Column(name = "card_holder", length = 100)
    private String cardHolder;

    // Transaction type
    @Column(name = "transaction_type", length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    // Metadata
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "text[]")
    private String[] tags;

    @Column(name = "is_recurring")
    private Boolean isRecurring;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isRecurring == null) {
            isRecurring = false;
        }
        if (isVerified == null) {
            isVerified = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum SourceType {
        EXTRATO, FATURA
    }

    public enum TransactionType {
        INCOME, EXPENSE, TRANSFER
    }
}
