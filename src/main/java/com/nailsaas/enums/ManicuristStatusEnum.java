package com.nailsaas.enums;

public enum ManicuristStatusEnum {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    SUSPENDED("SUSPENDED");

    private final String code;

    ManicuristStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ManicuristStatusEnum fromCode(String code) {
        for (ManicuristStatusEnum s : values()) {
            if (s.code.equals(code)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + code);
    }
}