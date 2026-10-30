package com.nailsaas.domain;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateNailPostRequest {

    @NotNull
    private Long postId;

    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    @Size(max = 500)
    private String description;

    @Size(max = 20)
    private String styleCode;

    @Size(max = 20)
    private String seasonCode;

    @Size(max = 20)
    private String mainColorCode;

    @PositiveOrZero
    @Max(1)
    private Integer enabled;

    @Valid
    private List<NailPostImageRequest> images;

}
