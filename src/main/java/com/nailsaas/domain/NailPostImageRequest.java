package com.nailsaas.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NailPostImageRequest {

    @NotBlank
    @Size(max = 255)
    private String storageName;

    @NotBlank
    @Size(max = 500)
    private String storagePath;

    @Size(max = 255)
    private String originalName;

    @NotBlank
    @Size(max = 100)
    private String contentType;

    @NotNull
    @PositiveOrZero
    private Long fileSize;

    @Positive
    @Max(99999)
    private Integer sortOrder;

    @Max(1)
    @PositiveOrZero
    private Integer isCover;
}
