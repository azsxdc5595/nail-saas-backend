package com.nailsaas.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "EMAIL_VERIFICATION", schema = "NSAS")
@Data
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userCode;

    private String email;

    private String verifyCode;

    private LocalDateTime expireTime;

    private String status;

    private Integer failCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}