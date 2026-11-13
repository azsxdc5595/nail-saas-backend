package com.nailsaas.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.nailsaas.enums.ReservationStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationResponse {

    private Long reservationId;
    private String reservationCode;
    private Long shopId;
    private Long userId;
    private Long manicuristId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatusEnum status;
    private String initiatorType;
    private Integer totalDurationMin;
    private BigDecimal totalPrice;
    private String customerNote;
    private String manicuristNote;
    private LocalDateTime responseDeadline;
    private List<ReservationServiceResponse> services;
}
