package com.nailsaas.enums;

public enum InviteCodeStatusEnum {

    ACTIVE("ACTIVE", "可使用"),
    USED("USED", "已使用"),
    EXPIRED("EXPIRED", "已過期");

    private final String code;
    private final String description;

    InviteCodeStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    
    public static InviteCodeStatusEnum fromCode(String code) {
        for (InviteCodeStatusEnum s : values()) {
            if (s.code.equals(code)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + code);
    }
}