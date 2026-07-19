package com.nailsaas.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "PROMOTION", schema = "NSAS")
@Data
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_promotion")
    @jakarta.persistence.SequenceGenerator(name = "seq_promotion", sequenceName = "NSAS.SEQ_PROMOTION", allocationSize = 1)
    @Column(name = "PROMOTION_ID")
    private Long id;

    @Column(name = "MANICURIST_ID")
    private Long manicuristId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DISCOUNT_PERCENT")
    private BigDecimal discountPercent;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;
}
