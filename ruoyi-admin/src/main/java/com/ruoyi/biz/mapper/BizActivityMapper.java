package com.ruoyi.biz.mapper;

import java.util.List;
import com.ruoyi.biz.domain.BizActivity;
import org.springframework.stereotype.Repository;

/**
 * 学术讲座Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-17
 */
@Repository
public interface BizActivityMapper 
{
    /**
     * 查询学术讲座
     * 
     * @param activityId 学术讲座主键
     * @return 学术讲座
     */
    public BizActivity selectBizActivityByActivityId(Long activityId);

    
    /**
     * 查询学术讲座列表
     * 
     * @param bizActivity 学术讲座
     * @return 学术讲座集合
     */
    public List<BizActivity> selectBizActivityList(BizActivity bizActivity);

    /**
     * 新增学术讲座
     * 
     * @param bizActivity 学术讲座
     * @return 结果
     */
    public int insertBizActivity(BizActivity bizActivity);

    /**
     * 修改学术讲座
     * 
     * @param bizActivity 学术讲座
     * @return 结果
     */
    public int updateBizActivity(BizActivity bizActivity);

    /**
     * 删除学术讲座
     * 
     * @param activityId 学术讲座主键
     * @return 结果
     */
    public int deleteBizActivityByActivityId(Long activityId);

    /**
     * 批量删除学术讲座
     * 
     * @param activityIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizActivityByActivityIds(Long[] activityIds);
}
