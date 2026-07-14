
package com.nailsaas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.nailsaas.entity.ShopInviteCode;

public interface ShopInviteCodeRepository extends JpaRepository<ShopInviteCode, String> {
    
    Optional<ShopInviteCode> findByCode(String code);
    
    @Modifying
    @Query("""
        UPDATE ShopInviteCode s
        SET s.status = :newStatus,
            s.updateTime = CURRENT_TIMESTAMP
        WHERE s.code = :code
          AND s.status = :oldStatus
    """)
    int useInviteCode(String code,
                      String oldStatus,
                      String newStatus);
    
    Optional<ShopInviteCode> findByShopIdAndStatus(Long shopId, String status);
}
