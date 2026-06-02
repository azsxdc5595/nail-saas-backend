package com.nailsaas.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "SHOP_INVITE_CODE", schema = "NSAS")
@Data
public class ShopInviteCode {

    @Id
    @Column(name = "CODE")
    private String code;

    @Column(name = "SHOP_ID")
    private Long shopId;

    @Column(name = "EXPIRE_TIME")
    private LocalDateTime expireTime;
    
    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
}