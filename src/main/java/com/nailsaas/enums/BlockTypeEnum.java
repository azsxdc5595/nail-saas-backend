package com.nailsaas.enums;

public enum BlockTypeEnum {

    REST("REST", "休息"),
    TRAINING("TRAINING", "教育訓練"),
    PERSONAL("PERSONAL", "私人行程"),
    OTHER("OTHER", "其他");

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