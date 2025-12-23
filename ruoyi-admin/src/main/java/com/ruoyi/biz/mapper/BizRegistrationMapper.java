package com.ruoyi.biz.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.biz.domain.BizRegistration;

/**
 * 报名记录Mapper接口
 */
public interface BizRegistrationMapper
{
    /**
     * 查询报名记录
     */
    public BizRegistration selectBizRegistrationById(Long regId);

    /**
     * 查询报名记录列表
     */
    public List<BizRegistration> selectBizRegistrationList(BizRegistration bizRegistration);

    /**
     * 新增报名记录
     */
    public int insertBizRegistration(BizRegistration bizRegistration);

    /**
     * 修改报名记录
     */
    public int updateBizRegistration(BizRegistration bizRegistration);

    /**
     * 批量删除报名记录
     */
    public int deleteBizRegistrationByRegIds(Long[] regIds);

    // 统计接口
    public List<Map<String, Object>> selectDeptStats();
    public List<Map<String, Object>> selectActivityStats();
}