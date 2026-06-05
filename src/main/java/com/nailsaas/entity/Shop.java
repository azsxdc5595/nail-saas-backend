package com.nailsaas.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "SHOP", schema = "NSAS")
@Data
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SHOP_ID")
    private Long id;

    @Column(name = "SHOP_CODE")
    private String code;

    @Column(name = "SHOP_NAME")
    private String shopName;

    @Column(name = "ADDRESS_ID")
    private Long addressId;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
    
    @Column(name = "ADDRESS")
    private String address;
}