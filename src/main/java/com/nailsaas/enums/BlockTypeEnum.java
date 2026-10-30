package com.nailsaas.enums;

public enum BlockTypeEnum {

    OFF("OFF", "休假"),
    REST("REST", "休息"),
    BOOKED("BOOKED", "已預約"),
    MAINTAIN("MAINTAIN", "維護");

    private final String code;
    private final String description;
    
    BlockTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    
    public static BlockTypeEnum fromCode(String code) {
        for (BlockTypeEnum s : values()) {
            if (s.code.equals(code)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + code);
    }
}
