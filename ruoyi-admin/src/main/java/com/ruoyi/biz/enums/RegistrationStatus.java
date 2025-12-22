package com.ruoyi.biz.enums;

/**
 * 报名签到状态枚举
 */
public enum RegistrationStatus {
    REGISTERED("0", "已报名"),
    CHECKED_IN("1", "已签到"),
    CANCELLED("2", "已取消");

    private final String code;
    private final String info;

    RegistrationStatus(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}