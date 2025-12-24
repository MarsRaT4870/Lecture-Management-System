package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.entity.BizActivity;
import com.ruoyi.biz.mapper.BizActivityMapper;
import com.ruoyi.biz.service.IBizActivityService;
import com.ruoyi.biz.domain.dto.ActivityStatsDTO;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class BizActivityServiceImpl extends ServiceImpl<BizActivityMapper, BizActivity> implements IBizActivityService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitActivity(BizActivity activity) {
        // 1. 完整性校验
        if (activity.getStartTime() == null || activity.getLocation() == null || activity.getMaxPeople() == null) {
            throw new ServiceException("活动时间、地点、最大人数为必填项");
        }

        // 2. 初始状态设置
        activity.setStatus("0"); // 草稿/待发布
        // 根据申报主体决定审核流程
        if ("2".equals(activity.getApplicantType())) {
            activity.setAuditStatus("1"); // 校外主体需校外审核
        } else {
            activity.setAuditStatus("0"); // 校内/个人需校内审核
        }

        return this.save(activity);
    }

    @Override
    public boolean auditActivity(Long activityId, String status, String comment) {
        // 使用Lambda更新，避免硬编码SQL
        return this.update(new LambdaUpdateWrapper<BizActivity>()
                .eq(BizActivity::getActivityId, activityId)
                .set(BizActivity::getAuditStatus, status)
                // 审核通过即发布
                .set("2".equals(status), BizActivity::getStatus, "1"));
    }

    @Override
    public String generateCheckinCode(Long activityId) {
        // 生成15分钟有效Token
        String token = IdUtils.fastSimpleUUID();
        // 实际项目应存入Redis: redisTemplate.opsForValue().set("sign:"+activityId, token, 15, TimeUnit.MINUTES);
        // 此处简化存入DB演示
        this.update(new LambdaUpdateWrapper<BizActivity>()
                .eq(BizActivity::getActivityId, activityId)
                .set(BizActivity::getQrCodeToken, token));
        return token;
    }

    @Override
    public void refreshActivityStatus() {
        Date now = new Date();
        // 自动结束活动
        this.update(new LambdaUpdateWrapper<BizActivity>()
                .eq(BizActivity::getStatus, "1")
                .lt(BizActivity::getEndTime, now)
                .set(BizActivity::getStatus, "2"));
    }

    @Override
    public List<ActivityStatsDTO> getSubjectRank() {
        // 复杂统计推荐使用XML Mapper或Stream聚合，此处略
        return null;
    }
}