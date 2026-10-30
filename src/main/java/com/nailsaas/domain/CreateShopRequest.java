package com.nailsaas.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateShopRequest {
    
    @NotBlank
    @Size(max = 255)
    private String shopName;
    
    @NotBlank
    @Size(max = 255)
    private String address;
    
    @NotBlank
    @Size(max = 50)
    private String phone;
    
    @Size(max = 500)
    private String description;
}
