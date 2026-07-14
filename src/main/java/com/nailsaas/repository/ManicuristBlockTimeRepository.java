
package com.nailsaas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailsaas.entity.ManicuristBlockTime;

public interface ManicuristBlockTimeRepository extends JpaRepository<ManicuristBlockTime, Long> {
    
    List<ManicuristBlockTime> findByManicuristId(Long manicuristId);
    
}
