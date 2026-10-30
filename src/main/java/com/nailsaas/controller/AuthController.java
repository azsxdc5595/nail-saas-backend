package com.nailsaas.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.nailsaas.domain.ForgotPasswordRequest;
import com.nailsaas.domain.LoginRequest;
import com.nailsaas.domain.RegisterRequest;
import com.nailsaas.domain.ResetPasswordRequest;
import com.nailsaas.domain.VerifyEmailRequest;
import com.nailsaas.entity.EmailVerification;
import com.nailsaas.entity.RefreshToken;
import com.nailsaas.entity.UserAccount;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.EmailVerificationRepository;
import com.nailsaas.repository.RefreshTokenRepository;
import com.nailsaas.repository.UserAccountRepository;
import com.nailsaas.security.JwtUtil;
import com.nailsaas.service.MailService;
import com.nailsaas.service.UserAccountService;
import com.nailsaas.util.Generate;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Transactional(noRollbackFor = ResponseStatusException.class)
public class AuthController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private Generate generate;
    
    @Autowired
    private MailService mailService;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest userAccount) {

        UserAccount user= userAccountService.register(userAccount);

        String verifyCode = generate.generateCode();
        
        EmailVerification ev = new EmailVerification();
        ev.setUserCode(user.getCode());
        ev.setEmail(user.getEmail());
        ev.setVerifyCode(verifyCode);
        ev.setExpireTime(LocalDateTime.now().plusMinutes(5));
        ev.setStatus("PENDING");
        ev.setFailCount(0);
        ev.setCreateTime(LocalDateTime.now());

        emailVerificationRepository.save(ev);
        // 🔥 寄信
        mailService.sendMail(
                user.getEmail(),
                "Email驗證碼",
                "您的驗證碼是：" + verifyCode + "（5分鐘內有效）"
        );
        
        return ResponseEntity.ok("註冊成功，請至信箱驗證");
    }
    
    @PostMapping("/verify")
    public Map<String, String> verify(@Valid @RequestBody VerifyEmailRequest req) {

        String email = req.getEmail();
        String code = req.getVerifyCode();

        EmailVerification ev = emailVerificationRepository
                .findTopByEmailAndStatusOrderByCreateTimeDesc(email, "PENDING")
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.AUTH_VERIFY_DATA_NOT_FOUND));

        // 檢查過期
        if (ev.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCodeEnum.AUTH_VERIFY_CODE_EXPIRED);
        }

        // 檢查錯誤次數
        if (ev.getFailCount() >= 5) {
            throw new BusinessException(ErrorCodeEnum.AUTH_VERIFY_FAILED_TOO_MANY);
        }

        // 驗證碼比對
        if (!ev.getVerifyCode().equals(code)) {
            ev.setFailCount(ev.getFailCount() + 1);
            emailVerificationRepository.save(ev);
            throw new BusinessException(ErrorCodeEnum.AUTH_VERIFY_CODE_INCORRECT);
        }

        // 驗證成功，把驗證碼資訊移除
        emailVerificationRepository.deleteByEmail(email);

        UserAccount user = userAccountRepository
                .findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.USER_NOT_FOUND));

        user.setVerified("1");
        userAccountRepository.save(user);

        // 直接登入
        String accessToken = jwtUtil.generateAccessToken(user.getCode());
        String refreshToken = jwtUtil.generateRefreshToken(user.getCode());

        refreshTokenRepository.deleteByUserCode(user.getCode());

        RefreshToken rt = new RefreshToken();
        rt.setUserCode(user.getCode());
        rt.setToken(refreshToken);
        rt.setExpireTime(LocalDateTime.now().plusDays(7));
        rt.setCreateTime(LocalDateTime.now());

        refreshTokenRepository.save(rt);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }
    
    // 登入
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest req, HttpServletResponse response) {

        UserAccount user = userAccountRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.USER_NOT_FOUND));

        if (!"1".equals(user.getVerified())) {
            throw new BusinessException(ErrorCodeEnum.AUTH_EMAIL_NOT_VERIFIED);
        }
        
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCodeEnum.AUTH_PASSWORD_INCORRECT);
        }

        String accessToken = jwtUtil.generateAccessToken(user.getCode());
        String refreshToken = jwtUtil.generateRefreshToken(user.getCode());

        refreshTokenRepository.deleteByUserCode(user.getCode());

        RefreshToken rt = new RefreshToken();
        rt.setUserCode(user.getCode());
        rt.setToken(refreshToken);
        rt.setExpireTime(LocalDateTime.now().plusDays(7));
        rt.setCreateTime(LocalDateTime.now());

        refreshTokenRepository.save(rt);

     // 設定 HttpOnly Cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
