package com.nailsaas.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCodeEnum {

    // User
    USER_NOT_FOUND("USER_001", HttpStatus.NOT_FOUND, "找不到使用者"),
    USER_EMAIL_DUPLICATE("USER_002", HttpStatus.CONFLICT, "Email已被使用"),
    USER_OLD_PASSWORD_INCORRECT("USER_003", HttpStatus.BAD_REQUEST, "舊密碼錯誤"),

    // Shop
    SHOP_NOT_FOUND("SHOP_001", HttpStatus.NOT_FOUND, "找不到店家"),
    SHOP_NAME_ALREADY_EXISTS("SHOP_002", HttpStatus.CONFLICT, "店名已被使用"),

    // Manicurist
    MANICURIST_ALREADY_EXISTS("MANICURIST_001", HttpStatus.CONFLICT, "已經是美甲師"),
    MANICURIST_NOT_FOUND("MANICURIST_002", HttpStatus.NOT_FOUND, "找不到美甲師資料"),

    // Invite
    INVITE_CODE_NOT_FOUND("INVITE_001", HttpStatus.BAD_REQUEST, "邀請碼不存在"),
    INVITE_CODE_USED("INVITE_002", HttpStatus.BAD_REQUEST, "邀請碼已被使用"),
    INVITE_CODE_EXPIRED("INVITE_003", HttpStatus.BAD_REQUEST, "邀請碼已過期"),
    INVITE_CODE_INVALID("INVITE_004", HttpStatus.BAD_REQUEST, "邀請碼不可使用"),
    
    // Manicurist
    NAIL_SAMPLE_NOT_FOUND("NAIL_SAMPLE_001", HttpStatus.BAD_REQUEST, "款式不存在"),

    // Auth
    AUTH_EMAIL_NOT_VERIFIED("AUTH_001", HttpStatus.BAD_REQUEST, "請先完成Email驗證"),
    AUTH_VERIFY_CODE_INCORRECT("AUTH_002", HttpStatus.BAD_REQUEST, "驗證碼錯誤"),
    AUTH_VERIFY_CODE_EXPIRED("AUTH_003", HttpStatus.BAD_REQUEST, "驗證碼已過期"),
    AUTH_VERIFY_FAILED_TOO_MANY("AUTH_004", HttpStatus.BAD_REQUEST, "驗證失敗過多，請重新取得驗證碼"),
    AUTH_PASSWORD_INCORRECT("AUTH_005", HttpStatus.BAD_REQUEST, "密碼錯誤"),
    AUTH_SEND_TOO_FREQUENT("AUTH_006", HttpStatus.BAD_REQUEST, "驗證碼發送過於頻繁，請稍後再試"),
    AUTH_REFRESH_TOKEN_NOT_FOUND("AUTH_007", HttpStatus.UNAUTHORIZED, "Refresh Token不存在"),
    AUTH_REFRESH_TOKEN_EXPIRED("AUTH_008", HttpStatus.UNAUTHORIZED, "Refresh Token過期"),
    AUTH_REFRESH_TOKEN_INVALID("AUTH_009", HttpStatus.UNAUTHORIZED, "Refresh Token無效"),
    AUTH_PASSWORD_TOO_SHORT("AUTH_010", HttpStatus.BAD_REQUEST, "密碼至少8碼"),
    AUTH_VERIFY_DATA_NOT_FOUND("AUTH_011", HttpStatus.BAD_REQUEST, "驗證資料不存在"),

    // Blacklist
    BLACKLIST_ALREADY_EXISTS("BLACKLIST_001", HttpStatus.CONFLICT, "已經加入黑名單"),
    BLACKLIST_NOT_FOUND("BLACKLIST_002", HttpStatus.NOT_FOUND, "此用戶不存在於黑名單");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCodeEnum(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}