
package com.nailsaas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nailsaas.entity.Manicurist;

public interface ManicuristRepository extends JpaRepository<Manicurist, Long> {
    
    Optional<Manicurist> findByCode(String code);

    @Query("""
        SELECT m
        FROM Manicurist m
        JOIN UserAccount u ON u.id = m.userId
        WHERE u.code = :userCode
    """)
    Optional<Manicurist> findManicuristByUserCode(@Param("userCode") String userCode);

    /**
     * 搜尋美甲師ID。
     *
     * @param userCode 使用者代碼
     * @return 美甲師ID
     */
    @Query("""
        SELECT m.id
        FROM Manicurist m
        JOIN UserAccount u ON u.id = m.userId
        WHERE u.code = :userCode
    """)
    Long searchManicuristIdByUserCode(@Param("userCode") String userCode);
    
    boolean existsByUserId(Long userId);
}
