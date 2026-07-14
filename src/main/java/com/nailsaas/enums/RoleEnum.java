package com.nailsaas.enums;

public enum RoleEnum {

    OWNER("OWNER", "業主"),
    ADMIN("ADMIN", "店長"),
    STAFF("STAFF", "一般職員");

    private final String code;
    private final String description;

    RoleEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
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