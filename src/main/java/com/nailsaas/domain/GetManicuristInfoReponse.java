package com.nailsaas.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetManicuristInfoReponse {

    private String displayName;

    private String intro;
    
    private String status;

}