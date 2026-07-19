package com.nailsaas.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ApplyNailSampleRequest {

    private Long manicuristId;

    private String imageUrl;

    private BigDecimal price;
    
    private String description;

    private String styleCode;

    private String seasonCode;

    private String mainColorCode;

    private Integer enabled;

}
