package com.ruoyi.biz.task; // 【注意】包名改到了 biz 下

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
// 如果下面的 import 报红，请按 Alt+Enter 重新导入你项目里正确的包路径
import com.ruoyi.biz.domain.entity.BizActivity;
import com.ruoyi.biz.domain.entity.BizRegistration;
import com.ruoyi.biz.service.IBizActivityService;
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
     * 任务2：活动开始前提醒
     */
    public void remindUser() {
        // 获取当前时间 + 1小时
        long currentTime = System.currentTimeMillis();
        long oneHourLater = currentTime + 60 * 60 * 1000;

        // 1. 查找即将开始的活动
        List<BizActivity> activities = activityService.list(new LambdaQueryWrapper<BizActivity>()
                .eq(BizActivity::getStatus, "1")
                .ge(BizActivity::getStartTime, new Date(currentTime))
                .le(BizActivity::getStartTime, new Date(oneHourLater))
        );

        if (activities.isEmpty()) return;

        System.out.println("-----------执行定时任务：活动提醒-----------");

        for (BizActivity act : activities) {
            // 2. 查找该活动的报名用户 (且状态为0=已报名)
            List<BizRegistration> users = registrationService.list(new LambdaQueryWrapper<BizRegistration>()
                    .eq(BizRegistration::getActivityId, act.getActivityId())
                    .eq(BizRegistration::getStatus, "0"));

            for (BizRegistration user : users) {
                // 3. 发送消息
                try {
                    messageService.sendMessage(
                            user.getUserId(),
                            user.getUserName(),
                            "⏰ 活动即将开始提醒",
                            "您报名的活动《" + act.getTitle() + "》将于 1 小时内开始，请前往 " + act.getLocation() + " 签到。",
                            "2" // 活动通知
                    );
                } catch (Exception e) {
                    System.err.println("发送提醒失败: " + e.getMessage());
                }
            }
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