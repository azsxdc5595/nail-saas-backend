package com.nailsaas.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();

        // 允許瀏覽器先發送 CORS 預檢請求
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 登入、註冊及刷新 Token 等 Auth API 不在此 Filter 驗證
        // Refresh Token 必須在 AuthService 中另外驗證及比對 DB
        if (uri.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        // 沒有攜帶 Bearer Token 時，交由後續 Spring Security 規則處理
        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            try {
                // 僅允許 Access Token，不接受 Refresh Token
                jwtUtil.validateAccessToken(token);

                // 從已驗證的 Access Token 取得使用者代碼
                String userCode = jwtUtil.getUserCode(token);

                // 建立已認證的 Spring Security 身分
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userCode,
                                null,
                                Collections.emptyList()
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

            } catch (ExpiredJwtException e) {

                SecurityContextHolder.clearContext();

                writeUnauthorizedResponse(
                        response,
                        "Token已過期"
                );
                return;

            } catch (JwtException | IllegalArgumentException e) {

                SecurityContextHolder.clearContext();

                writeUnauthorizedResponse(
                        response,
                        "Token無效或類型錯誤"
                );
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 回傳 HTTP 401 未授權。
     *
     * @param response HTTP 回應
     * @param message 錯誤訊息
     */
    private void writeUnauthorizedResponse(
            HttpServletResponse response,
            String message
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                "{\"message\":\"" + message + "\"}"
        );
    }
}