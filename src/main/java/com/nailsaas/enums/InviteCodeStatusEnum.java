package com.nailsaas.enums;

public enum InviteCodeStatusEnum {

    ACTIVE("ACTIVE"),
    USED("USED"),
    EXPIRED("EXPIRED");

    private final String code;

    InviteCodeStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
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