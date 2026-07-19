
package com.nailsaas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailsaas.entity.NailSample;

public interface NailSampleRepository extends JpaRepository<NailSample, Long> {
    
    List<NailSample> findByManicuristId(Long manicuristId);
    
}
