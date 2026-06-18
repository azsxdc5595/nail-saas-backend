
package com.nailsaas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.GetManicuristInfoReponse;
import com.nailsaas.domain.GetManicuristInfoRequest;
import com.nailsaas.service.ManicuristService;

@RestController
@RequestMapping("/manicurist")
public class ManicuristController {


    @Autowired
    private ManicuristService service;

    @GetMapping("/getInfo")
    public GetManicuristInfoReponse getShopInfo(@RequestBody GetManicuristInfoRequest req) {
        return service.getManicuristInfo(req);
    }
}
