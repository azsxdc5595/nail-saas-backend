package com.nailsaas.domain;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReplaceManicuristWorkScheduleRequest {

    @NotNull
    private List<@Valid ManicuristWorkScheduleIntervalRequest> intervals;
}