//                .secure(true)                 // HTTPS環境
                .secure(false)
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 只回傳 Access Token
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    // 刷新RefreshToken
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        // 1. 確認 Cookie 是否帶有 Refresh Token
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessException(
                    ErrorCodeEnum.AUTH_REFRESH_TOKEN_NOT_FOUND
            );
        }

        // 2. 驗證 Refresh Token 的簽章、期限及 Token 類型
        try {
            jwtUtil.validateRefreshToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new BusinessException(
                    ErrorCodeEnum.AUTH_REFRESH_TOKEN_EXPIRED
            );
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(
                    ErrorCodeEnum.AUTH_REFRESH_TOKEN_INVALID
            );
        }

        // 3. 從已驗證的 Refresh Token 取得使用者代碼
        String userCode = jwtUtil.getUserCode(refreshToken);

        // 4. 比對 DB，確認 Token 與使用者代碼相符
        RefreshToken rt = refreshTokenRepository
                .findByUserCodeAndToken(userCode, refreshToken)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCodeEnum.AUTH_REFRESH_TOKEN_NOT_FOUND
                        )
                );

        // 5. 確認 DB 紀錄的有效期限
        if (rt.getExpireTime() == null
                || !rt.getExpireTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException(
                    ErrorCodeEnum.AUTH_REFRESH_TOKEN_EXPIRED
            );
        }

        // 6. 刪除舊 Refresh Token，執行 Token Rotation
        refreshTokenRepository.deleteByUserCode(userCode);

        // 7. 產生新的 Access Token 與 Refresh Token
        String newAccessToken = jwtUtil.generateAccessToken(userCode);
        String newRefreshToken = jwtUtil.generateRefreshToken(userCode);

        // 8. 建立新的 Refresh Token 資料
        RefreshToken newRt = new RefreshToken();
        newRt.setUserCode(userCode);
        newRt.setToken(newRefreshToken);
        newRt.setExpireTime(LocalDateTime.now().plusDays(7));
        newRt.setCreateTime(LocalDateTime.now());

        refreshTokenRepository.save(newRt);

        // 9. 更新 HttpOnly Cookie
        ResponseCookie cookie = ResponseCookie.from(
                        "refreshToken",
                        newRefreshToken
                )
                .httpOnly(true)
                .secure(false) // 正式 HTTPS 環境應設為 true
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 10. 只回傳新的 Access Token
        return ResponseEntity.ok(
                Map.of("accessToken", newAccessToken)
        );
    }

    // 忘記密碼 - 發送驗證碼
    @PostMapping("/forgot-password/request")
    public String request(@Valid @RequestBody ForgotPasswordRequest req) {
        userAccountService.forgotPasswordRequest(req);
        return "驗證碼已寄出";
    }

    // 忘記密碼 - 重設
    @PostMapping("/forgot-password/reset")
    public String reset(@Valid @RequestBody ResetPasswordRequest req) {
        userAccountService.resetPassword(req);
        return "密碼已重設";
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {

        String userCode = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        refreshTokenRepository.deleteByUserCode(userCode);

        // 清除 Cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("已登出");
    }
}
