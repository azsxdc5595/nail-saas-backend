package com.nailsaas.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ApplyServiceItemRequest {

    private Long manicuristId;

    private String serviceName;

    private BigDecimal price;
    
    private String description;

    private Integer durationMin;

    private Integer isActive;

}