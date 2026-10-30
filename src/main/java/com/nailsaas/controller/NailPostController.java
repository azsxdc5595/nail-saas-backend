package com.nailsaas.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.nailsaas.domain.ApplyNailPostRequest;
import com.nailsaas.domain.GetNailPostDetailRequest;
import com.nailsaas.domain.GetNailPostDetailResponse;
import com.nailsaas.domain.RemoveNailPostRequest;
import com.nailsaas.domain.SearchNailPostRequest;
import com.nailsaas.domain.SearchNailPostResponse;
import com.nailsaas.domain.UpdateNailPostRequest;
import com.nailsaas.service.NailPostService;

@RestController
@RequestMapping("/nailPost")
public class NailPostController {

    private final NailPostService service;

    public NailPostController(NailPostService service) {
        this.service = service;
    }

    // 美甲師查看自己的作品貼文
    @GetMapping("/searchMyNailPost")
    public SearchNailPostResponse searchMyNailPost(@RequestBody SearchNailPostRequest req) {
        return service.searchMyNailPost(req);
    }

    // 美甲師新增自己的作品貼文
    @PostMapping("/add")
    public void addNailPost(@Valid @RequestBody ApplyNailPostRequest req) {
        service.addNailPost(req);
    }

    // 美甲師調整自己的作品貼文
    @PatchMapping("/edit")
    public void updateNailPost(@Valid @RequestBody UpdateNailPostRequest req) {
        service.updateNailPost(req);
    }

    // 美甲師移除自己的作品貼文
    @DeleteMapping("/remove")
    public void removeNailPost(@Valid @RequestBody RemoveNailPostRequest req) {
        service.removeNailPost(req);
    }

    // 查詢某一位美甲師的作品貼文
    @PostMapping("/search")
    public SearchNailPostResponse searchNailPost(@RequestBody SearchNailPostRequest req) {
        return service.searchNailPost(req);
    }

    // 查看作品貼文詳細資訊
    @PostMapping("/detail")
    public GetNailPostDetailResponse detailNailPost(@Valid @RequestBody GetNailPostDetailRequest req) {
        return service.detailNailPost(req);
    }
}
