package com.nailsaas.domain;

import java.util.List;

import com.nailsaas.entity.ManicuristBlockTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetManicuristBlockTimeReponse {

    private List<ManicuristBlockTime> manicuristBlockTimeList;

}