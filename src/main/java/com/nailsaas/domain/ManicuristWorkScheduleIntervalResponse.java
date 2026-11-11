package com.nailsaas.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ManicuristWorkScheduleIntervalResponse {

    private Integer startMinuteOfDay;
    private Integer endMinuteOfDay;
}
