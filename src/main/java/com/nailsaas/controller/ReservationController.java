
package com.nailsaas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.ApplyNailSampleRequest;
import com.nailsaas.domain.GetManicuristInfoRequest;
import com.nailsaas.domain.GetNailSampleReponse;
import com.nailsaas.domain.RemoveNailSampleRequest;
import com.nailsaas.service.ReservationService;

@RestController
@RequestMapping("/reservation")
public class ReservationController {


    @Autowired
    private ReservationService service;

    // 美甲師查看自己的所有範例
    @GetMapping("/getNailSample")
    public GetNailSampleReponse getNailSample(@RequestBody GetManicuristInfoRequest req) {
        return service.getNailSample(req);
    }
    
    // 美甲師新增自己的範例
    @PostMapping("/add")
    public void addNailSample(@RequestBody ApplyNailSampleRequest req) {
        service.addNailSample(req);
    }
    
    // 美甲師移除自己的範例
    @DeleteMapping("/remove")
    public void removeNailSample(@RequestBody RemoveNailSampleRequest req) {
        service.removeNailSample(req);
    }
}
