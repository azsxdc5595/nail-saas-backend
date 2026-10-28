package com.nailsaas.domain;

import lombok.Data;

@Data
public class CreateShopRequest {
    
    private String shopName;
    
    private String address;
    
    private String phone;
    
    private String description;
}
