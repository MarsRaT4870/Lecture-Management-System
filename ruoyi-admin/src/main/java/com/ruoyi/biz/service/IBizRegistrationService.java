package com.ruoyi.biz.service;

import java.util.List;
import com.ruoyi.biz.domain.BizRegistration;

/**
 * 报名记录Service接口
 * 
 * @author ruoyi
 * @date 2025-12-17
 */
public interface IBizRegistrationService 
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
     * 批量删除报名记录
     * 
     * @param regIds 需要删除的报名记录主键集合
     * @return 结果
     */
    public int deleteBizRegistrationByRegIds(Long[] regIds);

    /**
     * 删除报名记录信息
     * 
     * @param regId 报名记录主键
     * @return 结果
     */
    public int deleteBizRegistrationByRegId(Long regId);
}
