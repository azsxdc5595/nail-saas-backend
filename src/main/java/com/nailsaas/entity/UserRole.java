package com.nailsaas.entity;

import java.time.LocalDateTime;

import com.nailsaas.domain.UserRoleId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "USER_ROLE", schema = "NSAS")
@IdClass(UserRoleId.class)
@Data
public class UserRole {

    @Id
    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Id
    @Column(name = "ROLE_CODE", nullable = false, length = 30)
    private String roleCode;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Integer isActive;

    @Column(name = "GRANTED_BY_USER_ID")
    private Long grantedByUserId;

    @Column(name = "CREATE_TIME", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME", nullable = false)
    private LocalDateTime updateTime;
}
