package com.nailsaas.domain;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProposeReservationTimeRequest {

    @NotNull
    private LocalDateTime startTime;
}
