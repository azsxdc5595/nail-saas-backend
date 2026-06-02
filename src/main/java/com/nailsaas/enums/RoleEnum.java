package com.nailsaas.enums;

public enum RoleEnum {

    OWNER("OWNER"),
    ADMIN("ADMIN"),
    STAFF("STAFF");

    private final String code;

    RoleEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static RoleEnum fromCode(String code) {
        for (RoleEnum s : values()) {
            if (s.code.equals(code)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + code);
    }
}