package com.ruoyi.biz.service.impl;

import java.util.List;
import java.util.Map;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.biz.mapper.BizRegistrationMapper;
import com.ruoyi.biz.domain.BizRegistration;
import com.ruoyi.biz.service.IBizRegistrationService;
import com.ruoyi.biz.mapper.BizActivityMapper;
import com.ruoyi.biz.domain.BizActivity;

/**
 * 活动报名签到Service业务层处理
 */
@Service
public class BizRegistrationServiceImpl implements IBizRegistrationService {

    @Autowired
    private BizRegistrationMapper bizRegistrationMapper;

    @Autowired
    private BizActivityMapper bizActivityMapper;

    @Override
    public BizRegistration selectBizRegistrationByRegId(Long regId) {
        return bizRegistrationMapper.selectBizRegistrationByRegId(regId);
    }

    @Override
    public List<BizRegistration> selectBizRegistrationList(BizRegistration bizRegistration) {
        return bizRegistrationMapper.selectBizRegistrationList(bizRegistration);
    }

    // 核心报名逻辑
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int registerActivity(Long activityId) {
        Long userId = SecurityUtils.getUserId();
        String username = SecurityUtils.getUsername();
        String deptName = "公共学院";
        try {
            deptName = SecurityUtils.getLoginUser().getUser().getDept().getDeptName();
        } catch (Exception e) {
            // 忽略无部门情况
        }

        // 修改点：使用 selectBizActivityById (RuoYi默认生成的方法名)
        BizActivity activity = bizActivityMapper.selectBizActivityByActivityId(activityId);

        if (activity == null) {
            throw new ServiceException("活动不存在");
        }
        if ("1".equals(activity.getStatus())) {
            throw new ServiceException("活动已关闭");
        }
        if (DateUtils.getNowDate().after(activity.getEndTime())) {
            throw new ServiceException("活动已结束");
        }

        // 查重
        BizRegistration query = new BizRegistration();
        query.setActivityId(activityId);
        query.setUserId(userId);
        List<BizRegistration> list = bizRegistrationMapper.selectBizRegistrationList(query);
        if (list.size() > 0) {
            throw new ServiceException("请勿重复报名");
        }

        BizRegistration reg = new BizRegistration();
        reg.setActivityId(activityId);
        reg.setUserId(userId);
        reg.setUserName(username);
        reg.setDeptName(deptName);
        reg.setStatus("0"); // 0-已报名
        reg.setCreateTime(DateUtils.getNowDate());

        return bizRegistrationMapper.insertBizRegistration(reg);
    }

    // 核心签到逻辑
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkInActivity(Long activityId, String code) {
        Long userId = SecurityUtils.getUserId();

        // 修改点：使用 selectBizActivityById
        BizActivity activity = bizActivityMapper.selectBizActivityByActivityId(activityId);

        if (activity == null) {
            throw new ServiceException("活动不存在");
        }

        if (StringUtils.isEmpty(code) || !code.equals(activity.getCheckinCode())) {
            throw new ServiceException("签到码错误");
        }

        BizRegistration query = new BizRegistration();
        query.setActivityId(activityId);
        query.setUserId(userId);
        List<BizRegistration> list = bizRegistrationMapper.selectBizRegistrationList(query);

        if (list.isEmpty()) {
            throw new ServiceException("未报名，无法签到");
        }

        BizRegistration reg = list.get(0);
        if ("1".equals(reg.getStatus())) {
            throw new ServiceException("您已签到，无需重复");
        }

        reg.setStatus("1"); // 1-已签到
        reg.setCheckinTime(DateUtils.getNowDate());
        bizRegistrationMapper.updateBizRegistration(reg);
    }

    @Override
    public List<Map<String, Object>> getDeptStatistics() {
        return bizRegistrationMapper.selectDeptStatistics();
    }

    // 基础CRUD实现
    @Override
    public int insertBizRegistration(BizRegistration bizRegistration) {
        bizRegistration.setCreateTime(DateUtils.getNowDate());
        return bizRegistrationMapper.insertBizRegistration(bizRegistration);
    }

    @Override
    public int updateBizRegistration(BizRegistration bizRegistration) {
        return bizRegistrationMapper.updateBizRegistration(bizRegistration);
    }

    @Override
    public int deleteBizRegistrationByRegIds(Long[] regIds) {
        return bizRegistrationMapper.deleteBizRegistrationByRegIds(regIds);
    }

    @Override
    public int deleteBizRegistrationByRegId(Long regId) {
        return bizRegistrationMapper.deleteBizRegistrationByRegId(regId);
    }
}