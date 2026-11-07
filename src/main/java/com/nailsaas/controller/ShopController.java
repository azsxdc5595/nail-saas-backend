
package com.nailsaas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.CreateShopRequest;
import com.nailsaas.domain.GetShopInfoReponse;
import com.nailsaas.domain.JoinShopRequest;
import com.nailsaas.service.ShopService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/shop")
public class ShopController {


    private final ShopService service;

    ShopController(ShopService service) {
        this.service = service;
    }
    
    // 以店長身份 -> 創建店家
    @PostMapping("/create")
    public void create(@Valid @RequestBody CreateShopRequest req){
        service.createShop(req);
    }

    // 查詢自己店家的資訊
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
    public void joinShop(@Valid @RequestBody JoinShopRequest req) {
        service.joinShop(req);
    }
}
