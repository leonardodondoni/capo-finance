package com.capofinance.infrastructure;

import com.capofinance.domain.ImportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImportRepository extends JpaRepository<ImportEntity, Long> {
    
    // Duplicate detection
    Optional<ImportEntity> findByFileHash(String fileHash);
    boolean existsByFileHash(String fileHash);
    
    // By type
    List<ImportEntity> findByImportType(ImportEntity.ImportType importType);
    List<ImportEntity> findByImportTypeOrderByImportDateDesc(ImportEntity.ImportType importType);
    
    // By status
    List<ImportEntity> findByStatus(ImportEntity.ImportStatus status);
    List<ImportEntity> findByStatusOrderByImportDateDesc(ImportEntity.ImportStatus status);
    
    // By account/card
    List<ImportEntity> findByAccountIdOrderByImportDateDesc(Long accountId);
    List<ImportEntity> findByCreditCardIdOrderByImportDateDesc(Long creditCardId);
    
    // Recent imports
    @Query("SELECT i FROM ImportEntity i ORDER BY i.importDate DESC")
    List<ImportEntity> findRecentImports();
    
    @Query("SELECT i FROM ImportEntity i WHERE i.importDate >= :since ORDER BY i.importDate DESC")
    List<ImportEntity> findImportsSince(@Param("since") LocalDateTime since);
    
    // Failed/partial imports
    @Query("SELECT i FROM ImportEntity i WHERE i.status IN ('FAILED', 'PARTIAL') ORDER BY i.importDate DESC")
    List<ImportEntity> findFailedOrPartialImports();
    
    // Import statistics
    @Query("SELECT i.importType, COUNT(i), SUM(i.importedRows), SUM(i.skippedRows), SUM(i.errorRows) " +
           "FROM ImportEntity i " +
           "GROUP BY i.importType")
    List<Object[]> getImportStatisticsByType();
    
    @Query("SELECT COUNT(i) FROM ImportEntity i WHERE i.status = 'SUCCESS'")
    Long countSuccessfulImports();
    
    @Query("SELECT SUM(i.importedRows) FROM ImportEntity i WHERE i.status = 'SUCCESS'")
    Long getTotalImportedRows();
}
