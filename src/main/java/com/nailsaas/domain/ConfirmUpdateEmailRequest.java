package com.nailsaas.domain;

import lombok.Data;

@Data
public class ConfirmUpdateEmailRequest {

    private String email;
    private String verifyCode;

}