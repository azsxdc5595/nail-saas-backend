package com.nailsaas.domain;

import lombok.Data;

@Data
public class UpdateManicuristRequest {

    private String manicuristCode;
    
    private String displayName;

    private String intro;

}
