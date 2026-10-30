package com.nailsaas.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";

    private static final String ACCESS_TOKEN_TYPE = "ACCESS";

    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    /**
     * 取得 JWT 簽章金鑰。
     */
    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 產生 Access Token。
     *
     * @param userCode 使用者代碼
     * @return Access Token 字串
     */
    public String generateAccessToken(String userCode) {

        return Jwts.builder()
                .setSubject(userCode)
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + accessExpiration)
                )
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 產生 Refresh Token。
     *
     * @param userCode 使用者代碼
     * @return Refresh Token 字串
     */
    public String generateRefreshToken(String userCode) {

        return Jwts.builder()
                .setSubject(userCode)
                .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + refreshExpiration)
                )
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 JWT，並驗證簽章及有效期限。
     *
     * @param token JWT 字串
     * @return JWT Claims
     */
    public Claims parseToken(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 取得 JWT 中的使用者代碼。
     *
     * @param token JWT 字串
     * @return 使用者代碼
     */
    public String getUserCode(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 驗證 Access Token 的簽章、有效期限及 Token 類型。
     *
     * @param token Access Token 字串
     */
    public void validateAccessToken(String token) {

        Claims claims = parseToken(token);

        validateTokenType(claims, ACCESS_TOKEN_TYPE);
    }

    /**
     * 驗證 Refresh Token 的簽章、有效期限及 Token 類型。
     *
     * @param token Refresh Token 字串
     */
    public void validateRefreshToken(String token) {

        Claims claims = parseToken(token);

        validateTokenType(claims, REFRESH_TOKEN_TYPE);
    }

    /**
     * 確認 JWT 的 Token 類型符合預期。
     *
     * @param claims JWT Claims
     * @param expectedType 預期的 Token 類型
     */
    private void validateTokenType(Claims claims, String expectedType) {

        String actualType = claims.get(TOKEN_TYPE_CLAIM, String.class);

        if (!expectedType.equals(actualType)) {
            throw new IllegalArgumentException("Token 類型錯誤");
        }
    }
}