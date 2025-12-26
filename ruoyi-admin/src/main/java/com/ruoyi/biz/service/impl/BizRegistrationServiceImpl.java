package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
// 【关键】根据报错修正引用路径
import com.ruoyi.biz.domain.entity.BizActivity;
import com.ruoyi.biz.domain.entity.BizRegistration;
import com.ruoyi.biz.mapper.BizRegistrationMapper;
import com.ruoyi.biz.service.IBizActivityService;
import com.ruoyi.biz.service.IBizMessageService;
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
    @Autowired
    private IBizMessageService messageService; // 注入消息服务

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String register(Long activityId) {
        // 1. 校验活动状态
        BizActivity activity = activityService.getById(activityId);
        if (activity == null || !"1".equals(activity.getStatus())) {
            throw new ServiceException("活动不存在或未发布");
        }

        // 【修复Bug核心代码】严格校验截止时间
        // 如果当前时间 晚于 报名截止时间，直接报错拦截
        if (activity.getRegDeadline() != null && new Date().after(activity.getRegDeadline())) {
            throw new ServiceException("很抱歉，报名时间已截止");
        }
        Long userId = SecurityUtils.getUserId();
        String username = SecurityUtils.getUsername();

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
        reg.setUserName(username);
        // 【关键修复】删除了 reg.setActivityName(...) 以解决报错，因为实体类没这个字段
        // 如果 DeptName 字段也不存在，请将下一行也注释掉
        // reg.setDeptName(SecurityUtils.getDeptName());
        reg.setCreateTime(new Date());

        // 4. 判断是否进入候补
        String resultMsg;
        String msgTitle;
        String msgContent;

        if (activity.getMaxPeople() > 0 && currentCount >= activity.getMaxPeople()) {
            reg.setStatus("1"); // 候补状态
            resultMsg = "预约已满，已加入候补队列";
            msgTitle = "候补队列通知";
            msgContent = "您报名的活动《" + activity.getTitle() + "》名额已满，目前您已进入候补队列，请留意后续通知。";
        } else {
            reg.setStatus("0"); // 正常报名
            resultMsg = "报名成功";
            msgTitle = "报名成功通知";
            msgContent = "您已成功报名活动《" + activity.getTitle() + "》，请准时参加。";
        }

        // 保存报名记录
        this.save(reg);

        // 5. 【新增】发送站内信通知
        // type: "2" 代表活动提醒
        try {
            messageService.sendMessage(
                    userId,
                    username,
                    msgTitle,
                    msgContent,
                    "2"
            );
        } catch (Exception e) {
            // 捕获异常防止消息服务故障影响核心报名流程
            log.error("发送报名通知消息失败", e);
        }

        return resultMsg;
    }

    @Override
    public boolean checkIn(Long activityId, String token, Long userId) {
        BizActivity activity = activityService.getById(activityId);
        if (activity == null) {
            throw new ServiceException("活动不存在");
        }

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
            return true; // 已经签到过
        }

        // 4. 更新签到状态
        reg.setStatus("2"); // 已签到
        reg.setCheckinTime(new Date());
        reg.setCheckinType("1"); // 二维码签到

        // 5. 判断迟到 (活动开始30分钟后算迟到)
        if (activity.getStartTime() != null) {
            long diff = System.currentTimeMillis() - activity.getStartTime().getTime();
            reg.setIsLate(diff > 30 * 60 * 1000 ? "1" : "0");
        }

        return this.updateById(reg);
    }
}