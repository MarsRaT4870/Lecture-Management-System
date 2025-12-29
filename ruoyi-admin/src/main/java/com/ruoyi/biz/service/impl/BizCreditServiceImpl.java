package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.entity.BizActivity;
import com.ruoyi.biz.domain.entity.BizCredit;
import com.ruoyi.biz.domain.entity.BizRegistration;
import com.ruoyi.biz.mapper.BizCreditMapper;
import com.ruoyi.biz.service.IBizActivityService;
import com.ruoyi.biz.service.IBizCreditRuleService;
import com.ruoyi.biz.service.IBizCreditService;
import com.ruoyi.biz.service.IBizRegistrationService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学分认证Service业务层处理
 */
@Slf4j
@Service
public class BizCreditServiceImpl extends ServiceImpl<BizCreditMapper, BizCredit> implements IBizCreditService {

    @Autowired
    private IBizActivityService activityService;

    @Autowired
    private IBizRegistrationService registrationService;

    @Autowired
    private IBizCreditRuleService creditRuleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int autoGrantCredits(Long activityId) {
        // 1. 查询活动信息
        BizActivity activity = activityService.getById(activityId);
        if (activity == null) {
            log.warn("活动不存在: {}", activityId);
            return 0;
        }

        // 2. 查询已签到的报名记录
        List<BizRegistration> registrations = registrationService.list(
                new LambdaQueryWrapper<BizRegistration>()
                        .eq(BizRegistration::getActivityId, activityId)
                        .eq(BizRegistration::getStatus, "2") // 已签到
        );

        if (registrations.isEmpty()) {
            return 0;
        }

        // 3. 获取该活动类型的学分值
        BigDecimal creditValue = creditRuleService.getCreditValueByType(activity.getActivityType());
        if (creditValue == null || creditValue.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("活动类型 {} 未配置学分规则", activity.getActivityType());
            return 0;
        }

        // 4. 为每个已签到的用户发放学分
        int count = 0;
        for (BizRegistration reg : registrations) {
            // 检查是否已经发放过学分
            long existCount = this.count(new LambdaQueryWrapper<BizCredit>()
                    .eq(BizCredit::getActivityId, activityId)
                    .eq(BizCredit::getUserId, reg.getUserId()));

            if (existCount > 0) {
                continue; // 已发放过，跳过
            }

            // 创建学分记录
            BizCredit credit = new BizCredit();
            credit.setUserId(reg.getUserId());
            credit.setActivityId(activityId);
            credit.setCreditType(activity.getActivityType());
            credit.setCreditValue(creditValue);
            credit.setCertificateNo("CR" + IdUtils.fastSimpleUUID().substring(0, 12).toUpperCase());
            credit.setStatus("1"); // 已发放
            credit.setGrantTime(DateUtils.getNowDate());
            credit.setCreateTime(DateUtils.getNowDate());

            this.save(credit);
            count++;
        }

        log.info("活动 {} 自动发放学分完成，共发放 {} 条记录", activityId, count);
        return count;
    }

    @Override
    public double getUserTotalCredits(Long userId) {
        List<BizCredit> credits = this.list(new LambdaQueryWrapper<BizCredit>()
                .eq(BizCredit::getUserId, userId)
                .eq(BizCredit::getStatus, "1")); // 已发放

        return credits.stream()
                .map(BizCredit::getCreditValue)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
    }

    @Override
    public List<Map<String, Object>> getCreditRanking(int limit) {
        // 查询所有已发放的学分记录，按用户分组统计
        List<BizCredit> allCredits = this.list(new LambdaQueryWrapper<BizCredit>()
                .eq(BizCredit::getStatus, "1"));

        // 按用户ID分组，计算总学分
        Map<Long, Double> userCredits = allCredits.stream()
                .collect(Collectors.groupingBy(
                        BizCredit::getUserId,
                        Collectors.summingDouble(c -> c.getCreditValue().doubleValue())));

        // 转换为排行榜列表并排序
        return userCredits.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("userId", entry.getKey());
                    item.put("totalCredits", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }
}
