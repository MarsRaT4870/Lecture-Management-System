package com.ruoyi.biz.domain.dto;

import lombok.Data;

@Data
public class ActivityStatsDTO {
    /**
     * 学科/学院名称
     */
    private String subjectName;

    /**
     * 活动发布数量
     */
    private Integer publishCount;

    /**
     * 参与总人数
     */
    private Integer joinCount;

    /**
     * 平均评分
     */
    private Double avgScore;
}