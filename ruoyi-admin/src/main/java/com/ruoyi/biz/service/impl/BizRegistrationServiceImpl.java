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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class BizRegistrationServiceImpl extends ServiceImpl<BizRegistrationMapper, BizRegistration>
        implements IBizRegistrationService {

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
                    "2");
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

    @Override
    public boolean submitFeedback(Long regId, Integer scoreContent, Integer scoreSpeaker, Integer scoreOrg,
            String feedback) {
        // 1. 查询报名记录
        BizRegistration reg = this.getById(regId);
        if (reg == null) {
            throw new ServiceException("报名记录不存在");
        }

        // 2. 校验权限：只能评价自己的记录
        Long userId = SecurityUtils.getUserId();
        if (!reg.getUserId().equals(userId)) {
            throw new ServiceException("无权评价他人的报名记录");
        }

        // 3. 校验状态：只有已签到的才能评价
        if (!"2".equals(reg.getStatus())) {
            throw new ServiceException("只有已签到的活动才能评价");
        }

        // 4. 敏感词过滤（简单实现，实际应该用专业的敏感词库）
        if (feedback != null && !feedback.isEmpty()) {
            String[] sensitiveWords = { "垃圾", "差劲", "烂" }; // 示例敏感词
            for (String word : sensitiveWords) {
                if (feedback.contains(word)) {
                    // 记录日志，但不阻止提交（可根据需求调整）
                    log.warn("检测到敏感词: {}", word);
                }
            }
        }

        // 5. 校验评分范围
        if (scoreContent != null && (scoreContent < 1 || scoreContent > 5)) {
            throw new ServiceException("内容质量评分必须在1-5之间");
        }
        if (scoreSpeaker != null && (scoreSpeaker < 1 || scoreSpeaker > 5)) {
            throw new ServiceException("主讲人表现评分必须在1-5之间");
        }
        if (scoreOrg != null && (scoreOrg < 1 || scoreOrg > 5)) {
            throw new ServiceException("组织安排评分必须在1-5之间");
        }

        // 6. 更新评价信息
        reg.setScoreContent(scoreContent);
        reg.setScoreSpeaker(scoreSpeaker);
        reg.setScoreOrg(scoreOrg);
        reg.setFeedback(feedback);

        return this.updateById(reg);
    }

    @Override
    public boolean cancelRegistration(Long regId) {
        // 1. 查询报名记录
        BizRegistration reg = this.getById(regId);
        if (reg == null) {
            throw new ServiceException("报名记录不存在");
        }

        // 2. 校验权限：只能取消自己的报名
        Long userId = SecurityUtils.getUserId();
        if (!reg.getUserId().equals(userId)) {
            throw new ServiceException("无权取消他人的报名记录");
        }

        // 3. 校验状态：只有已报名或候补中的才能取消
        if (!"0".equals(reg.getStatus()) && !"1".equals(reg.getStatus())) {
            throw new ServiceException("当前状态不允许取消");
        }

        // 4. 更新状态
        reg.setStatus("3"); // 已取消
        reg.setCancelTime(new Date());

        boolean result = this.updateById(reg);

        // 5. 如果取消的是已报名状态，需要检查是否有候补人员可以转正
        if (result && "0".equals(reg.getStatus())) {
            // 查找该活动的第一个候补人员
            BizRegistration waitlist = this.getOne(new LambdaQueryWrapper<BizRegistration>()
                    .eq(BizRegistration::getActivityId, reg.getActivityId())
                    .eq(BizRegistration::getStatus, "1")
                    .orderByAsc(BizRegistration::getCreateTime)
                    .last("LIMIT 1"));

            if (waitlist != null) {
                // 将候补转为正式报名
                waitlist.setStatus("0");
                this.updateById(waitlist);

                // 发送通知给候补转正的用户
                try {
                    BizActivity activity = activityService.getById(reg.getActivityId());
                    if (activity != null) {
                        messageService.sendMessage(
                                waitlist.getUserId(),
                                waitlist.getUserName(),
                                "候补转正通知",
                                "恭喜！您报名的活动《" + activity.getTitle() + "》候补转正成功，请准时参加。",
                                "1");
                    }
                } catch (Exception e) {
                    log.error("发送候补转正通知失败", e);
                }
            }
        }

        return result;
    }

    @Override
    public boolean checkInByFace(Long activityId, Long userId, String faceImage) {
        // TODO: 实际实现需要接入人脸识别服务（如百度AI、腾讯云等）
        // 这里先做基础校验，实际项目中需要：
        // 1. 调用人脸识别API进行人脸比对
        // 2. 验证人脸是否匹配用户
        // 3. 验证活动是否允许人脸识别签到

        BizActivity activity = activityService.getById(activityId);
        if (activity == null) {
            throw new ServiceException("活动不存在");
        }

        // 查询有效的报名记录
        BizRegistration reg = this.getOne(new LambdaQueryWrapper<BizRegistration>()
                .eq(BizRegistration::getActivityId, activityId)
                .eq(BizRegistration::getUserId, userId)
                .ne(BizRegistration::getStatus, "3")); // 排除已取消

        if (reg == null) {
            throw new ServiceException("未查询到报名记录");
        }

        // 防止重复签到
        if ("2".equals(reg.getStatus())) {
            return true; // 已经签到过
        }

        // TODO: 这里应该调用人脸识别服务进行验证
        // 示例：boolean faceMatch = faceRecognitionService.verify(userId, faceImage);
        // if (!faceMatch) {
        // throw new ServiceException("人脸识别失败，请重试");
        // }

        // 更新签到状态
        reg.setStatus("2"); // 已签到
        reg.setCheckinTime(new Date());
        reg.setCheckinType("2"); // 人脸识别签到

        // 判断迟到
        if (activity.getStartTime() != null) {
            long diff = System.currentTimeMillis() - activity.getStartTime().getTime();
            reg.setIsLate(diff > 30 * 60 * 1000 ? "1" : "0");
        }

        return this.updateById(reg);
    }
}