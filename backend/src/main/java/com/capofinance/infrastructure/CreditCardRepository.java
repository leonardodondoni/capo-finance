package com.capofinance.infrastructure;

import com.capofinance.domain.CreditCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCardEntity, Long> {
    
    // Active cards
    List<CreditCardEntity> findByIsActiveTrue();
    List<CreditCardEntity> findByIsActiveTrueOrderByName();
    
    // Shared cards
    List<CreditCardEntity> findByIsSharedTrue();
    
    // By person
    List<CreditCardEntity> findByPersonId(Long personId);
    List<CreditCardEntity> findByPersonIdAndIsActiveTrue(Long personId);
    
    // By brand
    List<CreditCardEntity> findByCardBrand(String cardBrand);
    
    // Search
    List<CreditCardEntity> findByNameContainingIgnoreCase(String name);
    Optional<CreditCardEntity> findByNameAndPersonId(String name, Long personId);
    Optional<CreditCardEntity> findByLastFourDigits(String lastFourDigits);
    
    // Billing queries
    @Query("SELECT c FROM CreditCardEntity c WHERE c.isActive = true AND c.billingDay = :day")
    List<CreditCardEntity> findByBillingDay(@Param("day") Integer day);
    
    @Query("SELECT c FROM CreditCardEntity c WHERE c.isActive = true AND c.dueDay = :day")
    List<CreditCardEntity> findByDueDay(@Param("day") Integer day);
    
    // Cards with bills closing soon (next 7 days)
    @Query("SELECT c FROM CreditCardEntity c WHERE c.isActive = true " +
           "AND c.billingDay BETWEEN :currentDay AND :endDay")
    List<CreditCardEntity> findCardsWithUpcomingBillingCycle(
        @Param("currentDay") Integer currentDay, 
        @Param("endDay") Integer endDay
    );
}
