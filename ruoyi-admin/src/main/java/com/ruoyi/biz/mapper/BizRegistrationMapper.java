package com.ruoyi.biz.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.biz.domain.BizRegistration;
import org.springframework.stereotype.Repository;

/**
 * 活动报名签到Mapper接口
 */
@Repository
public interface BizRegistrationMapper extends BaseMapper<BizRegistration> {
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
     * 删除活动报名签到
     * @param regId 活动报名签到主键
     * @return 结果
     */
    public int deleteBizRegistrationByRegId(Long regId);

    /**
     * 批量删除活动报名签到
     * @param regIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizRegistrationByRegIds(Long[] regIds);

    /**
     * [新增] 统计学院活跃度
     */
    public List<Map<String, Object>> selectDeptStatistics();
}