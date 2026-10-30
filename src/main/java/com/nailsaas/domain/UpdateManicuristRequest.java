package com.nailsaas.domain;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateManicuristRequest {

    /** Legacy input retained for request compatibility; the service ignores it. */
    private String manicuristCode;

    @Size(max = 50)
    @Pattern(regexp = ".*\\S.*")
    private String displayName;

    @Size(max = 500)
    private String intro;

}
