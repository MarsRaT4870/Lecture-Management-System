package com.ruoyi.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain.entity.BizRegistration;
import org.springframework.transaction.annotation.Transactional;

/**
 * 活动报名签到Service接口
 */
public interface IBizRegistrationService extends IService<BizRegistration> {

    /**
     * 学生报名活动
     *
     * @param activityId 活动ID
     * @return 结果消息
     */
    @Transactional(rollbackFor = Exception.class)
    String register(Long activityId);

    /**
     * 扫码签到
     *
     * @param activityId 活动ID
     * @param token      动态签到码
     * @param userId     用户ID
     * @return 是否签到成功
     */
    boolean checkIn(Long activityId, String token, Long userId);
}