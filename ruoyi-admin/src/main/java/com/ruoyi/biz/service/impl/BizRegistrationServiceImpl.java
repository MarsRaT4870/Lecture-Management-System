package com.ruoyi.biz.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.biz.mapper.BizRegistrationMapper;
import com.ruoyi.biz.domain.BizRegistration;
import com.ruoyi.biz.domain.BizActivity;
import com.ruoyi.biz.service.IBizRegistrationService;
import com.ruoyi.biz.service.IBizActivityService;

/**
 * 报名记录Service业务层处理
 */
@Service
public class BizRegistrationServiceImpl implements IBizRegistrationService
{
    @Autowired
    private BizRegistrationMapper bizRegistrationMapper;

    @Autowired
    private IBizActivityService bizActivityService; // 必须注入活动服务

    /**
     * 查询报名记录
     */
    @Override
    public BizRegistration selectBizRegistrationById(Long regId)
    {
        return bizRegistrationMapper.selectBizRegistrationById(regId);
    }

    /**
     * 查询报名记录列表
     */
    @Override
    public List<BizRegistration> selectBizRegistrationList(BizRegistration bizRegistration)
    {
        // 如果是普通用户（非管理员），强制只查自己的数据
        Long userId = SecurityUtils.getUserId();
        if (!SecurityUtils.isAdmin(userId)) {
            bizRegistration.setUserId(userId);
        }
        return bizRegistrationMapper.selectBizRegistrationList(bizRegistration);
    }

    /**
     * 新增报名记录
     */
    @Override
    public int insertBizRegistration(BizRegistration bizRegistration)
    {
        // 1. 获取并设置当前用户信息
        Long userId = SecurityUtils.getUserId();
        bizRegistration.setUserId(userId);
        bizRegistration.setUserName(SecurityUtils.getUsername());
        bizRegistration.setCreateTime(DateUtils.getNowDate());

        // 默认状态设为"已报名"(1)
        if (bizRegistration.getStatus() == null) {
            bizRegistration.setStatus("1");
        }

        // 2.【校验】检查是否重复报名
        BizRegistration queryParams = new BizRegistration();
        queryParams.setActivityId(bizRegistration.getActivityId());
        queryParams.setUserId(userId);
        // 注意：这里调用 Mapper 直接查，避免触发上面的 SecurityUtils.isAdmin 过滤逻辑影响判断
        List<BizRegistration> list = bizRegistrationMapper.selectBizRegistrationList(queryParams);
        if (list.size() > 0) {
            throw new ServiceException("您已经报名过该活动，请勿重复报名！");
        }

        // 3.【校验】检查活动人数是否已满
        BizActivity activity = bizActivityService.selectBizActivityByActivityId(bizRegistration.getActivityId());
        if (activity == null) {
            throw new ServiceException("活动不存在！");
        }

        // 统计当前活动已报名人数
        BizRegistration countParams = new BizRegistration();
        countParams.setActivityId(bizRegistration.getActivityId());
        List<BizRegistration> currentList = bizRegistrationMapper.selectBizRegistrationList(countParams);

        // 假设 maxPeople 为 0 或 null 代表不限人数
        if (activity.getMaxPeople() != null && activity.getMaxPeople() > 0) {
            if (currentList.size() >= activity.getMaxPeople()) {
                throw new ServiceException("很抱歉，该活动名额已满！");
            }
        }

        return bizRegistrationMapper.insertBizRegistration(bizRegistration);
    }

    /**
     * 修改报名记录
     */
    @Override
    public int updateBizRegistration(BizRegistration bizRegistration)
    {
        return bizRegistrationMapper.updateBizRegistration(bizRegistration);
    }

    /**
     * 批量删除报名记录
     */
    @Override
    public int deleteBizRegistrationByRegIds(Long[] regIds)
    {
        return bizRegistrationMapper.deleteBizRegistrationByRegIds(regIds);
    }

    @Override
    public List<Map<String, Object>> selectDeptStats() {
        return bizRegistrationMapper.selectDeptStats();
    }

    @Override
    public List<Map<String, Object>> selectActivityStats() {
        return bizRegistrationMapper.selectActivityStats();
    }
}