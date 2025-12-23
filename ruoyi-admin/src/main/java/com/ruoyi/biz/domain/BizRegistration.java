package com.ruoyi.biz.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报名记录对象 biz_registration
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizRegistration extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 报名ID */
    private Long regId;

    /** 活动ID */
    @Excel(name = "活动ID")
    private Long activityId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String userName;

    /** 学号 */
    @Excel(name = "学号")
    private String studentNo;

    /** 院系名称 */
    @Excel(name = "院系名称")
    private String deptName;

    /** 状态（1=已报名, 2=已签到, 0=待审核） */
    @Excel(name = "状态")
    private String status;

    /** 签到状态 (0未签到 1已签到) */
    @Excel(name = "签到状态")
    private String checkinStatus;

    /** 签到时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "签到时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date checkinTime;

    /** 活动名称 (非数据库字段，用于列表展示) */
    @Excel(name = "活动名称")
    private String activityName;

}