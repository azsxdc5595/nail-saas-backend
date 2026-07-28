package com.nailsaas.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "RESERVATION_ITEM", schema = "NSAS")
@IdClass(ReservationItemId.class)
@Data
public class ReservationItem {

    @Id
    @Column(name = "RESERVATION_ID")
    private Long reservationId;

    @Id
    @Column(name = "SERVICE_SEQ")
    private Long serviceSeq;

    @Column(name = "SERVICE_ID")
    private Long serviceId;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "DURATION_MIN")
    private Integer durationMin;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;
}
