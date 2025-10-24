package com.capofinance.infrastructure;

import com.capofinance.domain.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
    
    // Active people
    List<PersonEntity> findByIsActiveTrue();
    List<PersonEntity> findByIsActiveTrueOrderByName();
    
    // Search
    Optional<PersonEntity> findByNameIgnoreCase(String name);
    Optional<PersonEntity> findByEmail(String email);
    List<PersonEntity> findByNameContainingIgnoreCase(String name);
    
    // Statistics - queries simplificadas sem JOINs complexos
    // Para contar transações por pessoa, melhor fazer queries separadas ou usar repository methods
}
