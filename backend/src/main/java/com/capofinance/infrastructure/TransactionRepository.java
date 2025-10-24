package com.capofinance.infrastructure;

import com.capofinance.domain.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    
    // Basic queries
    List<TransactionEntity> findByPersonIdOrderByTransactionDateDesc(Long personId);
    List<TransactionEntity> findByImportId(Long importId);
    List<TransactionEntity> findByCategoryIdOrderByTransactionDateDesc(Long categoryId);
    List<TransactionEntity> findBySubcategoryIdOrderByTransactionDateDesc(Long subcategoryId);
    
    // Date range queries
    List<TransactionEntity> findByTransactionDateBetweenOrderByTransactionDateDesc(
        LocalDateTime start, 
        LocalDateTime end
    );
    
    Page<TransactionEntity> findByTransactionDateBetween(
        LocalDateTime start,
        LocalDateTime end,
        Pageable pageable
    );
    
    // Account/Card queries
    List<TransactionEntity> findByAccountIdOrderByTransactionDateDesc(Long accountId);
    List<TransactionEntity> findByCreditCardIdOrderByTransactionDateDesc(Long creditCardId);
    
    // Transaction type queries
    List<TransactionEntity> findByTransactionTypeOrderByTransactionDateDesc(
        TransactionEntity.TransactionType transactionType
    );
    
    List<TransactionEntity> findByTransactionTypeAndTransactionDateBetween(
        TransactionEntity.TransactionType transactionType,
        LocalDateTime start,
        LocalDateTime end
    );
    
    // Uncategorized transactions
    @Query("SELECT t FROM TransactionEntity t WHERE t.categoryId IS NULL ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findUncategorizedTransactions();
    
    @Query("SELECT COUNT(t) FROM TransactionEntity t WHERE t.categoryId IS NULL")
    Long countUncategorizedTransactions();
    
    // Search
    @Query("SELECT t FROM TransactionEntity t WHERE " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY t.transactionDate DESC")
    List<TransactionEntity> searchByDescription(@Param("keyword") String keyword);
    
    // Monthly summaries
    @Query("SELECT FUNCTION('DATE_TRUNC', 'month', t.transactionDate) as month, " +
           "t.transactionType, " +
           "COUNT(t), " +
           "SUM(t.amount) " +
           "FROM TransactionEntity t " +
           "WHERE t.transactionDate BETWEEN :start AND :end " +
           "GROUP BY FUNCTION('DATE_TRUNC', 'month', t.transactionDate), t.transactionType " +
           "ORDER BY month DESC")
    List<Object[]> getMonthlySummary(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Spending by category
    @Query("SELECT t.category.name, SUM(t.amount), COUNT(t) " +
           "FROM TransactionEntity t " +
           "WHERE t.category IS NOT NULL " +
           "AND t.transactionType = 'EXPENSE' " +
           "AND t.transactionDate BETWEEN :start AND :end " +
           "GROUP BY t.category.id, t.category.name " +
           "ORDER BY SUM(t.amount) DESC")
    List<Object[]> getSpendingByCategory(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Spending by subcategory
    @Query("SELECT t.category.name, t.subcategory.name, SUM(t.amount), COUNT(t) " +
           "FROM TransactionEntity t " +
           "WHERE t.category IS NOT NULL " +
           "AND t.subcategory IS NOT NULL " +
           "AND t.transactionType = 'EXPENSE' " +
           "AND t.transactionDate BETWEEN :start AND :end " +
           "GROUP BY t.category.id, t.category.name, t.subcategory.id, t.subcategory.name " +
           "ORDER BY SUM(t.amount) DESC")
    List<Object[]> getSpendingBySubcategory(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Spending by person
    @Query("SELECT t.person.name, SUM(t.amount), COUNT(t) " +
           "FROM TransactionEntity t " +
           "WHERE t.person IS NOT NULL " +
           "AND t.transactionType = 'EXPENSE' " +
           "AND t.transactionDate BETWEEN :start AND :end " +
           "GROUP BY t.person.id, t.person.name " +
           "ORDER BY SUM(t.amount) DESC")
    List<Object[]> getSpendingByPerson(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Top expenses
    @Query("SELECT t FROM TransactionEntity t " +
           "WHERE t.transactionType = 'EXPENSE' " +
           "AND t.transactionDate BETWEEN :start AND :end " +
           "ORDER BY t.amount DESC")
    List<TransactionEntity> getTopExpenses(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);
    
    // Recent transactions with full details
    @Query("SELECT t FROM TransactionEntity t " +
           "LEFT JOIN FETCH t.category " +
           "LEFT JOIN FETCH t.subcategory " +
           "LEFT JOIN FETCH t.person " +
           "ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findRecentTransactionsWithDetails(Pageable pageable);
    
    // Total income/expense
    @Query("SELECT SUM(t.amount) FROM TransactionEntity t " +
           "WHERE t.transactionType = :type " +
           "AND t.transactionDate BETWEEN :start AND :end")
    BigDecimal getTotalByType(
        @Param("type") TransactionEntity.TransactionType type,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    // Average transaction amount by category
    @Query("SELECT t.category.name, AVG(t.amount), MIN(t.amount), MAX(t.amount) " +
           "FROM TransactionEntity t " +
           "WHERE t.category IS NOT NULL " +
           "AND t.transactionType = 'EXPENSE' " +
           "AND t.transactionDate BETWEEN :start AND :end " +
           "GROUP BY t.category.id, t.category.name " +
           "ORDER BY AVG(t.amount) DESC")
    List<Object[]> getAverageSpendingByCategory(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Recurring transactions detection (same description, similar amounts)
    @Query("SELECT t.description, COUNT(t), AVG(t.amount), MIN(t.transactionDate), MAX(t.transactionDate) " +
           "FROM TransactionEntity t " +
           "GROUP BY t.description " +
           "HAVING COUNT(t) >= :minOccurrences " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> findPotentialRecurringTransactions(@Param("minOccurrences") Long minOccurrences);
}
