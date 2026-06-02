package com.nailsaas.domain;

import lombok.Data;

@Data
public class ApplyShopRequest {
    
    private String shopName;
    
    private Long addressId;
    
    private String phone;
    
    private String description;
}
