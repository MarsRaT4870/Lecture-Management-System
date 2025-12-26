package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.dto.ActivityStatsDTO;
import com.ruoyi.biz.domain.entity.BizActivity;
import com.ruoyi.biz.domain.entity.BizRegistration;
import com.ruoyi.biz.domain.entity.BizVenue;
import com.ruoyi.biz.mapper.BizActivityMapper;
import com.ruoyi.biz.mapper.BizRegistrationMapper;
import com.ruoyi.biz.mapper.BizVenueMapper;
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

    @Autowired
    private BizVenueMapper venueMapper; // 注入场地Mapper

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
        // 【新增】如果前端传了 visible 过滤 (例如大厅只看 visible=1)，则加上条件
        if (bizActivity.getVisible() != null) {
            lqw.eq(BizActivity::getVisible, bizActivity.getVisible());
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
     * 新增活动 (包含各项业务规则校验)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBizActivity(BizActivity bizActivity) {
        // 1. 统一业务规则校验 (时间、场地容量、冲突等)
        checkBusinessRules(bizActivity);

        // 2. 设置初始状态
        if (bizActivity.getStatus() == null) {
            bizActivity.setStatus("0"); // 默认为草稿
        }
        // 默认上架显示
        if (bizActivity.getVisible() == null) {
            bizActivity.setVisible("1");
        }
        bizActivity.setCreateTime(DateUtils.getNowDate());

        // 使用 MP 的 insert 替代 insertBizActivity
        return bizActivityMapper.insert(bizActivity);
    }

    /**
     * 修改活动 (包含各项业务规则校验)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBizActivity(BizActivity bizActivity) {
        // 1. 统一业务规则校验
        checkBusinessRules(bizActivity);

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
                throw new ServiceException("活动《" + title + "》已有 " + count + " 人报名，禁止直接删除！请使用‘下架’功能或联系管理员。");
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

        // 2. 统一业务规则校验
        checkBusinessRules(activity);

        // 3. 初始状态设置
        activity.setStatus("0"); // 草稿/待发布
        activity.setVisible("1"); // 默认可见

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

    // 【新增】手动下架/归档活动
    public boolean archiveActivity(Long activityId) {
        BizActivity act = this.getById(activityId);
        if (act == null) {
            throw new ServiceException("活动不存在");
        }
        // 只有“已结束”状态才能下架，防止误操作正在进行的活动
        if (!"2".equals(act.getStatus())) {
            throw new ServiceException("只有‘已结束’的活动才能下架归档！");
        }

        act.setVisible("0"); // 设置为隐藏/归档
        return this.updateById(act);
    }

    @Override
    public List<ActivityStatsDTO> getSubjectRank() {
        return null;
    }

    // ================== 私有辅助方法 ==================

    /**
     * 统一业务规则校验
     */
    private void checkBusinessRules(BizActivity activity) {
        // 1. 基础时间校验：结束 > 开始
        checkTime(activity);

        // 2. 【核心修复2】活动必须提前一周申报
        // 只有在修改或新增“开始时间”时才校验。如果只是修改描述，不应该拦截（除非时间也变了）。
        // 这里简单处理：只要涉及开始时间，就必须符合“未来一周”原则。
        if (activity.getStartTime() != null) {
            long now = System.currentTimeMillis();
            long sevenDaysLater = now + (7L * 24 * 60 * 60 * 1000); // 7天毫秒数
            // 允许1分钟误差
            if (activity.getStartTime().getTime() < (sevenDaysLater - 60000)) {
                throw new ServiceException("根据规定，活动必须提前至少一周(7天)进行申报！");
            }
        }

        // 3. 报名截止时间校验
        if (activity.getStartTime() != null && activity.getRegDeadline() != null) {
            long limitTime = activity.getStartTime().getTime() - (2 * 60 * 60 * 1000);
            if (activity.getRegDeadline().getTime() > limitTime) {
                throw new ServiceException("报名截止时间必须至少比活动开始时间早 2 小时！");
            }
        }

        // 4. 场地容量校验
        if (activity.getVenueId() != null && activity.getMaxPeople() != null) {
            BizVenue venue = venueMapper.selectById(activity.getVenueId());
            if (venue != null && activity.getMaxPeople() > venue.getCapacity()) {
                throw new ServiceException("设置人数(" + activity.getMaxPeople() + ")超出了场地最大容量(" + venue.getCapacity() + ")");
            }
        }

        // 5. 冲突检测
        checkVenueConflict(activity);
    }

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