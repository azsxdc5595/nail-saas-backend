package com.nailsaas.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "REFRESH_TOKEN", schema = "NSAS")
@Data
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_token_seq")
    @jakarta.persistence.SequenceGenerator(name = "refresh_token_seq", sequenceName = "NSAS.REFRESH_TOKEN_SEQ", allocationSize = 1)
    private Long id;

    private String userCode;

    private String token;

    private LocalDateTime expireTime;

    private LocalDateTime createTime;
}
