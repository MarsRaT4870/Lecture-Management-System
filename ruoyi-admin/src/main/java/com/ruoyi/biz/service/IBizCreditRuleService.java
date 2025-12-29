package com.ruoyi.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain.entity.BizCreditRule;

/**
 * 学分规则Service接口
 */
public interface IBizCreditRuleService extends IService<BizCreditRule> {

    /**
     * 根据活动类型获取学分值
     *
     * @param activityType 活动类型
     * @return 学分值
     */
    java.math.BigDecimal getCreditValueByType(String activityType);
}

