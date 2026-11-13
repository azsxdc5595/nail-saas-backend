package com.nailsaas.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationServiceResponse {

    private Long serviceId;
    private String serviceName;
    private BigDecimal price;
    private Integer durationMin;
}
