package com.nailsaas.domain;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NailPostImageRequest {

    @NotNull
    private MultipartFile file;

    @Size(max = 255)
    private String originalName;

    @Positive
    @Max(99999)
    private Integer sortOrder;

    @Max(1)
    @PositiveOrZero
    private Integer isCover;
}
