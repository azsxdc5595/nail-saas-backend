package com.nailsaas.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetShopInfoReponse {

    private String shopName;

    private String phone;
    
    private String description;
    
    private String address;

}