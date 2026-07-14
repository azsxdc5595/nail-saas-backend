package com.nailsaas.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ApplyManicuristBlockTimeRequest {

    private Long manicuristId;

    private LocalDateTime startTime;
    
    private LocalDateTime endTime;

    private String blockType;

    private String reason;

}