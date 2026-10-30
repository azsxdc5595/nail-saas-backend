
package com.nailsaas.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nailsaas.entity.NailPost;

public interface NailPostRepository extends JpaRepository<NailPost, Long> {
    
    /**
     * 搜尋作品貼文。
     *
     * <p>
     * 會先依照搜尋條件篩選符合的作品貼文，
     * 再依照 Pageable 取得目前頁面的資料。
     * </p>
     *
     * @param userCode 使用者代碼，指定某一位美甲師
     * @param enabled 啟用狀態；null 表示不限制
     * @param displayName 美甲師對外顯示名稱，支援模糊搜尋
     * @param description 作品貼文描述，支援模糊搜尋
     * @param styleCode 作品貼文風格代碼
     * @param seasonCode 季節代碼
     * @param mainColorCode 主色系代碼
     * @param minPrice 最低價格
     * @param maxPrice 最高價格
     * @param pageable 分頁條件
     * @return 目前頁面的作品貼文資料，以及符合條件的總筆數
     */
    @Query("""
        SELECT n
        FROM NailPost n
        JOIN Manicurist m ON m.id = n.manicuristId
        LEFT JOIN UserAccount u ON u.id = m.userId
        WHERE (
            :userCode IS NULL
            OR u.code = :userCode
        )
          AND (
            :enabled IS NULL
            OR n.enabled = :enabled
        )
          AND (
            :displayName IS NULL
            OR m.displayName LIKE %:displayName%
        )
          AND (
            :description IS NULL
            OR n.description LIKE %:description%
        )
          AND (
            :styleCode IS NULL
            OR n.styleCode = :styleCode
        )
          AND (
            :seasonCode IS NULL
            OR n.seasonCode = :seasonCode
        )
          AND (
            :mainColorCode IS NULL
            OR n.mainColorCode = :mainColorCode
        )
          AND (
            :minPrice IS NULL
            OR n.price >= :minPrice
        )
          AND (
            :maxPrice IS NULL
            OR n.price <= :maxPrice
        )
        ORDER BY n.createTime DESC, n.id DESC
    """)
    Page<NailPost> search(
            @Param("userCode") String userCode,
            @Param("enabled") Integer enabled,
            @Param("displayName") String displayName,
            @Param("description") String description,
            @Param("styleCode") String styleCode,
            @Param("seasonCode") String seasonCode,
            @Param("mainColorCode") String mainColorCode,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    // 查看作品貼文詳細資訊
    Optional<NailPost> findByIdAndEnabled(Long postId, Integer enabled);
}
