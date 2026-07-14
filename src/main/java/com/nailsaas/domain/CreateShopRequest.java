package com.nailsaas.domain;

import lombok.Data;

@Data
public class CreateShopRequest {
    
    private String shopName;
    
    private Long addressId;
    
    private String phone;
    
    private String description;
}
