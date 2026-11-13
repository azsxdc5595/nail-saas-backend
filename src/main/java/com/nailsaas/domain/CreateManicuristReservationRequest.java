package com.nailsaas.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateManicuristReservationRequest {

    @NotNull
    @Positive
    private Long userId;

    @NotNull
    private LocalDateTime startTime;

    @NotEmpty
    @Size(max = 20)
    private List<@NotNull @Positive Long> serviceIds;

    @Size(max = 500)
    private String manicuristNote;
}
