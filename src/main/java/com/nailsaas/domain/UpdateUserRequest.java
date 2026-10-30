package com.nailsaas.domain;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Size(max = 50)
    @Pattern(regexp = ".*\\S.*")
    private String userName;

    @Size(max = 20)
    @Pattern(regexp = ".*\\S.*")
    private String phone;

}
