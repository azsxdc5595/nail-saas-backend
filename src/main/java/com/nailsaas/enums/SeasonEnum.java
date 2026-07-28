package com.nailsaas.enums;

public enum SeasonEnum {

    SPRING("SPRING", "春季"),
    SUMMER("SUMMER", "夏季"),
    AUTUMN("AUTUMN", "秋季"),
    WINTER("WINTER", "冬季"),
    ALL_SEASON("ALL_SEASON", "四季皆適合");

    private final String code;
    private final String description;

    SeasonEnum(String code, String description) {
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