package com.nailsaas.enums;

public enum SystemRoleCodeEnum {

    MEMBER("MEMBER", "一般用戶"),
    MANICURIST("MANICURIST", "美甲師"),
    SHOP_MANAGER("SHOP_MANAGER", "店長／老闆"),
    ADMIN("ADMIN", "系統管理員");

    private final String code;
    private final String description;

    SystemRoleCodeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
