package com.nailsaas.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserInfoReponse {

    private String userName;

    private String email;

    private String phone;

}