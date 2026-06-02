package com.nailsaas.domain;

import lombok.Data;

@Data
public class UpdatePasswordRequest {

    private String oldPassword;
    private String newPassword;

}