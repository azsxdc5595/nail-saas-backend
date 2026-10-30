package com.nailsaas.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BlacklistRequest {

    private Long manicuristId;
    
    @NotNull
    private Long userId;
    
    @Size(max = 500)
    private String reason;
    
}
