package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.entity.BizActivity;
import com.ruoyi.biz.domain.entity.BizRegistration;
import com.ruoyi.biz.mapper.BizRegistrationMapper;
import com.ruoyi.biz.service.IBizActivityService;
import com.ruoyi.biz.service.IBizRegistrationService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class BizRegistrationServiceImpl extends ServiceImpl<BizRegistrationMapper, BizRegistration> implements IBizRegistrationService {

    @Autowired
    private IBizActivityService activityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String register(Long activityId) {
        // 1. 校验活动状态
        BizActivity activity = activityService.getById(activityId);
        if (activity == null || !"1".equals(activity.getStatus())) {
            throw new ServiceException("活动不存在或未发布");
        }
        if (new Date().after(activity.getRegDeadline())) {
            throw new ServiceException("报名已截止");
        }

        Long userId = SecurityUtils.getUserId();

        // 2. 校验是否重复报名 (排除已取消状态 '3')
        long count = this.count(new LambdaQueryWrapper<BizRegistration>()
                .eq(BizRegistration::getActivityId, activityId)
                .eq(BizRegistration::getUserId, userId)
                .ne(BizRegistration::getStatus, "3"));
        if (count > 0) {
            throw new ServiceException("您已报名该活动");
        }

        // 3. 校验人数限制
        // 统计状态为 0(已报名) 和 2(已签到) 的人数
        long currentCount = this.count(new LambdaQueryWrapper<BizRegistration>()
                .eq(BizRegistration::getActivityId, activityId)
                .in(BizRegistration::getStatus, "0", "2"));

        BizRegistration reg = new BizRegistration();
        reg.setActivityId(activityId);
        reg.setUserId(userId);
        reg.setUserName(SecurityUtils.getUsername());
        reg.setCreateTime(new Date());

        // 4. 判断是否进入候补
        if (activity.getMaxPeople() > 0 && currentCount >= activity.getMaxPeople()) {
            reg.setStatus("1"); // 候补状态
            this.save(reg);
            return "预约已满，已加入候补队列";
        } else {
            reg.setStatus("0"); // 正常报名
            this.save(reg);
            return "报名成功";
        }
    }

    @Override
    public boolean checkIn(Long activityId, String token, Long userId) {
        BizActivity activity = activityService.getById(activityId);

        // 1. 校验二维码Token
        if (activity.getQrCodeToken() == null || !activity.getQrCodeToken().equals(token)) {
            throw new ServiceException("二维码已失效");
        }

        // 2. 查询有效的报名记录
        BizRegistration reg = this.getOne(new LambdaQueryWrapper<BizRegistration>()
                .eq(BizRegistration::getActivityId, activityId)
                .eq(BizRegistration::getUserId, userId)
                .ne(BizRegistration::getStatus, "3")); // 排除已取消

        if (reg == null) {
            throw new ServiceException("未查询到报名记录");
        }

        // 3. 防止重复签到
        if ("2".equals(reg.getStatus())) {
            return true; // 已经签到过，直接返回成功，或者抛出提示
        }

        // 4. 更新签到状态
        reg.setStatus("2"); // 已签到
        reg.setCheckinTime(new Date());
        reg.setCheckinType("1"); // 二维码签到

        // 5. 判断迟到 (活动开始30分钟后算迟到)
        long diff = System.currentTimeMillis() - activity.getStartTime().getTime();
        reg.setIsLate(diff > 30 * 60 * 1000 ? "1" : "0");

        return this.updateById(reg);
    }
}