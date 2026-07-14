
package com.nailsaas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.ApplyManicuristBlockTimeRequest;
import com.nailsaas.domain.GetManicuristBlockTimeReponse;
import com.nailsaas.domain.GetManicuristInfoRequest;
import com.nailsaas.domain.RemoveManicuristBlockTimeRequest;
import com.nailsaas.service.ManicuristBlockTimeService;

@RestController
@RequestMapping("/manicuristBlockTime")
public class ManicuristBlockTimeController {


    @Autowired
    private ManicuristBlockTimeService service;

    @GetMapping("/getBlockTime")
    public GetManicuristBlockTimeReponse getBlockTime(@RequestBody GetManicuristInfoRequest req) {
        return service.getBlockTime(req);
    }
    
    // 美甲師自己增設不可預約時間
    @PostMapping("/add")
    public void addBlockTime(@RequestBody ApplyManicuristBlockTimeRequest req) {
        service.addBlockTime(req);
    }
    
    // 美甲師自己移除不可預約時間
    @DeleteMapping("/remove")
    public void removeBlockTime(@RequestBody RemoveManicuristBlockTimeRequest req) {
        service.removeBlockTime(req);
    }
}
