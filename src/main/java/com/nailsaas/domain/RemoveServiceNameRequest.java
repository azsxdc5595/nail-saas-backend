package com.nailsaas.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveServiceNameRequest {

    @NotNull
    private Long serviceId;

}
