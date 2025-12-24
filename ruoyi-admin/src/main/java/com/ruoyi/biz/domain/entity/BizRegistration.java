package com.ruoyi.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 活动报名对象 biz_registration
 */
@Data
@TableName("biz_registration")
public class BizRegistration {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "reg_id", type = IdType.AUTO)
    private Long regId;

    /** 活动ID */
    @TableField("activity_id")
    private Long activityId;

    /** 用户ID */
    @TableField("user_id")
    private Long userId;

    /** 学号/工号 */
    @TableField("student_no")
    private String studentNo;

    /** 姓名 */
    @TableField("user_name")
    private String userName;

    /** 学院/部门 */
    @TableField("dept_name")
    private String deptName;

    /** 状态（0已报名 1候补中 2已签到 3已取消 4候补失败 5缺席） */
    @TableField("status")
    private String status;

    /** 来源（0 PC端 1 移动端） */
    @TableField("source")
    private String source;

    /** 报名时间 */
    @TableField("create_time")
    private Date createTime;

    /** 签到时间 */
    @TableField("checkin_time")
    private Date checkinTime;

    /** 签到方式（1二维码 2人脸 3代签） */
    @TableField("checkin_type")
    private String checkinType;

    /** 是否迟到（0否 1是） */
    @TableField("is_late")
    private String isLate;

    /** 取消时间 */
    @TableField("cancel_time")
    private Date cancelTime;

    /** 内容评分 */
    @TableField("score_content")
    private Integer scoreContent;

    /** 主讲人评分 */
    @TableField("score_speaker")
    private Integer scoreSpeaker;

    /** 组织评分 */
    @TableField("score_org")
    private Integer scoreOrg;

    /** 反馈建议 */
    @TableField("feedback")
    private String feedback;

    // ================= 非数据库字段 (DTO) =================

    /** 备注 / 签到Token / 附加信息 */
    @TableField(exist = false)
    private String remark;
}