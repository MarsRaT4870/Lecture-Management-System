package com.ruoyi.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("biz_credit")
public class BizCredit implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long creditId;

    /**
     * 学生ID
     */
    private Long userId;

    /**
     * 学生姓名
     */
    private String userName;

    /**
     * 学院
     */
    private String deptName;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 学分值
     */
    private Double creditVal;

    /**
     * 发放时间
     */
    private Date grantTime;

    /**
     * 证书编号 (唯一标识)
     */
    private String certificateNo;
}