
package com.nailsaas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailsaas.entity.ReservationBlockTime;

public interface ReservationBlockTimeRepository extends JpaRepository<ReservationBlockTime, Long> {
    
    List<ReservationBlockTime> findByManicuristId(Long manicuristId);
    
}
