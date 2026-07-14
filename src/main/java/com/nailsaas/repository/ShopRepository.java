
package com.nailsaas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nailsaas.entity.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    
    Optional<Shop> findByShopName(String ShopName);
    
    Optional<Shop> findByCode(String shopCode);

    @Query("""
        SELECT s
        FROM Shop s, Manicurist m
        WHERE m.shopId = s.id
          AND m.userId = :userId
    """)
    Optional<Shop> findByUserId(Long userId);
    
}
