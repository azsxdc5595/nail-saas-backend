package com.nailsaas.enums;

public enum MainColorEnum {

    WHITE("WHITE", "白色"),
    BLACK("BLACK", "黑色"),
    GRAY("GRAY", "灰色"),
    NUDE("NUDE", "裸色"),
    PINK("PINK", "粉色"),
    RED("RED", "紅色"),
    ORANGE("ORANGE", "橘色"),
    YELLOW("YELLOW", "黃色"),
    GREEN("GREEN", "綠色"),
    BLUE("BLUE", "藍色"),
    PURPLE("PURPLE", "紫色"),
    BROWN("BROWN", "棕色"),
    GOLD("GOLD", "金色"),
    SILVER("SILVER", "銀色"),
    MULTI("MULTI", "多色");

    private final String code;
    private final String description;

    MainColorEnum(String code, String description) {
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