package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

// 【关键】根据报错修正引用路径
import com.ruoyi.biz.domain.entity.BizActivity;
import com.ruoyi.biz.domain.entity.BizRegistration;
// 这里的 BizCredit 是新模块，通常还在 domain 包下，如果报错请改为 domain.entity
import com.ruoyi.biz.domain.entity.BizCredit;

import com.ruoyi.biz.mapper.BizActivityMapper;
import com.ruoyi.biz.mapper.BizCreditMapper;
import com.ruoyi.biz.mapper.BizRegistrationMapper;
import com.ruoyi.biz.service.IBizCreditService;
import com.ruoyi.biz.service.IBizMessageService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class BizCreditServiceImpl extends ServiceImpl<BizCreditMapper, BizCredit> implements IBizCreditService {

    @Autowired
    private BizRegistrationMapper registrationMapper;
    @Autowired
    private BizActivityMapper activityMapper;

    @Autowired
    private IBizMessageService messageService; // 注入消息服务

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateCredit(Long activityId) {
        // 1. 获取活动信息
        BizActivity activity = activityMapper.selectById(activityId);
        if (activity == null) return;

        // 2. 查询所有已签到(status='2') 且 还没有学分记录 的学生
        LambdaQueryWrapper<BizRegistration> regWrapper = new LambdaQueryWrapper<>();
        regWrapper.eq(BizRegistration::getActivityId, activityId)
                .eq(BizRegistration::getStatus, "2"); // 2=已签到

        List<BizRegistration> users = registrationMapper.selectList(regWrapper);

        // 3. 批量生成证书
        for (BizRegistration reg : users) {
            // 检查是否已经发过，防止重复发放
            Long count = this.count(new LambdaQueryWrapper<BizCredit>()
                    .eq(BizCredit::getActivityId, activityId)
                    .eq(BizCredit::getUserId, reg.getUserId()));

            if (count == 0) {
                BizCredit credit = new BizCredit();
                credit.setUserId(reg.getUserId());
                credit.setUserName(reg.getUserName());

                // 如果 Registration 实体没有 deptName 字段，请注释掉下面这行
                // credit.setDeptName(reg.getDeptName());

                credit.setActivityId(activityId);
                credit.setActivityName(activity.getTitle());
                // 这里可以根据活动类型判断学分值，暂时写死 0.5
                credit.setCreditVal(0.5);
                credit.setGrantTime(new Date());
                credit.setCertificateNo("CERT" + DateUtils.dateTimeNow() + IdUtils.fastSimpleUUID().substring(0, 4).toUpperCase());

                // 保存学分记录
                this.save(credit);

                // 4. 【新增】发送学分到账通知
                // type: "1" 代表系统通知
                try {
                    messageService.sendMessage(
                            reg.getUserId(),
                            reg.getUserName(),
                            "学分到账通知",
                            "恭喜！您参与的《" + activity.getTitle() + "》已获得 0.5 个第二课堂学分，证书已生成。",
                            "1"
                    );
                } catch (Exception e) {
                    log.error("发送学分通知失败", e);
                }
            }
        }
    }
}