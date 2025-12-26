package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.BizEvaluation;
import com.ruoyi.biz.mapper.BizEvaluationMapper;
import com.ruoyi.biz.service.IBizEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BizEvaluationServiceImpl extends ServiceImpl<BizEvaluationMapper, BizEvaluation> implements IBizEvaluationService {
    @Autowired
    private BizEvaluationMapper evaluationMapper;

    @Override
    public List<BizEvaluation> selectList() {
        return evaluationMapper.selectEvaluationListWithActivity();
    }
}