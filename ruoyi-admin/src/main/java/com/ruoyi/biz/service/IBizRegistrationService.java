package com.ruoyi.biz.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.biz.domain.BizRegistration;

/**
 * 活动报名签到Service接口
 */
public interface IBizRegistrationService {
    /**
     * 查询活动报名签到
     * @param regId 活动报名签到主键
     * @return 活动报名签到
     */
    public BizRegistration selectBizRegistrationByRegId(Long regId);

    /**
     * 查询活动报名签到列表
     * @param bizRegistration 活动报名签到
     * @return 活动报名签到集合
     */
    public List<BizRegistration> selectBizRegistrationList(BizRegistration bizRegistration);

    /**
     * 新增活动报名签到
     * @param bizRegistration 活动报名签到
     * @return 结果
     */
    public int insertBizRegistration(BizRegistration bizRegistration);

    /**
     * 修改活动报名签到
     * @param bizRegistration 活动报名签到
     * @return 结果
     */
    public int updateBizRegistration(BizRegistration bizRegistration);

    /**
     * 批量删除活动报名签到
     * @param regIds 需要删除的活动报名签到主键集合
     * @return 结果
     */
    public int deleteBizRegistrationByRegIds(Long[] regIds);

    /**
     * 删除活动报名签到信息
     * @param regId 活动报名签到主键
     * @return 结果
     */
    public int deleteBizRegistrationByRegId(Long regId);

    /**
     * [新增] 学生报名活动
     */
    public int registerActivity(Long activityId);

    /**
     * [新增] 学生签到
     */
    public void checkInActivity(Long activityId, String code);

    /**
     * [新增] 获取统计数据
     */
    public List<Map<String, Object>> getDeptStatistics();
}