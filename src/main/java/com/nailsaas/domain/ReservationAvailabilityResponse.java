package com.nailsaas.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationAvailabilityResponse {

    private Long manicuristId;
    private LocalDate workDate;
    private Integer totalDurationMin;
    private List<LocalDateTime> availableStartTimes;
}
