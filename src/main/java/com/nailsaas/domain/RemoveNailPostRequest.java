package com.nailsaas.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveNailPostRequest {

    @NotNull
    private Long postId;

}
