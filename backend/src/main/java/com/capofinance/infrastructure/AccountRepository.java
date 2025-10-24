package com.capofinance.infrastructure;

import com.capofinance.domain.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    
    // Active accounts
    List<AccountEntity> findByIsActiveTrue();
    List<AccountEntity> findByIsActiveTrueOrderByName();
    
    // Shared accounts
    List<AccountEntity> findByIsSharedTrue();
    
    // By person
    List<AccountEntity> findByPersonId(Long personId);
    List<AccountEntity> findByPersonIdAndIsActiveTrue(Long personId);
    
    // By type
    List<AccountEntity> findByAccountType(AccountEntity.AccountType accountType);
    
    // Search
    List<AccountEntity> findByNameContainingIgnoreCase(String name);
    Optional<AccountEntity> findByNameAndPersonId(String name, Long personId);
    
    // Balance queries
    @Query("SELECT a FROM AccountEntity a WHERE a.isActive = true ORDER BY a.currentBalance DESC")
    List<AccountEntity> findActiveAccountsOrderByBalance();
    
    @Query("SELECT SUM(a.currentBalance) FROM AccountEntity a WHERE a.isActive = true")
    java.math.BigDecimal getTotalActiveBalance();
    
    @Query("SELECT SUM(a.currentBalance) FROM AccountEntity a WHERE a.isActive = true AND a.personId = :personId")
    java.math.BigDecimal getTotalBalanceByPerson(@Param("personId") Long personId);
}
