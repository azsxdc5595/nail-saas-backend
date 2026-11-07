
package com.nailsaas.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.GetManicuristInfoReponse;
import com.nailsaas.domain.UpdateManicuristRequest;
import com.nailsaas.service.ManicuristService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/manicurist")
@PreAuthorize("hasAnyRole('MANICURIST', 'SHOP_MANAGER', 'ADMIN')")
public class ManicuristController {


    private final ManicuristService service;

    ManicuristController(ManicuristService service) {
        this.service = service;
    }

    // 美甲師查詢自己美甲師資訊
    @GetMapping("/me")
    public GetManicuristInfoReponse getShopInfo() {
        return service.getManicuristInfo();
    }
    
    // 美甲師修改自己美甲師資訊
    @PatchMapping("/me")
    public void updateMe(@Valid @RequestBody UpdateManicuristRequest req) {
        service.updateMe(req);
    }
}
