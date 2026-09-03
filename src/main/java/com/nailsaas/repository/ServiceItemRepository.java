package com.nailsaas.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nailsaas.entity.ServiceItem;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {

    /**
     * 搜尋服務項目。
     *
     * <p>
     * 當指定 manicuristCode 時，搜尋指定美甲師的所有服務項目，
     * 包含啟用與停用的服務。
     * </p>
     *
     * <p>
     * 當未指定 manicuristCode 時，僅搜尋目前啟用的服務項目。
     * </p>
     *
     * <p>
     * 其他條件皆為可選條件：
     * serviceName 與 description 支援模糊搜尋，
     * price 與 durationMin 支援區間搜尋。
     * </p>
     *
     * @param manicuristCode 美甲師代碼，指定某一位美甲師
     * @param serviceName 服務名稱，支援模糊搜尋
     * @param minPrice 最低價格
     * @param maxPrice 最高價格
     * @param minDurationMin 最短服務時間
     * @param maxDurationMin 最長服務時間
     * @param description 服務描述，支援模糊搜尋
     * @param pageable 分頁條件
     * @return 目前頁面的服務項目資料，以及符合條件的總筆數
     */
    @Query("""
        SELECT n
        FROM ServiceItem n
        JOIN Manicurist m ON m.id = n.manicuristId
        WHERE (
            :manicuristCode IS NOT NULL
            OR n.isActive = 1
        )
          AND (
            :manicuristCode IS NULL
            OR m.code = :manicuristCode
        )
          AND (
            :serviceName IS NULL
            OR n.serviceName LIKE %:serviceName%
        )
          AND (
            :minPrice IS NULL
            OR n.price >= :minPrice
        )
          AND (
            :maxPrice IS NULL
            OR n.price <= :maxPrice
        )
          AND (
            :minDurationMin IS NULL
            OR n.durationMin >= :minDurationMin
        )
          AND (
            :maxDurationMin IS NULL
            OR n.durationMin <= :maxDurationMin
        )
          AND (
            :description IS NULL
            OR n.description LIKE %:description%
        )
        ORDER BY n.createTime DESC
    """)
    Page<ServiceItem> search(
            @Param("manicuristCode") String manicuristCode,
            @Param("serviceName") String serviceName,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minDurationMin") Integer minDurationMin,
            @Param("maxDurationMin") Integer maxDurationMin,
            @Param("description") String description,
            Pageable pageable);
}