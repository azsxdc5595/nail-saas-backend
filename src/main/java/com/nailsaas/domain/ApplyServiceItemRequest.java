package com.nailsaas.domain;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplyServiceItemRequest {

    private Long manicuristId;

    @NotBlank
    @Size(max = 200)
    @Pattern(regexp = ".*\\S.*")
    private String serviceName;

    @NotNull
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;
    
    @Size(max = 500)
    private String description;

    @NotNull
    private Integer durationMin;

    @PositiveOrZero
    @Max(1)
    private Integer isActive;

}
