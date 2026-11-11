package com.nailsaas.domain;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ManicuristWorkScheduleIntervalRequest {

    @NotNull
    @Min(0)
    @Max(1439)
    private Integer startMinuteOfDay;

    @NotNull
    @Min(1)
    @Max(1440)
    private Integer endMinuteOfDay;

    @AssertTrue(message = "endMinuteOfDay must be greater than startMinuteOfDay")
    public boolean isTimeRangeValid() {
        return startMinuteOfDay == null || endMinuteOfDay == null || endMinuteOfDay > startMinuteOfDay;
    }
}
