package com.nailsaas.enums;

public enum NailStyleEnum {

    KOREAN("KOREAN", "韓系"),
    JAPANESE("JAPANESE", "日系"),
    AMERICAN("AMERICAN", "美式"),
    FRENCH("FRENCH", "法式"),
    MINIMAL("MINIMAL", "極簡"),
    CUTE("CUTE", "可愛"),
    ELEGANT("ELEGANT", "優雅"),
    LUXURY("LUXURY", "輕奢"),
    VINTAGE("VINTAGE", "復古"),
    SWEET("SWEET", "甜美"),
    COOL("COOL", "個性"),
    NATURAL("NATURAL", "自然");

    private final String code;
    private final String description;

    NailStyleEnum(String code, String description) {
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