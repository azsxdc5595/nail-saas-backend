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
@Table(name = "SERVICE_ITEM", schema = "NSAS")
@Data
public class ServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_service_item")
    @jakarta.persistence.SequenceGenerator(name = "seq_service_item", sequenceName = "NSAS.SEQ_SERVICE_ITEM", allocationSize = 1)
    @Column(name = "SERVICE_ID")
    private Long id;

    @Column(name = "SERVICE_CODE")
    private String code;

    @Column(name = "MANICURIST_ID")
    private Long manicuristId;

    @Column(name = "SERVICE_NAME")
    private String serviceName;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "DURATION_MIN")
    private Integer durationMin;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
}
