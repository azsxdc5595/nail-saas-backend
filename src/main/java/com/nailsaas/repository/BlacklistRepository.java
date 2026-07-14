
package com.nailsaas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailsaas.entity.ManicuristBlacklist;

public interface BlacklistRepository extends JpaRepository<ManicuristBlacklist, Long> {
    
    List<ManicuristBlacklist> findByManicuristId(Long manicuristId);
    
    boolean existsByManicuristIdAndUserId(Long manicuristId, Long userId);
    
    int deleteByManicuristIdAndUserId(Long manicuristId, Long userId);

}
