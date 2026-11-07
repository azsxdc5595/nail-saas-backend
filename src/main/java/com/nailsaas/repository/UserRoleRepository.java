package com.nailsaas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nailsaas.domain.UserRoleId;
import com.nailsaas.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    List<UserRole> findByUserIdAndIsActiveOrderByRoleCodeAsc(Long userId, Integer isActive);

    @Query("""
            SELECT userRole.roleCode
            FROM UserRole userRole
            JOIN UserAccount account ON account.id = userRole.userId
            WHERE account.code = :userCode
              AND userRole.isActive = :isActive
            ORDER BY userRole.roleCode
            """)
    List<String> findActiveRoleCodesByUserCode(
            @Param("userCode") String userCode,
            @Param("isActive") Integer isActive);
}
