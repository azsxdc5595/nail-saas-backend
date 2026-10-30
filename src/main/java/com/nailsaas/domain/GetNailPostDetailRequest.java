package com.nailsaas.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetNailPostDetailRequest {

    /** 作品貼文ID */
    @NotNull
    private Long postId;

}
