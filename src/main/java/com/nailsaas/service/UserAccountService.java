
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nailsaas.domain.ConfirmUpdateEmailRequest;
import com.nailsaas.domain.ForgotPasswordRequest;
import com.nailsaas.domain.GetUserInfoReponse;
import com.nailsaas.domain.ResetPasswordRequest;
import com.nailsaas.domain.UpdateEmailRequest;
import com.nailsaas.domain.UpdatePasswordRequest;
import com.nailsaas.domain.UpdateUserRequest;
import com.nailsaas.entity.EmailVerification;
import com.nailsaas.entity.UserAccount;
import com.nailsaas.repository.EmailVerificationRepository;
import com.nailsaas.repository.UserAccountRepository;
import com.nailsaas.util.Generate;
import com.nailsaas.util.SecurityUtil;

@Service
public class UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;
    
    @Autowired
    private Generate generate;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private MailService mailService;

    // 後臺查詢所有帳號 
    public List<UserAccount> findAll() {
        return userAccountRepository.findAll();
    }
    
    // 新增帳號
    public UserAccount register(UserAccount user) {
        Optional<UserAccount> userAccount = userAccountRepository.findByEmail(user.getEmail());

        if (userAccount.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email已被使用");
        }

        user.setCode(generate.generateUuid());
        user.setUserName(user.getUserName());
        user.setPhone(user.getPhone());
        user.setCreateTime(LocalDateTime.now());

        // 密碼加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 帳號尚未驗證
        user.setVerified("0");

        return userAccountRepository.save(user);
    }

    // 查詢
    public GetUserInfoReponse getUserinfo() {
        Optional<UserAccount> optionalUserAccount = Optional.ofNullable(userAccountRepository.findByCode(SecurityUtil.getCurrentUserCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到使用者")));
        UserAccount userAccount = optionalUserAccount.get();
        GetUserInfoReponse reponse = GetUserInfoReponse.builder().userName(userAccount.getUserName()).email(userAccount.getEmail()).phone(userAccount.getPhone()).build();
        return reponse;
    }

    // 註銷
    public void cancelAccount() {
        UserAccount user = userAccountRepository.findByCode(SecurityUtil.getCurrentUserCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到使用者"));

        userAccountRepository.delete(user);
    }

    public void updateMe(UpdateUserRequest req) {

        UserAccount user = userAccountRepository
                .findByCode(SecurityUtil.getCurrentUserCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "找不到使用者"));

        // 只更新有傳的欄位
        if (req.getUserName() != null) {
            user.setUserName(req.getUserName());
        }

        if (req.getPhone() != null) {
            user.setPhone(req.getPhone());
        }

        user.setUpdateTime(LocalDateTime.now());

        userAccountRepository.save(user);
    }

    public void updatePassword(UpdatePasswordRequest req) {

        UserAccount user = userAccountRepository.findByCode(SecurityUtil.getCurrentUserCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "找不到使用者"));

        // 驗證舊密碼
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "舊密碼錯誤");
        }

        // 設定新密碼（加密）
        String encodedPassword = passwordEncoder.encode(req.getNewPassword());

        user.setPassword(encodedPassword);

        user.setUpdateTime(LocalDateTime.now());

        userAccountRepository.save(user);
    }
    
    public void requestEmailChange(UpdateEmailRequest req) {
        
        String code = SecurityUtil.getCurrentUserCode();
        
        UserAccount user = userAccountRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "找不到使用者"));

        // 防重發（60秒內）
        Optional<EmailVerification> latest = emailVerificationRepository.findTopByUserCodeAndEmailOrderByCreateTimeDesc(code, req.getEmail());

        if (latest.isPresent()) {
            LocalDateTime lastTime = latest.get().getCreateTime();

            if (lastTime.isAfter(LocalDateTime.now().minusSeconds(60))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "請稍後再試（60秒限制）");
            }
        }
        
        Optional<UserAccount> userAccount = userAccountRepository.findByEmail(req.getEmail());

        if (userAccount.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email已被使用");
        }
        
        // 產生6碼驗證碼
        String verifyCode = generate.generateCode();

        // 存DB
        EmailVerification ev = new EmailVerification();
        ev.setUserCode(user.getCode());
        ev.setEmail(req.getEmail());
        ev.setVerifyCode(verifyCode);
        ev.setExpireTime(LocalDateTime.now().plusMinutes(5));
        ev.setStatus("PENDING");
        ev.setFailCount(0);
        ev.setCreateTime(LocalDateTime.now());

        emailVerificationRepository.save(ev);

        // 寄信
        mailService.sendMail(
                req.getEmail(),
                "Email驗證碼",
                "您的驗證碼是：" + verifyCode + "（5分鐘內有效）"
        );
    }
    
    public void confirmEmail(ConfirmUpdateEmailRequest req) {

        String code = SecurityUtil.getCurrentUserCode();
        
        EmailVerification ev = emailVerificationRepository.findTopByUserCodeAndEmailAndStatusOrderByCreateTimeDesc(code, req.getEmail(), "PENDING")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "驗證資料不存在"));

        // 過期
        if (ev.getExpireTime().isBefore(LocalDateTime.now())) {
            ev.setStatus("EXPIRED");
            emailVerificationRepository.save(ev);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "驗證碼已過期");
        }

        // 錯誤次數過多
        if (ev.getFailCount() >= 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "驗證失敗過多，請重新取得驗證碼");
        }

        // 驗證碼錯誤
        if (!ev.getVerifyCode().equals(req.getVerifyCode())) {
            ev.setFailCount(ev.getFailCount() + 1);
            emailVerificationRepository.save(ev);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "驗證碼錯誤");
        }

        // 更新 Email
        UserAccount user = userAccountRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "找不到使用者"));

        user.setEmail(req.getEmail());
        user.setUpdateTime(LocalDateTime.now());
        userAccountRepository.save(user);

        // 標記完成
        ev.setStatus("VERIFIED");
        ev.setUpdateTime(LocalDateTime.now());
        emailVerificationRepository.save(ev);
    }
    
    public void forgotPasswordRequest(ForgotPasswordRequest req) {

        UserAccount user = userAccountRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email不存在"));

        // 防重發（60秒）
        Optional<EmailVerification> latest =
                emailVerificationRepository.findTopByUserCodeAndEmailOrderByCreateTimeDesc(
                        user.getCode(), req.getEmail());

        if (latest.isPresent() &&
            latest.get().getCreateTime().isAfter(LocalDateTime.now().minusSeconds(60))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "請稍後再試");
        }

        // 產生OTP
        String code = generate.generateCode();

        // 存DB（重用同一張表）
        EmailVerification ev = new EmailVerification();
        ev.setUserCode(user.getCode());
        ev.setEmail(req.getEmail());
        ev.setVerifyCode(code);
        ev.setExpireTime(LocalDateTime.now().plusMinutes(5));
        ev.setStatus("PENDING");
        ev.setFailCount(0);
        ev.setCreateTime(LocalDateTime.now());

        emailVerificationRepository.save(ev);

        // 寄信
        mailService.sendMail(
                req.getEmail(),
                "重設密碼驗證碼",
                "您的驗證碼是：" + code + "（5分鐘內有效）"
        );
    }

    public void resetPassword(ResetPasswordRequest req) {

        // 找驗證紀錄
        EmailVerification ev =
            emailVerificationRepository
                .findTopByEmailAndStatusOrderByCreateTimeDesc(req.getEmail(), "PENDING")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "驗證資料不存在"));

        // 過期檢查
        if (ev.getExpireTime().isBefore(LocalDateTime.now())) {
            ev.setStatus("EXPIRED");
            emailVerificationRepository.save(ev);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "驗證碼已過期");
        }

        // 錯誤次數
        if (ev.getFailCount() >= 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "驗證失敗過多");
        }

        // 驗證碼比對
        if (!ev.getVerifyCode().equals(req.getVerifyCode())) {
            ev.setFailCount(ev.getFailCount() + 1);
            emailVerificationRepository.save(ev);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "驗證碼錯誤");
        }

        if (req.getNewPassword().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "密碼至少8碼");
        }
        
        // 更新密碼
        UserAccount user = userAccountRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "找不到使用者"));

        String encoded = passwordEncoder.encode(req.getNewPassword());

        user.setPassword(encoded);
        user.setUpdateTime(LocalDateTime.now());

        userAccountRepository.save(user);

        // 標記完成
        ev.setStatus("VERIFIED");
        emailVerificationRepository.save(ev);
    }

}