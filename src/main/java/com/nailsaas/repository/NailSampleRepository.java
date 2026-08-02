
package com.nailsaas.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nailsaas.entity.NailSample;

public interface NailSampleRepository extends JpaRepository<NailSample, Long> {
    
    List<NailSample> findByManicuristId(Long manicuristId);
    
    /**
     * 使用者搜尋作品
     */
    @Query("""
        SELECT n
        FROM NailSample n
        WHERE n.enabled = 1
          AND (:description IS NULL OR n.description LIKE %:description%)
          AND (:styleCode IS NULL OR n.styleCode = :styleCode)
          AND (:seasonCode IS NULL OR n.seasonCode = :seasonCode)
          AND (:mainColorCode IS NULL OR n.mainColorCode = :mainColorCode)
          AND (:minPrice IS NULL OR n.price >= :minPrice)
          AND (:maxPrice IS NULL OR n.price <= :maxPrice)
        ORDER BY n.createTime DESC
    """)
    List<NailSample> search(
            @Param("description") String description,
            @Param("styleCode") String styleCode,
            @Param("seasonCode") String seasonCode,
            @Param("mainColorCode") String mainColorCode,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);

    /**
     * 查詢作品詳細
     */
    Optional<NailSample> findByIdAndEnabled(Long sampleId, Integer enabled);
}
