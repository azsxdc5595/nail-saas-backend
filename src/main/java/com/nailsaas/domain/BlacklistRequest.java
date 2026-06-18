package com.nailsaas.domain;

import lombok.Data;

@Data
public class BlacklistRequest {

    private Long manicuristId;
    
    private Long userId;
    
    private String reason;
    
}