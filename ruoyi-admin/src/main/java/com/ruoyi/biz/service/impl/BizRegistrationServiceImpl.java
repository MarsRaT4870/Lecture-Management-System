package com.ruoyi.biz.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.biz.mapper.BizRegistrationMapper;
import com.ruoyi.biz.mapper.BizActivityMapper;
import com.ruoyi.biz.domain.BizRegistration;
import com.ruoyi.biz.domain.BizActivity;
import com.ruoyi.biz.service.IBizRegistrationService;
import com.ruoyi.biz.enums.ActivityStatus;
import com.ruoyi.biz.enums.RegistrationStatus;

/**
 * 报名记录Service业务层处理
 * 重构版：包含智能校验与自动填表
 */
@Service
public class BizRegistrationServiceImpl implements IBizRegistrationService
{
    @Autowired
    private BizRegistrationMapper bizRegistrationMapper;

    @Autowired
    private BizActivityMapper bizActivityMapper; // 引入活动Mapper查询活动详情

    @Autowired
    private ISysUserService userService; // 引入用户Service查询当前学生信息

    /**
     * 查询报名记录
     */
    @Override
    public BizRegistration selectBizRegistrationByRegId(Long regId)
    {
        return bizRegistrationMapper.selectBizRegistrationByRegId(regId);
    }

    /**
     * 查询报名记录列表
     */
    @Override
    public List<BizRegistration> selectBizRegistrationList(BizRegistration bizRegistration)
    {
        // 获取当前用户ID
        Long userId = SecurityUtils.getUserId();

        // 修复点：isAdmin 需要传入 userId 才能判断该用户是否为管理员
        if (!SecurityUtils.isAdmin(userId)) {
            bizRegistration.setUserId(userId);
        }

        return bizRegistrationMapper.selectBizRegistrationList(bizRegistration);
    }

    /**
     * 新增报名记录 (核心业务逻辑)
     */
    @Override
    public int insertBizRegistration(BizRegistration bizRegistration)
    {
        // 1. 获取当前活动信息
        Long activityId = bizRegistration.getActivityId();
        BizActivity activity = bizActivityMapper.selectBizActivityByActivityId(activityId);

        // --- 校验A: 活动是否存在 ---
        if (activity == null) {
            throw new ServiceException("目标活动不存在");
        }

        // --- 校验B: 活动是否已结束/关闭 ---
        // 使用枚举 ActivityStatus.CLOSED ("1")
        if (ActivityStatus.CLOSED.getCode().equals(activity.getStatus())) {
            throw new ServiceException("活动已结束，无法报名");
        }

        // 2. 获取当前登录用户信息
        Long userId = SecurityUtils.getUserId();
        SysUser currentUser = userService.selectUserById(userId);

        // --- 校验C: 是否重复报名 ---
        BizRegistration queryParam = new BizRegistration();
        queryParam.setActivityId(activityId);
        queryParam.setUserId(userId);
        List<BizRegistration> exists = bizRegistrationMapper.selectBizRegistrationList(queryParam);

        // 如果查到了记录，且状态不是“已取消”，则视为重复
        for (BizRegistration reg : exists) {
            if (!RegistrationStatus.CANCELLED.getCode().equals(reg.getStatus())) {
                throw new ServiceException("您已报名该活动，请勿重复操作");
            }
        }

        // --- 校验D: 人数限制 (防超员) ---
        // 如果设置了最大人数 (maxPeople > 0)
        if (activity.getMaxPeople() != null && activity.getMaxPeople() > 0) {
            int currentCount = bizRegistrationMapper.selectRegistrationCount(activityId);
            if (currentCount >= activity.getMaxPeople()) {
                throw new ServiceException("报名火爆，名额已满！");
            }
        }

        // 3. 自动填表 (填充冗余字段，方便以后统计)
        bizRegistration.setUserId(userId);
        bizRegistration.setUserName(currentUser.getNickName()); // 填充姓名
        bizRegistration.setDeptName(currentUser.getDept() != null ? currentUser.getDept().getDeptName() : "未知学院"); // 填充学院
        // 假设 SysUser 的 remark 字段存的是学号，如果没有专门的字段的话
        bizRegistration.setStudentNo(currentUser.getRemark());

        // 4. 初始化状态
        bizRegistration.setStatus(RegistrationStatus.REGISTERED.getCode()); // 设置为 '0' (已报名)
        bizRegistration.setCreateTime(DateUtils.getNowDate()); // 报名时间

        return bizRegistrationMapper.insertBizRegistration(bizRegistration);
    }

    /**
     * 修改报名记录 (用于签到、评价、取消报名)
     */
    @Override
    public int updateBizRegistration(BizRegistration bizRegistration)
    {
        return bizRegistrationMapper.updateBizRegistration(bizRegistration);
    }

    /**
     * 批量删除
     */
    @Override
    public int deleteBizRegistrationByRegIds(Long[] regIds)
    {
        return bizRegistrationMapper.deleteBizRegistrationByRegIds(regIds);
    }

    /**
     * 删除单条
     */
    @Override
    public int deleteBizRegistrationByRegId(Long regId)
    {
        return bizRegistrationMapper.deleteBizRegistrationByRegId(regId);
    }


    @Override
    public List<java.util.Map<String, Object>> selectDeptStats() {
        return bizRegistrationMapper.selectDeptStats();
    }

    @Override
    public List<java.util.Map<String, Object>> selectActivityStats() {
        return bizRegistrationMapper.selectActivityStats();
    }

}