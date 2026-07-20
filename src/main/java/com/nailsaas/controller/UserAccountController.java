package com.nailsaas.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/user")
public class UserAccountController {

    @Autowired
    private UserAccountService service;

    @GetMapping("/me")
    public GetUserInfoReponse getMe() {
        return service.getCurrentUser();
    }

    // 修改名稱
    @PatchMapping("/me")
    public void updateMe(@RequestBody UpdateUserRequest req) {
        service.updateMe(req);
    }

    // 修改密碼
    @PatchMapping("/password")
    public void updatePassword(@RequestBody UpdatePasswordRequest req) {
        service.updatePassword(req);
    }

    // Email 驗證
    @PostMapping("/email/request")
    public String requestEmailChange(@RequestBody UpdateEmailRequest req) {
        service.requestEmailChange(req);
        return "驗證碼已發送";
    }

    @PostMapping("/email/confirm")
    public String confirmEmail(@RequestBody ConfirmUpdateEmailRequest req) {
        service.confirmEmail(req);
        return "Email修改成功";
    }
    
}