package com.ruoyi.biz.mapper;

import java.util.List;
import com.ruoyi.biz.domain.BizRegistration;
import org.springframework.stereotype.Repository;

/**
 * 报名记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-17
 */
@Repository
public interface BizRegistrationMapper 
{
    /**
     * 查询报名记录
     * 
     * @param regId 报名记录主键
     * @return 报名记录
     */
    public BizRegistration selectBizRegistrationByRegId(Long regId);

    /**
     * 查询报名记录列表
     * 
     * @param bizRegistration 报名记录
     * @return 报名记录集合
     */
    public List<BizRegistration> selectBizRegistrationList(BizRegistration bizRegistration);

    /**
     * 新增报名记录
     * 
     * @param bizRegistration 报名记录
     * @return 结果
     */
    public int insertBizRegistration(BizRegistration bizRegistration);

    /**
     * 修改报名记录
     * 
     * @param bizRegistration 报名记录
     * @return 结果
     */
    public int updateBizRegistration(BizRegistration bizRegistration);

    /**
     * 删除报名记录
     * 
     * @param regId 报名记录主键
     * @return 结果
     */
    public int deleteBizRegistrationByRegId(Long regId);

    /**
     * 批量删除报名记录
     * 
     * @param regIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizRegistrationByRegIds(Long[] regIds);

    /**
     * 统计指定活动的有效报名人数（排除已取消的）
     * @param activityId 活动ID
     * @return 报名人数
     */
    public int selectRegistrationCount(Long activityId);
}
