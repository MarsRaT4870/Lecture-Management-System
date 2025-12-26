package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.dto.ActivityStatsDTO;
import com.ruoyi.biz.domain.entity.BizActivity;
import com.ruoyi.biz.domain.entity.BizRegistration;
import com.ruoyi.biz.mapper.BizActivityMapper;
import com.ruoyi.biz.mapper.BizRegistrationMapper;
import com.ruoyi.biz.service.IBizActivityService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 活动管理 Service 业务层处理
 */
@Service
public class BizActivityServiceImpl extends ServiceImpl<BizActivityMapper, BizActivity> implements IBizActivityService {

    @Autowired
    private BizActivityMapper bizActivityMapper;

    @Autowired
    private BizRegistrationMapper registrationMapper;

    /**
     * 查询活动列表
     */
    @Override
    public List<BizActivity> selectBizActivityList(BizActivity bizActivity) {
        LambdaQueryWrapper<BizActivity> lqw = new LambdaQueryWrapper<>();
        if (bizActivity.getTitle() != null) {
            lqw.like(BizActivity::getTitle, bizActivity.getTitle());
        }
        if (bizActivity.getStatus() != null) {
            lqw.eq(BizActivity::getStatus, bizActivity.getStatus());
        }
        if (bizActivity.getActivityType() != null) {
            lqw.eq(BizActivity::getActivityType, bizActivity.getActivityType());
        }
        lqw.orderByDesc(BizActivity::getCreateTime);
        return this.list(lqw);
    }

    /**
     * 查询活动详细
     */
    @Override
    public BizActivity selectBizActivityByActivityId(Long activityId) {
        // 使用 MP 的 selectById 替代不存在的 selectBizActivityByActivityId
        return bizActivityMapper.selectById(activityId);
    }

    /**
     * 新增活动 (包含场地冲突检测)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBizActivity(BizActivity bizActivity) {
        // 1. 基础校验
        checkTime(bizActivity);

        // 2. 【核心】场地冲突检测
        checkVenueConflict(bizActivity);

        // 3. 设置初始状态
        if (bizActivity.getStatus() == null) {
            bizActivity.setStatus("0"); // 默认为草稿
        }
        bizActivity.setCreateTime(DateUtils.getNowDate());

        // 使用 MP 的 insert 替代 insertBizActivity
        return bizActivityMapper.insert(bizActivity);
    }

    /**
     * 修改活动 (包含场地冲突检测)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBizActivity(BizActivity bizActivity) {
        // 1. 基础校验
        checkTime(bizActivity);

        // 2. 【核心】场地冲突检测
        checkVenueConflict(bizActivity);

        bizActivity.setUpdateTime(DateUtils.getNowDate());

        // 使用 MP 的 updateById 替代 updateBizActivity
        return bizActivityMapper.updateById(bizActivity);
    }

    /**
     * 批量删除活动 (包含级联删除保护)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBizActivityByActivityIds(Long[] activityIds) {
        for (Long id : activityIds) {
            // 1. 【核心】级联删除保护：检查是否有报名记录
            Long count = registrationMapper.selectCount(new LambdaQueryWrapper<BizRegistration>()
                    .eq(BizRegistration::getActivityId, id));

            if (count > 0) {
                BizActivity act = this.getById(id);
                String title = (act != null) ? act.getTitle() : "未知活动";
                throw new ServiceException("活动《" + title + "》已有 " + count + " 人报名，禁止直接删除！请先取消活动或联系管理员。");
            }
        }
        // 使用 MP 的 deleteBatchIds 替代 deleteBizActivityByActivityIds
        return bizActivityMapper.deleteBatchIds(Arrays.asList(activityIds));
    }

    // ================== 业务方法 ==================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitActivity(BizActivity activity) {
        // 1. 完整性校验
        if (activity.getStartTime() == null || activity.getMaxPeople() == null) {
            throw new ServiceException("活动时间、最大人数为必填项");
        }

        // 冲突检测
        checkVenueConflict(activity);

        // 2. 初始状态设置
        activity.setStatus("0"); // 草稿/待发布
        // 根据申报主体决定审核流程
        if ("2".equals(activity.getApplicantType())) {
            activity.setAuditStatus("1"); // 校外主体需校外审核
        } else {
            activity.setAuditStatus("0"); // 校内/个人需校内审核
        }

        return this.save(activity);
    }

    @Override
    public boolean auditActivity(Long activityId, String status, String comment) {
        return this.update(new LambdaUpdateWrapper<BizActivity>()
                .eq(BizActivity::getActivityId, activityId)
                .set(BizActivity::getAuditStatus, status)
                // 审核通过(2)即发布状态(1)
                .set("2".equals(status), BizActivity::getStatus, "1"));
    }

    @Override
    public String generateCheckinCode(Long activityId) {
        String token = IdUtils.fastSimpleUUID();
        this.update(new LambdaUpdateWrapper<BizActivity>()
                .eq(BizActivity::getActivityId, activityId)
                .set(BizActivity::getQrCodeToken, token));
        return token;
    }

    @Override
    public void refreshActivityStatus() {
        Date now = new Date();
        // 自动结束活动：状态为发布中(1) 且 结束时间 < 当前时间
        this.update(new LambdaUpdateWrapper<BizActivity>()
                .eq(BizActivity::getStatus, "1")
                .lt(BizActivity::getEndTime, now)
                .set(BizActivity::getStatus, "2")); // 2代表已结束
    }

    @Override
    public List<ActivityStatsDTO> getSubjectRank() {
        return null;
    }

    // ================== 私有辅助方法 ==================

    private void checkTime(BizActivity bizActivity) {
        if (bizActivity.getStartTime() != null && bizActivity.getEndTime() != null) {
            if (bizActivity.getEndTime().before(bizActivity.getStartTime())) {
                throw new ServiceException("活动结束时间不能早于开始时间");
            }
        }
    }

    /**
     * 检查场地是否冲突
     */
    private void checkVenueConflict(BizActivity current) {
        // 如果是线上活动或者未指定场地，直接跳过
        if (current.getVenueId() == null) {
            return;
        }

        // 查询该场地下的所有非“已取消”和非“已结束”活动
        LambdaQueryWrapper<BizActivity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BizActivity::getVenueId, current.getVenueId());
        lqw.ne(BizActivity::getStatus, "3"); // 排除已取消的活动

        // 如果是修改操作，要排除掉自己
        if (current.getActivityId() != null) {
            lqw.ne(BizActivity::getActivityId, current.getActivityId());
        }

        // 时间重叠查询
        lqw.lt(BizActivity::getStartTime, current.getEndTime());
        lqw.gt(BizActivity::getEndTime, current.getStartTime());

        long count = this.count(lqw);
        if (count > 0) {
            throw new ServiceException("该场地在指定时间段内已被占用，请调整时间或更换场地！");
        }
    }
}