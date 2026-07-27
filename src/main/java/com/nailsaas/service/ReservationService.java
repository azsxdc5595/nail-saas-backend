
package com.nailsaas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nailsaas.domain.ApplyNailSampleRequest;
import com.nailsaas.domain.GetManicuristInfoRequest;
import com.nailsaas.domain.GetNailSampleReponse;
import com.nailsaas.domain.RemoveNailSampleRequest;
import com.nailsaas.entity.Manicurist;
import com.nailsaas.entity.NailSample;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.ManicuristRepository;
import com.nailsaas.repository.NailSampleRepository;

@Service
public class ReservationService {
    
    @Autowired
    private ManicuristRepository manicuristRepository;
    
    @Autowired
    private NailSampleRepository NailSampleRepository;

    // 美甲師查看自己的所有範例
    public GetNailSampleReponse getNailSample(GetManicuristInfoRequest req) {
        Optional<Manicurist> optionalManicurist = Optional.ofNullable(manicuristRepository.findByCode(req.getManicuristCode())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.MANICURIST_NOT_FOUND)));
        Manicurist manicurist = optionalManicurist.get();
        List<NailSample> nailSampleList = NailSampleRepository.findByManicuristId(manicurist.getId());
        GetNailSampleReponse reponse = GetNailSampleReponse.builder().nailSampleList(nailSampleList).build();
        return reponse;
    }

    // 美甲師新增自己的範例
    public void addNailSample(ApplyNailSampleRequest req) {
        NailSample nailSample = new NailSample();
        nailSample.setManicuristId(req.getManicuristId());
        nailSample.setImageUrl(req.getImageUrl());
        nailSample.setPrice(req.getPrice());
        nailSample.setDescription(req.getDescription());
        nailSample.setStyleCode(req.getStyleCode());
        nailSample.setSeasonCode(req.getSeasonCode());
        nailSample.setMainColorCode(req.getMainColorCode());
        nailSample.setEnabled(req.getEnabled() == null ? 1 : req.getEnabled());
        NailSampleRepository.save(nailSample);
    }
    
    // 美甲師移除自己的範例
    public void removeNailSample(RemoveNailSampleRequest req) {
        NailSampleRepository.deleteById(req.getSampleId());
    }
}
