
package com.nailsaas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailsaas.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    
    /**
     * 根據縣市與行政區查詢地址資料。
     *
     * @param city 縣市名稱，例如：台北市
     * @param district 行政區名稱，例如：大安區
     * @return 查詢到的行政區資料
     */
    Optional<Address> findByCityAndDistrict(String city, String district);
}
