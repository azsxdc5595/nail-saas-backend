
package com.nailsaas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailsaas.entity.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    
    Optional<Shop> findByShopName(String ShopName);
    
}
