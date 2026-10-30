
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailsaas.domain.ApplyServiceItemRequest;
import com.nailsaas.domain.RemoveServiceNameRequest;
import com.nailsaas.domain.SearchServiceItemData;
import com.nailsaas.domain.SearchServiceItemRequest;
import com.nailsaas.domain.SearchServiceItemResponse;
import com.nailsaas.domain.UpdateServiceItemRequest;
import com.nailsaas.entity.ServiceItem;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.ManicuristRepository;
import com.nailsaas.repository.ServiceItemRepository;
import com.nailsaas.util.Generate;
import com.nailsaas.util.SecurityUtil;

@Service
public class ServiceItemService {

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    @Autowired
    private ManicuristRepository manicuristRepository;
    
    @Autowired
    private Generate generate;

    // 美甲師查詢自己的服務項目
    public SearchServiceItemResponse searchMyServiceItem(SearchServiceItemRequest req) {
        req.setUserCode(SecurityUtil.getCurrentUserCode());
        return executeServiceItemSearch(req);
    }

    // 美甲師新增自己的服務項目
    @Transactional
    public void save(ApplyServiceItemRequest req){
        Long manicuristId = manicuristRepository.searchManicuristIdByUserCode(SecurityUtil.getCurrentUserCode());
        if (manicuristId == null) {
            throw new BusinessException(ErrorCodeEnum.PERMISSION_DENIED);
        }
        ServiceItem serviceItem = new ServiceItem();
        serviceItem.setCode(generate.generateUuid());
        serviceItem.setManicuristId(manicuristId);
        serviceItem.setServiceName(req.getServiceName());
        serviceItem.setPrice(req.getPrice());
        serviceItem.setDurationMin(req.getDurationMin());
        serviceItem.setDescription(req.getDescription());
        serviceItem.setIsActive(req.getIsActive() == null ? 1 : req.getIsActive());
        serviceItem.setCreateTime(LocalDateTime.now());
        serviceItemRepository.save(serviceItem);
    }

    // 美甲師移除自己的所有服務項目
    @Transactional
    public void remove(RemoveServiceNameRequest req){
        ServiceItem serviceItem = serviceItemRepository.findById(req.getServiceId())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.SERVICE_ITEM_NOT_FOUND));
        Long manicuristId = manicuristRepository.searchManicuristIdByUserCode(SecurityUtil.getCurrentUserCode());
        if (manicuristId == null || !Objects.equals(serviceItem.getManicuristId(), manicuristId)) {
            throw new BusinessException(ErrorCodeEnum.PERMISSION_DENIED);
        }
        serviceItemRepository.delete(serviceItem);
    }

    // 查詢某一位美甲師的服務項目
    public SearchServiceItemResponse searchServiceItem(SearchServiceItemRequest req) {
        return executeServiceItemSearch(req);
    }

    // 條件查詢服務項目
    public SearchServiceItemResponse executeServiceItemSearch(SearchServiceItemRequest req) {
        Pageable pageable = req.toPageable();

        Page<ServiceItem> serviceItemPage =
                serviceItemRepository.search(
                    req.getUserCode(),
                    req.getServiceName(),
                    req.getMinPrice(),
                    req.getMaxPrice(),
                    req.getMinDurationMin(),
                    req.getMaxDurationMin(),
                    req.getDescription(),
                    pageable
                );

        List<SearchServiceItemData> dataList = new ArrayList<>();

        for (ServiceItem serviceItem : serviceItemPage.getContent()) {

            SearchServiceItemData data = new SearchServiceItemData();

            data.setServiceId(serviceItem.getId());
            data.setManicuristId(serviceItem.getManicuristId());
            data.setServiceName(serviceItem.getServiceName());
            data.setPrice(serviceItem.getPrice());
            data.setDurationMin(serviceItem.getDurationMin());
            data.setDescription(serviceItem.getDescription());
            dataList.add(data);
        }

        SearchServiceItemResponse response = new SearchServiceItemResponse();

        response.setSearchServiceItemDataList(dataList);
        response.setTotalCount((int) serviceItemPage.getTotalElements()
        );

        return response;
    }

    @Transactional
    public void updateServiceItem(UpdateServiceItemRequest req) {
        ServiceItem serviceItem = serviceItemRepository.findById(req.getServiceId())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.SERVICE_ITEM_NOT_FOUND));
        Long manicuristId = manicuristRepository.searchManicuristIdByUserCode(SecurityUtil.getCurrentUserCode());
        if (manicuristId == null || !Objects.equals(serviceItem.getManicuristId(), manicuristId)) {
            throw new BusinessException(ErrorCodeEnum.PERMISSION_DENIED);
        }
        if (req.getServiceName() != null) {
            serviceItem.setServiceName(req.getServiceName());
        }
        if (req.getPrice() != null) {
            serviceItem.setPrice(req.getPrice());
        }
        if (req.getDurationMin() != null) {
            serviceItem.setDurationMin(req.getDurationMin());
        }
        if (req.getDescription() != null) {
            serviceItem.setDescription(req.getDescription());
        }
        if (req.getIsActive() != null) {
            serviceItem.setIsActive(req.getIsActive());
        }
        serviceItem.setUpdateTime(LocalDateTime.now());
        serviceItemRepository.save(serviceItem);
    }
}
