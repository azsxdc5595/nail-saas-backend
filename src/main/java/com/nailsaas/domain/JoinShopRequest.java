package com.nailsaas.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JoinShopRequest {
    
    @NotBlank
    @Size(max = 50)
    private String inviteCode;
    
}
