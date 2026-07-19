package com.nailsaas.domain;

import java.util.List;

import com.nailsaas.entity.NailSample;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetNailSampleReponse {

    private List<NailSample> nailSampleList;

}