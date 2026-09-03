
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nailsaas.domain.ApplyNailSampleRequest;
import com.nailsaas.domain.GetNailSampleDetailRequest;
import com.nailsaas.domain.GetNailSampleDetailResponse;
import com.nailsaas.domain.RemoveNailSampleRequest;
import com.nailsaas.domain.SearchNailSampleData;
import com.nailsaas.domain.SearchNailSampleRequest;
import com.nailsaas.domain.SearchNailSampleResponse;
import com.nailsaas.entity.NailSample;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.NailSampleRepository;

@Service
public class NailSampleService {

    @Autowired
    private NailSampleRepository nailSampleRepository;

    // 美甲師查看自己的作品
    public SearchNailSampleResponse searchMyNailSample(SearchNailSampleRequest req) {
        return executeNailSampleSearch(req);
    }

    // 美甲師新增自己的作品
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

    // 美甲師移除自己的作品
    public void removeNailSample(RemoveNailSampleRequest req) {
        nailSampleRepository.deleteById(req.getSampleId());
    }

    // 查詢某一位美甲師的作品
    public SearchNailSampleResponse searchNailSample(SearchNailSampleRequest req) {
        return executeNailSampleSearch(req);
    }

    // 查看作品詳細資訊
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

    // 條件查詢作品
    private SearchNailSampleResponse executeNailSampleSearch(SearchNailSampleRequest req) {

        Pageable pageable = req.toPageable();

        Page<NailSample> nailSamplePage =
                nailSampleRepository.search(
                    req.getManicuristCode(),
                    req.getDisplayName(),
                    req.getDescription(),
                    req.getStyleCode(),
                    req.getSeasonCode(),
                    req.getMainColorCode(),
                    req.getMinPrice(),
                    req.getMaxPrice(),
                    pageable
                );

        List<SearchNailSampleData> dataList = new ArrayList<>();

        for (NailSample nailSample : nailSamplePage.getContent()) {

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
        response.setTotalCount((int) nailSamplePage.getTotalElements()
        );

        return response;
    }
}