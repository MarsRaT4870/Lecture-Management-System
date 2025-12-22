package com.ruoyi.biz.enums;

/**
 * 活动状态枚举
 */
public enum ActivityStatus {
    NORMAL("0", "正常"),
    CLOSED("1", "关闭/结束");

    private final String code;
    private final String info;

    ActivityStatus(String code, String info) {
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