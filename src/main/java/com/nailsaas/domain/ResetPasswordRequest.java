package com.nailsaas.domain;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String email;
    private String verifyCode;
    private String newPassword;

}