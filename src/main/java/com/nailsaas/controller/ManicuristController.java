
package com.nailsaas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.GetManicuristInfoReponse;
import com.nailsaas.domain.GetManicuristInfoRequest;
import com.nailsaas.domain.UpdateManicuristRequest;
import com.nailsaas.service.ManicuristService;

@RestController
@RequestMapping("/manicurist")
public class ManicuristController {


    @Autowired
    private ManicuristService service;

    // 美甲師查詢自己美甲師資訊
    @GetMapping("/me")
    public GetManicuristInfoReponse getShopInfo(@RequestBody GetManicuristInfoRequest req) {
        return service.getManicuristInfo(req);
    }
    
    // 美甲師修改自己美甲師資訊
    @PatchMapping("/me")
    public void updateMe(@RequestBody UpdateManicuristRequest req) {
        service.updateMe(req);
    }
}
