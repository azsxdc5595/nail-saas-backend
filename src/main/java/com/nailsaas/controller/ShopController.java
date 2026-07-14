
package com.nailsaas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.CreateShopRequest;
import com.nailsaas.domain.GetShopInfoReponse;
import com.nailsaas.domain.JoinShopRequest;
import com.nailsaas.service.ShopService;

@RestController
@RequestMapping("/shop")
public class ShopController {


    @Autowired
    private ShopService service;
    
    // 以店長身份 -> 創建店家
    @PostMapping("/create")
    public void create(@RequestBody CreateShopRequest req){
        service.createShop(req);
    }

    @GetMapping("/me")
    public GetShopInfoReponse me() {
        return service.me();
    }
    
    // 新增一組邀請碼
    @PostMapping("/invite")
    public String invite() {
        return service.invite();
    }
    
    // 以美甲師身份 -> 加入店家
    @PostMapping("/join")
    public void joinShop(@RequestBody JoinShopRequest req) {
        service.joinShop(req);
    }
}
