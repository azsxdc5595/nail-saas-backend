package com.nailsaas.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "RESERVATION", schema = "NSAS")
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reservation")
    @SequenceGenerator(
            name = "seq_reservation",
            sequenceName = "NSAS.SEQ_RESERVATION",
            allocationSize = 1)
    @Column(name = "RESERVATION_ID")
    private Long id;

    @Column(name = "RESERVATION_CODE")
    private String code;

    @Column(name = "SHOP_ID")
    private Long shopId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "MANICURIST_ID")
    private Long manicuristId;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "TOTAL_DURATION_MIN")
    private Integer totalDurationMin;

    @Column(name = "TOTAL_PRICE")
    private Integer totalPrice;

    @Column(name = "CUSTOMER_NOTE")
    private String customerNote;

    @Column(name = "MANICURIST_NOTE")
    private String manicuristNote;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

}