package com.ruoyi.biz.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动对象 biz_activity
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizActivity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 活动ID */
    private Long activityId;

    /** 活动标题 */
    @Excel(name = "活动标题")
    private String title;

    /** 活动描述 */
    @Excel(name = "活动描述")
    private String description;

    /** 主讲人 */
    @Excel(name = "主讲人")
    private String speaker;

    /** 主讲人简介 */
    @Excel(name = "主讲人简介")
    private String speakerInfo;

    /** 地点 */
    @Excel(name = "地点")
    private String location;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 最大人数限制（0为不限） */
    @Excel(name = "最大人数")
    private Long maxPeople;

    /** 状态 (0正常 1关闭) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=关闭")
    private String status;

    /** 审核状态 (0待审核 1通过 2拒绝) */
    @Excel(name = "审核状态", readConverterExp = "0=待审核,1=通过,2=拒绝")
    private String auditStatus;

    /** 部门ID */
    private Long deptId;

    /** 签到验证码 */
    @Excel(name = "签到码")
    private String checkinCode;
}