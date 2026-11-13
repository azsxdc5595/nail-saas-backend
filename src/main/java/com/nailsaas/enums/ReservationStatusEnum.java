package com.nailsaas.enums;

public enum ReservationStatusEnum {

    PENDING("PENDING", "預約待確認"),
    EXPIRED("EXPIRED", "預約已逾時"),
    CONFIRMED("CONFIRMED", "預約已確認"),
    REJECTED("REJECTED", "預約已拒絕"),
    FINISH("FINISH", "服務已完成"),
    CANCELLED("CANCELLED", "預約已取消"),
    NO_SHOW("NO_SHOW", "預約未履行（放鳥）");

    private final String code;
    private final String description;

    ReservationStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ReservationStatusEnum fromCode(String code) {
        for (ReservationStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown reservation status: " + code);
    }
}
