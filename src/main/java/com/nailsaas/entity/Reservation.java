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
@Table(name = "RESERVATION", schema = "NSAS")
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESERVATION_ID")
    private Long id;

    @Column(name = "RESERVATION_CODE")
    private String code;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "MANICURIST_ID")
    private Long manicuristId;

    @Column(name = "RESERVATION_DATE")
    private java.time.LocalDate reservationDate;

    @Column(name = "START_SLOT_ID")
    private Integer startSlotId;

    @Column(name = "SLOT_COUNT")
    private Integer slotCount;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
}