package com.ruoyi.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_activity")
public class BizActivity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "activity_id", type = IdType.AUTO)
    private Long activityId;

    @TableField("title")
    private String title;

    @TableField("activity_type")
    private String activityType;

    @TableField("applicant_type")
    private String applicantType;

    @TableField("speaker")
    private String speaker;

    @TableField("speaker_title")
    private String speakerTitle;

    @TableField("speaker_affil")
    private String speakerAffil;

    @TableField("location")
    private String location;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("start_time")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("end_time")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("reg_deadline")
    private Date regDeadline;

    @TableField("target_audience")
    private String targetAudience;

    @TableField("description")
    private String description;

    @TableField("max_people")
    private Integer maxPeople;

    @TableField("status")
    private String status;

    @TableField("audit_status")
    private String auditStatus;

    @TableField("qr_code_token")
    private String qrCodeToken;

    @TableLogic
    @TableField("del_flag")
    private String delFlag;
}