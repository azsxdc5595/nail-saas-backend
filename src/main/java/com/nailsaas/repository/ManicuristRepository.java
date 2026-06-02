
package com.nailsaas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailsaas.entity.Manicurist;

public interface ManicuristRepository extends JpaRepository<Manicurist, Long> {
    
    Optional<Manicurist> findByCode(String code);
    
    boolean existsByUserId(Long userId);
}
