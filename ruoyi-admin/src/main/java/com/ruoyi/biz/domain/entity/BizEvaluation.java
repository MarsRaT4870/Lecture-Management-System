package com.ruoyi.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动评价对象 biz_evaluation
 */
@Data
@TableName("biz_evaluation")
public class BizEvaluation implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long evalId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 活动名称 (非数据库字段，用于展示)
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String activityName;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 评分 1-5
     */
    private Integer score;

    /**
     * 评价内容
     */
    private String comment;

    /**
     * 创建时间
     */
    private Date createTime;
}