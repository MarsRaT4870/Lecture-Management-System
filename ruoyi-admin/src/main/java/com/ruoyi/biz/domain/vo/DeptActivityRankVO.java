package com.ruoyi.biz.domain.vo;

import lombok.Data;

@Data
public class DeptActivityRankVO {
    private String deptName;    // 学院名称
    private Integer totalCount; // 报名总人数
    private Integer checkInCount; // 实际签到人数
    private String activeRate;  // 活跃率 (计算字段)
}