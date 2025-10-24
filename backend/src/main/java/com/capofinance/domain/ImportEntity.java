package com.capofinance.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "imports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "import_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ImportType importType;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_hash", nullable = false, length = 64, unique = true)
    private String fileHash;

    @Column(name = "import_date")
    private LocalDateTime importDate;

    @Column(name = "total_rows")
    private Integer totalRows;

    @Column(name = "imported_rows")
    private Integer importedRows;

    @Column(name = "skipped_rows")
    private Integer skippedRows;

    @Column(name = "error_rows")
    private Integer errorRows;

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

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private ImportStatus status;

    @PrePersist
    protected void onCreate() {
        if (importDate == null) {
            importDate = LocalDateTime.now();
        }
        if (status == null) {
            status = ImportStatus.SUCCESS;
        }
    }

    public enum ImportType {
        EXTRATO, FATURA
    }

    public enum ImportStatus {
        SUCCESS, PARTIAL, FAILED
    }
}
