package com.ruoyi.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 学分规则对象 biz_credit_rule
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_credit_rule")
public class BizCreditRule extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    @TableId(value = "rule_id", type = IdType.AUTO)
    private Long ruleId;

    /** 活动类型（1学术讲座 2校园活动 3竞赛） */
    private String activityType;

    /** 学分值 */
    private BigDecimal creditValue;

    /** 规则名称 */
    private String ruleName;

    /** 规则描述 */
    private String description;

    /** 状态（1启用 0停用） */
    private String status;
}

