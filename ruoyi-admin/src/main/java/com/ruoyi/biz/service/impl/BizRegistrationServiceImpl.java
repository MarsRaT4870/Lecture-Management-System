package com.ruoyi.biz.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.dto.CheckInCommand;
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
public class BizRegistrationServiceImpl extends ServiceImpl<BizRegistrationMapper, BizRegistration> implements IBizRegistrationService {

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
        // 【修复1】方法名修正：selectBizActivityById -> selectBizActivityByActivityId
        BizActivity activity = bizActivityMapper.selectBizActivityByActivityId(activityId);

        if (activity == null) {
            throw new ServiceException("活动不存在");
        }

        // 校验签到码 (防止空指针)
        String dbCode = activity.getCheckinCode();
        if (dbCode == null || dbCode.isEmpty()) {
            throw new ServiceException("该活动未设置签到码，无法签到");
        }
        if (!dbCode.equals(code)) {
            throw new ServiceException("签到码错误");
        }

        // 构造查询条件：查当前用户的报名记录
        BizRegistration query = new BizRegistration();
        query.setActivityId(activityId);
        // 【修复2】获取当前用户ID (原代码这里会报错)
        query.setUserId(SecurityUtils.getUserId());

        List<BizRegistration> list = bizRegistrationMapper.selectBizRegistrationList(query);
        if (list.isEmpty()) {
            throw new ServiceException("您未报名该活动，无法签到");
        }

        BizRegistration reg = list.get(0);

        // 【修复3】状态逻辑修正：2 代表已签到
        if ("2".equals(reg.getStatus())) {
            throw new ServiceException("您已签到，无需重复操作");
        }

        reg.setStatus("2"); // 设置为“已签到”状态
        // 这里假设你有 setCheckinTime 方法，如果没有可以删掉下面这行
        reg.setCheckinTime(new java.util.Date());

        bizRegistrationMapper.updateBizRegistration(reg);
    }

    @Override
    public List<Map<String, Object>> getDeptStatistics() {
        return bizRegistrationMapper.selectDeptStatistics();
    }

    /**
     * 新增报名（含防重复逻辑）
     */
    @Override
    public int insertBizRegistration(BizRegistration bizRegistration) {
        // 1. 自动填入报名时间
        bizRegistration.setCreateTime(DateUtils.getNowDate());

        // 2. 设置默认状态：如果是学生自主报名，通常是 "0"(待审核) 或 "1"(已报名)
        // 这里我们默认设为 0 (待审核)，或者你可以改成 1
        if (bizRegistration.getStatus() == null) {
            bizRegistration.setStatus("0");
        }

        // --- 【核心修改】防重复报名检查 ---
        BizRegistration query = new BizRegistration();
        query.setActivityId(bizRegistration.getActivityId());
        query.setUserId(bizRegistration.getUserId());

        // 查询该用户在这个活动下有没有记录
        List<BizRegistration> list = bizRegistrationMapper.selectBizRegistrationList(query);

        if (list.size() > 0) {
            // 如果查到了，直接抛出异常，阻止写入
            throw new ServiceException("您已经报名过该活动了，请勿重复操作！");
        }
        // ------------------------------------

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


    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务，保证严谨性
    public void executeCheckIn(CheckInCommand command) {
        // 1. 使用 Lambda 查询，查出该用户对该活动的报名记录
        BizRegistration record = this.getOne(
                new LambdaQueryWrapper<BizRegistration>()
                        .eq(BizRegistration::getActivityId, command.getActivityId())
                        .eq(BizRegistration::getUserId, command.getUserId())
        );

        // 2. 严谨的逻辑校验 (防御性编程)
        if (record == null) {
            throw new ServiceException("签到失败：您未报名参加该活动！");
        }
        if ("2".equals(record.getStatus())) {
            throw new ServiceException("请勿重复操作，您已签到成功！");
        }
        if (!"1".equals(record.getStatus())) { // 假设 "1" 是已报名(审核通过)
            throw new ServiceException("您的报名状态异常（未审核或已取消），无法签到。");
        }

        // 3. (可选) 校验 Token 或 GPS 距离
        // if (!isValidToken(command.getCheckInToken())) ...

        // 4. 执行签到更新 (使用 Lambda Update)
        boolean updateSuccess = this.update(
                new LambdaUpdateWrapper<BizRegistration>()
                        .set(BizRegistration::getStatus, "2") // 状态变为已签到
                        .set(BizRegistration::getCheckinTime, new Date()) // 记录当前时间
                        .eq(BizRegistration::getRegId, record.getRegId()) // 锁定主键更新
        );

        if (!updateSuccess) {
            throw new ServiceException("系统繁忙，签到更新失败");
        }
    }

}