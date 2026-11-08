package com.nailsaas.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.ApplyNailPostRequest;
import com.nailsaas.domain.GetNailPostDetailRequest;
import com.nailsaas.domain.GetNailPostDetailResponse;
import com.nailsaas.domain.RemoveNailPostRequest;
import com.nailsaas.domain.SearchNailPostRequest;
import com.nailsaas.domain.SearchNailPostResponse;
import com.nailsaas.domain.UpdateNailPostRequest;
import com.nailsaas.service.NailPostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/nailPost")
public class NailPostController {

    private final NailPostService service;

    public NailPostController(NailPostService service) {
        this.service = service;
    }

    // 美甲師查看自己的作品貼文
    @GetMapping("/searchMyNailPost")
    @PreAuthorize("hasAnyRole('MANICURIST', 'SHOP_MANAGER', 'ADMIN')")
    public SearchNailPostResponse searchMyNailPost(@ModelAttribute SearchNailPostRequest req) {
        return service.searchMyNailPost(req);
    }

    // 美甲師新增自己的作品貼文
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('MANICURIST', 'SHOP_MANAGER', 'ADMIN')")
    public void addNailPost(@Valid @ModelAttribute ApplyNailPostRequest req) {
        service.addNailPost(req);
    }

    // 美甲師調整自己的作品貼文
    @PatchMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('MANICURIST', 'SHOP_MANAGER', 'ADMIN')")
    public void updateNailPost(@Valid @ModelAttribute UpdateNailPostRequest req) {
        service.updateNailPost(req);
    }

    // 美甲師移除自己的作品貼文
    @DeleteMapping("/remove")
    @PreAuthorize("hasAnyRole('MANICURIST', 'SHOP_MANAGER', 'ADMIN')")
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

    // 美甲師查看自己的作品貼文明細（包含未啟用貼文）
    @PostMapping("/my/detail")
    @PreAuthorize("hasAnyRole('MANICURIST', 'SHOP_MANAGER', 'ADMIN')")
    public GetNailPostDetailResponse detailMyNailPost(@Valid @RequestBody GetNailPostDetailRequest req) {
        return service.detailMyNailPost(req);
    }

    @GetMapping(value = "/image/{imageId}")
    public ResponseEntity<Resource> getImage(@PathVariable Long imageId) {
        var image = service.loadImage(imageId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.contentType()))
                .body(image.resource());
    }
}
