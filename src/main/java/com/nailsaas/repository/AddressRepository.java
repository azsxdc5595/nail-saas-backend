
package com.nailsaas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailsaas.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    
}
