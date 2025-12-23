package com.ruoyi.biz.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.biz.domain.BizRegistration;

/**
 * 报名记录Service接口
 */
public interface IBizRegistrationService
{
    /**
     * 查询报名记录
     * @param regId 报名记录主键
     * @return 报名记录
     */
    public BizRegistration selectBizRegistrationById(Long regId);

    /**
     * 查询报名记录列表
     * @param bizRegistration 报名记录
     * @return 报名记录集合
     */
    public List<BizRegistration> selectBizRegistrationList(BizRegistration bizRegistration);

    /**
     * 新增报名记录 (包含业务校验逻辑)
     * @param bizRegistration 报名记录
     * @return 结果
     */
    public int insertBizRegistration(BizRegistration bizRegistration);

    /**
     * 修改报名记录
     * @param bizRegistration 报名记录
     * @return 结果
     */
    public int updateBizRegistration(BizRegistration bizRegistration);

    /**
     * 批量删除报名记录
     * @param regIds 需要删除的报名记录主键集合
     * @return 结果
     */
    public int deleteBizRegistrationByRegIds(Long[] regIds);

    // --- 统计相关接口 (如果 XML 没写对应的 SQL，调用会报错，暂时保留定义) ---
    public List<Map<String, Object>> selectDeptStats();
    public List<Map<String, Object>> selectActivityStats();
}