package com.ruoyi.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain.dto.ActivityStatsDTO;
import com.ruoyi.biz.domain.entity.BizActivity;
import java.util.List;

/**
 * 活动管理 Service 接口
 */
public interface IBizActivityService extends IService<BizActivity> {

    /**
     * 查询活动列表
     */
    List<BizActivity> selectBizActivityList(BizActivity bizActivity);

    /**
     * 查询活动详细
     */
    BizActivity selectBizActivityByActivityId(Long activityId);

    /**
     * 新增活动
     */
    int insertBizActivity(BizActivity bizActivity);

    /**
     * 修改活动
     */
    int updateBizActivity(BizActivity bizActivity);

    /**
     * 批量删除活动
     */
    int deleteBizActivityByActivityIds(Long[] activityIds);

    /**
     * 提交活动 (业务方法)
     */
    boolean submitActivity(BizActivity activity);

    /**
     * 审核活动
     */
    boolean auditActivity(Long activityId, String status, String comment);

    /**
     * 生成签到码
     */
    String generateCheckinCode(Long activityId);

    /**
     * 刷新活动状态
     */
    void refreshActivityStatus();

    /**
     * 获取学科排行
     */
    List<ActivityStatsDTO> getSubjectRank();


    // 【新增】手动归档/下架活动接口
    boolean archiveActivity(Long activityId);
}