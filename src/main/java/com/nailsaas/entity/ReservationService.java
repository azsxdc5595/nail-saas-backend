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
@Table(name = "RESERVATION_SERVICE", schema = "NSAS")
@Data
public class ReservationService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "RESERVATION_ID")
    private Long reservationId;

    @Column(name = "SERVICE_SEQ")
    private Integer serviceSeq;

    @Column(name = "SERVICE_ID")
    private Long serviceId;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "DURATION_MIN")
    private Integer durationMin;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;
}