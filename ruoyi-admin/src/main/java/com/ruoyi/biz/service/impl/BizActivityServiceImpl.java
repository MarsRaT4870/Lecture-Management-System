package com.ruoyi.biz.service.impl;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.biz.mapper.BizActivityMapper;
import com.ruoyi.biz.domain.BizActivity;
import com.ruoyi.biz.service.IBizActivityService;

/**
 * 学术讲座Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-17
 */
@Service
public class BizActivityServiceImpl implements IBizActivityService 
{
    @Autowired
    private BizActivityMapper bizActivityMapper;

    /**
     * 查询学术讲座
     * 
     * @param activityId 学术讲座主键
     * @return 学术讲座
     */
    @Override
    public BizActivity selectBizActivityByActivityId(Long activityId)
    {
        return bizActivityMapper.selectBizActivityByActivityId(activityId);
    }

    /**
     * 查询学术讲座列表
     * 
     * @param bizActivity 学术讲座
     * @return 学术讲座
     */
    @Override
    public List<BizActivity> selectBizActivityList(BizActivity bizActivity)
    {
        return bizActivityMapper.selectBizActivityList(bizActivity);
    }


    /**
     * 新增学术讲座
     */
    @Override
    public int insertBizActivity(BizActivity bizActivity)
    {
        bizActivity.setCreateTime(DateUtils.getNowDate());

        // --- 新增逻辑：如果前端没传签到码，系统自动生成一个6位数字码 ---
        if (bizActivity.getCheckinCode() == null || bizActivity.getCheckinCode().isEmpty()) {
            // 生成 100000 - 999999 之间的随机数
            int code = ThreadLocalRandom.current().nextInt(100000, 999999);
            bizActivity.setCheckinCode(String.valueOf(code));
        }

        return bizActivityMapper.insertBizActivity(bizActivity);
    }

    /**
     * 修改学术讲座
     * 
     * @param bizActivity 学术讲座
     * @return 结果
     */
    @Override
    public int updateBizActivity(BizActivity bizActivity)
    {
        bizActivity.setUpdateTime(DateUtils.getNowDate());
        return bizActivityMapper.updateBizActivity(bizActivity);
    }

    /**
     * 批量删除学术讲座
     * 
     * @param activityIds 需要删除的学术讲座主键
     * @return 结果
     */
    @Override
    public int deleteBizActivityByActivityIds(Long[] activityIds)
    {
        return bizActivityMapper.deleteBizActivityByActivityIds(activityIds);
    }

    /**
     * 删除学术讲座信息
     * 
     * @param activityId 学术讲座主键
     * @return 结果
     */
    @Override
    public int deleteBizActivityByActivityId(Long activityId)
    {
        return bizActivityMapper.deleteBizActivityByActivityId(activityId);
    }
}
