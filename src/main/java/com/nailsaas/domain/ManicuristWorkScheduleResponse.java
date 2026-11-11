package com.nailsaas.domain;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ManicuristWorkScheduleResponse {

    private LocalDate workDate;
    private List<ManicuristWorkScheduleIntervalResponse> intervals;
}
