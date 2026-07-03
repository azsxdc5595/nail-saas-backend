package com.nailsaas.enums;

public enum DayOfWeekEnum {

    MONDAY("MONDAY", "星期一"),
    TUESDAY("TUESDAY", "星期二"),
    WEDNESDAY("WEDNESDAY", "星期三"),
    THURSDAY("THURSDAY", "星期四"),
    FRIDAY("FRIDAY", "星期五"),
    SATURDAY("SATURDAY", "星期六"),
    SUNDAY("SUNDAY", "星期日");

    private final String code;
    private final String description;

    DayOfWeekEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DayOfWeekEnum fromCode(String code) {
        for (DayOfWeekEnum day : values()) {
            if (day.code.equals(code)) {
                return day;
            }
        }
        throw new IllegalArgumentException("Unknown dayOfWeek: " + code);
    }
}