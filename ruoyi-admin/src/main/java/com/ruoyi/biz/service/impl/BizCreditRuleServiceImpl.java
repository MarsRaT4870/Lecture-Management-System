package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.entity.BizCreditRule;
import com.ruoyi.biz.mapper.BizCreditRuleMapper;
import com.ruoyi.biz.service.IBizCreditRuleService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 学分规则Service业务层处理
 */
@Service
public class BizCreditRuleServiceImpl extends ServiceImpl<BizCreditRuleMapper, BizCreditRule> implements IBizCreditRuleService {

    @Override
    public BigDecimal getCreditValueByType(String activityType) {
        BizCreditRule rule = this.getOne(new LambdaQueryWrapper<BizCreditRule>()
                .eq(BizCreditRule::getActivityType, activityType)
                .eq(BizCreditRule::getStatus, "1") // 启用状态
                .last("LIMIT 1"));

        if (rule != null) {
            return rule.getCreditValue();
        }

        // 默认值：学术讲座0.5分，校园活动0.3分，竞赛2分
        switch (activityType) {
            case "1":
                return new BigDecimal("0.5");
            case "2":
                return new BigDecimal("0.3");
            case "3":
                return new BigDecimal("2.0");
            default:
                return BigDecimal.ZERO;
        }
    }
}

