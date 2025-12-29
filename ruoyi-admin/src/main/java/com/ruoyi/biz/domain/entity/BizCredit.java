package com.ruoyi.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 学分认证对象 biz_credit
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_credit")
public class BizCredit extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 学分记录ID */
    @TableId(value = "credit_id", type = IdType.AUTO)
    private Long creditId;

    /** 用户ID */
    private Long userId;

    /** 活动ID */
    private Long activityId;

    /** 学分类型（1学术讲座 2校园活动 3竞赛） */
    private String creditType;

    /** 学分值 */
    private BigDecimal creditValue;

    /** 证书编号 */
    private String certificateNo;

    /** 电子证书URL */
    private String certificateUrl;

    /** 状态（0待审核 1已发放 2已撤销） */
    private String status;

    /** 发放时间 */
    private Date grantTime;
}
