package com.nailsaas.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordRequest {

    @NotBlank
    private String oldPassword;

    private String newPassword;

}
