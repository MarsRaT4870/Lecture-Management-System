package com.ruoyi.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain.entity.BizCredit;

import java.util.List;
import java.util.Map;

/**
 * 学分认证Service接口
 */
public interface IBizCreditService extends IService<BizCredit> {

    /**
     * 自动发放学分（活动结束后自动调用）
     *
     * @param activityId 活动ID
     * @return 发放的学分记录数
     */
    int autoGrantCredits(Long activityId);

    /**
     * 获取用户总学分
     *
     * @param userId 用户ID
     * @return 总学分
     */
    double getUserTotalCredits(Long userId);

    /**
     * 获取学分排行榜
     *
     * @param limit 前N名
     * @return 排行榜列表
     */
    List<Map<String, Object>> getCreditRanking(int limit);
}
