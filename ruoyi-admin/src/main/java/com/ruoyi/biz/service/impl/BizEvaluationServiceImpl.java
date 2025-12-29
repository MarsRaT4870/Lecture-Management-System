package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.entity.BizEvaluation;
import com.ruoyi.biz.domain.entity.BizRegistration;
import com.ruoyi.biz.mapper.BizEvaluationMapper;
import com.ruoyi.biz.service.IBizEvaluationService;
import com.ruoyi.biz.service.IBizMessageService;
import com.ruoyi.biz.service.IBizRegistrationService;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class BizEvaluationServiceImpl extends ServiceImpl<BizEvaluationMapper, BizEvaluation> implements IBizEvaluationService {
    @Autowired
    private BizEvaluationMapper evaluationMapper;
    @Autowired
    private IBizRegistrationService registrationService;
    @Autowired
    private IBizMessageService messageService;

    @Override
    public List<BizEvaluation> selectList() {
        return evaluationMapper.selectEvaluationListWithActivity();
    }

    @Override
    @Transactional
    public boolean submitEvaluation(BizEvaluation eval) {
        // 1. 资格校验：必须已签到
        BizRegistration reg = registrationService.getOne(new LambdaQueryWrapper<BizRegistration>()
                .eq(BizRegistration::getActivityId, eval.getActivityId())
                .eq(BizRegistration::getUserId, eval.getUserId())
                .eq(BizRegistration::getStatus, "2")); // 2=已签到

        if (reg == null) {
            throw new ServiceException("您未签到或未报名，无法评价");
        }

        // 2. 防重复评价
        long count = this.count(new LambdaQueryWrapper<BizEvaluation>()
                .eq(BizEvaluation::getRegId, reg.getRegId()));
        if (count > 0) {
            throw new ServiceException("您已评价过该活动");
        }

        // 3. 计算综合评分 (加权平均)
        // 假设权重：内容50%，讲师30%，环境20%
        double avg = (eval.getScoreContent() * 0.5) +
                (eval.getScoreSpeaker() * 0.3) +
                (eval.getScoreEnv() * 0.2);
        eval.setScore((int) Math.round(avg)); // 四舍五入存总分

        eval.setRegId(reg.getRegId());
        eval.setCreateTime(new Date());

        // 4. 敏感词过滤 (示例)
        if (eval.getComment().contains("垃圾")) {
            throw new ServiceException("评价包含不文明用语");
        }

        boolean success = this.save(eval);

        // 5. 差评预警 (智能逻辑)
        if (success && eval.getScore() <= 2) {
            // 发送给管理员
            messageService.sendMessage(1L, "Admin", "差评预警",
                    "活动ID " + eval.getActivityId() + " 收到一条差评，请及时关注。", "1");
        }

        return success;
    }

}