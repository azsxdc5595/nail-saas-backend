package com.nailsaas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.ConfirmUpdateEmailRequest;
import com.nailsaas.domain.GetUserInfoReponse;
import com.nailsaas.domain.UpdateEmailRequest;
import com.nailsaas.domain.UpdatePasswordRequest;
import com.nailsaas.domain.UpdateUserRequest;
import com.nailsaas.service.UserAccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserAccountController {

    private final UserAccountService service;

    UserAccountController(UserAccountService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public GetUserInfoReponse getMe() {
        return service.getCurrentUser();
    }

    // 用戶修改個人資訊
    @PatchMapping("/me")
    public void updateMe(@Valid @RequestBody UpdateUserRequest req) {
        service.updateMe(req);
    }

    // 修改密碼
    @PatchMapping("/password")
    public void updatePassword(@Valid @RequestBody UpdatePasswordRequest req) {
        service.updatePassword(req);
    }

    // Email 驗證
    @PostMapping("/email/request")
    public String requestEmailChange(@Valid @RequestBody UpdateEmailRequest req) {
        service.requestEmailChange(req);
        return "驗證碼已發送";
    }

    @PostMapping("/email/confirm")
    public String confirmEmail(@Valid @RequestBody ConfirmUpdateEmailRequest req) {
        service.confirmEmail(req);
        return "Email修改成功";
    }
    
}
