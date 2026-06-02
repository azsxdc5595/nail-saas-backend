package com.nailsaas.domain;

import lombok.Data;

@Data
public class VerifyEmailRequest {

    private String email;
    private String verifyCode;

}