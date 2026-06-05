
package com.nailsaas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.ApplyShopRequest;
import com.nailsaas.domain.GetShopInfoReponse;
import com.nailsaas.domain.GetShopInfoRequest;
import com.nailsaas.service.ShopService;

@RestController
@RequestMapping("/shop")
public class ShopController {


    @Autowired
    private ShopService service;
    
    @PostMapping("/apply")
    public void apply(@RequestBody ApplyShopRequest req){
        service.apply(req);
    }

    @GetMapping("/getInfo")
    public GetShopInfoReponse getShopInfo(@RequestBody GetShopInfoRequest req) {
        return service.getShopInfo(req);
    }
}
