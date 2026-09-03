
package com.nailsaas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.ApplyServiceItemRequest;
import com.nailsaas.domain.RemoveServiceNameRequest;
import com.nailsaas.domain.SearchServiceItemRequest;
import com.nailsaas.domain.SearchServiceItemResponse;
import com.nailsaas.service.ServiceItemService;

@RestController
@RequestMapping("/serviceItem")
public class ServiceItemController {

    @Autowired
    private ServiceItemService service;

    // 美甲師查詢自己的服務項目
    @GetMapping("/searchMyServiceItem")
    public SearchServiceItemResponse searchMyNailSample(@RequestBody SearchServiceItemRequest req) {
        return service.searchMyServiceItem(req);
    }

    // 美甲師新增自己的服務項目
    @PostMapping("/add")
    public void create(@RequestBody ApplyServiceItemRequest req){
        service.save(req);
    }

    // 美甲師移除自己的服務項目
    @DeleteMapping("/remove")
    public void remove(@RequestBody RemoveServiceNameRequest req){
        service.remove(req);
    }
    
    // 查詢某一位美甲師的服務項目
    @PostMapping("/search")
    public SearchServiceItemResponse searchNailSample(@RequestBody SearchServiceItemRequest req) {
        return service.searchServiceItem(req);
    }
}
