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
@Table(name = "MANICURIST", schema = "NSAS")
@Data
public class Manicurist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MANICURIST_ID")
    private Long id;

    @Column(name = "MANICURIST_CODE")
    private String code;

    @Column(name = "SHOP_ID")
    private Long shopId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "INTRO")
    private String intro;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
    
    @Column(name = "STATUS")
    private String status;
    
    @Column(name = "DISPLAY_NAME")
    private String displayName;
    
    @Column(name = "ROLE")
    private String role;
}