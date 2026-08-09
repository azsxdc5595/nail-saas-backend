
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nailsaas.domain.ApplyNailSampleRequest;
import com.nailsaas.domain.GetManicuristInfoRequest;
import com.nailsaas.domain.GetNailSampleDetailRequest;
import com.nailsaas.domain.GetNailSampleDetailResponse;
import com.nailsaas.domain.GetNailSampleReponse;
import com.nailsaas.domain.RemoveNailSampleRequest;
import com.nailsaas.domain.SearchNailSampleData;
import com.nailsaas.domain.SearchNailSampleRequest;
import com.nailsaas.domain.SearchNailSampleResponse;
import com.nailsaas.entity.Manicurist;
import com.nailsaas.entity.NailSample;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.ManicuristRepository;
import com.nailsaas.repository.NailSampleRepository;

@Service
public class NailSampleService {
    
    @Autowired
    private ManicuristRepository manicuristRepository;
    
    @Autowired
    private NailSampleRepository nailSampleRepository;

    // 美甲師查看自己的所有範例
    public GetNailSampleReponse getNailSample(GetManicuristInfoRequest req) {
        Optional<Manicurist> optionalManicurist = Optional.ofNullable(manicuristRepository.findByCode(req.getManicuristCode())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.MANICURIST_NOT_FOUND)));
        Manicurist manicurist = optionalManicurist.get();
        List<NailSample> nailSampleList = nailSampleRepository.findByManicuristId(manicurist.getId());
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
        nailSample.setCreateTime(LocalDateTime.now());
        nailSampleRepository.save(nailSample);
    }
    
    // 美甲師移除自己的範例
    public void removeNailSample(RemoveNailSampleRequest req) {
        nailSampleRepository.deleteById(req.getSampleId());
    }
    
    // 使用者搜尋作品
    /**
     * 使用者搜尋作品
     */
    public SearchNailSampleResponse searchNailSample(SearchNailSampleRequest req) {

        Pageable pageable = req.toPageable();

        Page<NailSample> nailSamplePage = nailSampleRepository.search(
                req.getDescription(),
                req.getStyleCode(),
                req.getSeasonCode(),
                req.getMainColorCode(),
                req.getMinPrice(),
                req.getMaxPrice(),
                pageable
        );

        List<NailSample> nailSampleList = nailSamplePage.getContent();

        List<SearchNailSampleData> dataList = new ArrayList<>();

        for (NailSample nailSample : nailSampleList) {

            SearchNailSampleData data = new SearchNailSampleData();

            data.setSampleId(nailSample.getId());
            data.setManicuristId(nailSample.getManicuristId());
            data.setImageUrl(nailSample.getImageUrl());
            data.setPrice(nailSample.getPrice());
            data.setDescription(nailSample.getDescription());
            data.setStyleCode(nailSample.getStyleCode());
            data.setSeasonCode(nailSample.getSeasonCode());
            data.setMainColorCode(nailSample.getMainColorCode());

            dataList.add(data);
        }

        SearchNailSampleResponse response = new SearchNailSampleResponse();

        response.setNailSampleList(dataList);
        response.setTotalCount((int) nailSamplePage.getTotalElements());

        return response;
    }
    
    // 使用者查看作品詳細
    public GetNailSampleDetailResponse detailNailSample(GetNailSampleDetailRequest req) {

        NailSample nailSample = nailSampleRepository
                .findByIdAndEnabled(req.getSampleId(), 1)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.NAIL_SAMPLE_NOT_FOUND));

        GetNailSampleDetailResponse response = new GetNailSampleDetailResponse();

        response.setSampleId(nailSample.getId());
        response.setManicuristId(nailSample.getManicuristId());
        response.setImageUrl(nailSample.getImageUrl());
        response.setPrice(nailSample.getPrice());
        response.setDescription(nailSample.getDescription());
        response.setStyleCode(nailSample.getStyleCode());
        response.setSeasonCode(nailSample.getSeasonCode());
        response.setMainColorCode(nailSample.getMainColorCode());

        return response;
    }
}
