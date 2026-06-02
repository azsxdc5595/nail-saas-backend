
package com.nailsaas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailsaas.entity.ServiceItem;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
}
