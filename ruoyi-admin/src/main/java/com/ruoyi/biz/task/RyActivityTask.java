package com.ruoyi.biz.task; // 【注意】包名改到了 biz 下

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
// 如果下面的 import 报红，请按 Alt+Enter 重新导入你项目里正确的包路径
import com.ruoyi.biz.domain.entity.BizActivity;
import com.ruoyi.biz.domain.entity.BizRegistration;
import com.ruoyi.biz.service.IBizActivityService;
import com.ruoyi.biz.service.IBizCreditService;
import com.ruoyi.biz.service.IBizMessageService;
import com.ruoyi.biz.service.IBizRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 活动相关定时任务
 * 1. 自动结束活动
 * 2. 活动开始前提醒
 */
@Component("ryActivityTask") // Bean 名称保持不变
public class RyActivityTask {

    @Autowired
    private IBizActivityService activityService;

    @Autowired
    private IBizRegistrationService registrationService;

    @Autowired
    private IBizMessageService messageService;

    @Autowired
    private IBizCreditService creditService;

    /**
     * 任务1：自动刷新活动状态
     * 将所有“已发布(1)”且“结束时间 < 当前时间”的活动，更新为“已结束(2)”
     */
    public void autoFinishActivity() {
        System.out.println("-----------执行定时任务：刷新活动状态-----------");
        Date now = new Date();

        // 使用 UpdateWrapper 批量更新
        boolean result = activityService.update(new LambdaUpdateWrapper<BizActivity>()
                .eq(BizActivity::getStatus, "1") // 状态：已发布
                .lt(BizActivity::getEndTime, now) // 结束时间 < 现在
                .set(BizActivity::getStatus, "2")); // 更新为：已结束

        if (result) {
            System.out.println(">>> 成功归档了已过期的活动");
        }
    }

    /**
     * 任务2：活动开始前提醒（2小时前和1小时前）
     */
    public void remindUser() {
        Date now = new Date();
        long currentTime = now.getTime();
        
        // 2小时后的时间范围（1.5小时到2.5小时之间）
        long twoHoursLater = currentTime + 2L * 60 * 60 * 1000;
        long twoHoursBefore = currentTime + (long)(1.5 * 60 * 60 * 1000);
        
        // 1小时后的时间范围（0.5小时到1.5小时之间）
        long oneHourLater = currentTime + 1L * 60 * 60 * 1000;
        long oneHourBefore = currentTime + (long)(0.5 * 60 * 60 * 1000);

        // 查找2小时内开始的活动
        List<BizActivity> activities2h = activityService.list(new LambdaQueryWrapper<BizActivity>()
                .eq(BizActivity::getStatus, "1")
                .ge(BizActivity::getStartTime, new Date(twoHoursBefore))
                .le(BizActivity::getStartTime, new Date(twoHoursLater))
        );

        // 查找1小时内开始的活动
        List<BizActivity> activities1h = activityService.list(new LambdaQueryWrapper<BizActivity>()
                .eq(BizActivity::getStatus, "1")
                .ge(BizActivity::getStartTime, new Date(oneHourBefore))
                .le(BizActivity::getStartTime, new Date(oneHourLater))
        );

        if (activities2h.isEmpty() && activities1h.isEmpty()) return;

        System.out.println("-----------执行定时任务：活动提醒-----------");

        // 发送2小时提醒
        for (BizActivity act : activities2h) {
            sendReminder(act, "2小时内");
        }

        // 发送1小时提醒
        for (BizActivity act : activities1h) {
            sendReminder(act, "1小时内");
        }
    }

    /**
     * 发送提醒消息
     */
    private void sendReminder(BizActivity act, String timeDesc) {
        List<BizRegistration> users = registrationService.list(new LambdaQueryWrapper<BizRegistration>()
                .eq(BizRegistration::getActivityId, act.getActivityId())
                .in(BizRegistration::getStatus, "0", "1")); // 已报名和候补中

        for (BizRegistration user : users) {
            try {
                messageService.sendMessage(
                        user.getUserId(),
                        user.getUserName(),
                        "⏰ 活动即将开始提醒",
                        "您报名的活动《" + act.getTitle() + "》将于 " + timeDesc + " 开始，请前往 " + act.getLocation() + " 签到。",
                        "2" // 活动通知
                );
            } catch (Exception e) {
                System.err.println("发送提醒失败: " + e.getMessage());
            }
        }
    }

    /**
     * 任务4：活动结束后自动发放学分
     */
    public void autoGrantCredits() {
        System.out.println("-----------执行定时任务：自动发放学分-----------");
        Date now = new Date();
        
        // 查找已结束但未发放学分的活动（结束时间在1小时内）
        long oneHourAgo = now.getTime() - 60 * 60 * 1000;
        List<BizActivity> finishedActivities = activityService.list(new LambdaQueryWrapper<BizActivity>()
                .eq(BizActivity::getStatus, "2") // 已结束
                .ge(BizActivity::getEndTime, new Date(oneHourAgo))
                .le(BizActivity::getEndTime, now)
        );

        int totalGranted = 0;
        for (BizActivity activity : finishedActivities) {
            try {
                int count = creditService.autoGrantCredits(activity.getActivityId());
                totalGranted += count;
                System.out.println(">>> 活动《" + activity.getTitle() + "》发放了 " + count + " 条学分记录");
            } catch (Exception e) {
                System.err.println("发放学分失败: " + e.getMessage());
            }
        }

        if (totalGranted > 0) {
            System.out.println(">>> 本次共发放 " + totalGranted + " 条学分记录");
        }
    }



    /**
     * 任务3：自动下架已结束超过 3 天的活动
     * 逻辑：如果活动已结束(status=2) 且当前还显示(visible=1) 且 结束时间早于3天前 -> 设置为不显示(visible=0)
     */
    public void autoArchiveActivity() {
        System.out.println("-----------执行定时任务：清理过期活动-----------");

        // 【核心修改】当前时间 - 3天
        long threeDaysAgo = System.currentTimeMillis() - (3L * 24 * 60 * 60 * 1000);
        Date limitDate = new Date(threeDaysAgo);

        // 更新条件：
        // 1. 状态 = 已结束 (2)
        // 2. 可见性 = 显示 (1) —— 保证手动下架的不会被重复处理
        // 3. 结束时间 < 3天前
        boolean result = activityService.update(new LambdaUpdateWrapper<BizActivity>()
                .eq(BizActivity::getStatus, "2")
                .eq(BizActivity::getVisible, "1")
                .lt(BizActivity::getEndTime, limitDate)
                .set(BizActivity::getVisible, "0")); // 动作：自动归档

        if(result) {
            System.out.println(">>> 成功将结束超过3天的活动自动归档");
        }
    }

}