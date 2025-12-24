package com.ruoyi.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain.entity.BizActivity;
import com.ruoyi.biz.domain.dto.ActivityStatsDTO; // 需自行定义DTO

import java.util.List;

public interface IBizActivityService extends IService<BizActivity> {
    /**
     * 发布活动申请
     */
    boolean submitActivity(BizActivity activity);

    /**
     * 审核活动
     */
    boolean auditActivity(Long activityId, String status, String comment);

    /**
     * 生成动态签到码
     */
    String generateCheckinCode(Long activityId);

    /**
     * 刷新过期状态
     */
    void refreshActivityStatus();

    /**
     * 获取学科活跃度排名
     */
    List<ActivityStatsDTO> getSubjectRank();
}