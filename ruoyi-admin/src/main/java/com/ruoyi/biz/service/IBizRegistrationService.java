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

    /**
     * 提交评价反馈
     *
     * @param regId        报名记录ID
     * @param scoreContent 内容质量评分(1-5)
     * @param scoreSpeaker 主讲人表现评分(1-5)
     * @param scoreOrg     组织安排评分(1-5)
     * @param feedback     文字建议
     * @return 是否提交成功
     */
    @Transactional(rollbackFor = Exception.class)
    boolean submitFeedback(Long regId, Integer scoreContent, Integer scoreSpeaker, Integer scoreOrg, String feedback);

    /**
     * 取消报名
     *
     * @param regId 报名记录ID
     * @return 是否取消成功
     */
    @Transactional(rollbackFor = Exception.class)
    boolean cancelRegistration(Long regId);

    /**
     * 人脸识别签到（接口预留，实际需要接入AI服务）
     *
     * @param activityId 活动ID
     * @param userId     用户ID
     * @param faceImage  人脸图片（Base64或文件）
     * @return 是否签到成功
     */
    boolean checkInByFace(Long activityId, Long userId, String faceImage);
}