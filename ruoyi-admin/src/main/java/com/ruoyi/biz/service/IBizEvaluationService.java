package com.ruoyi.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain.entity.BizEvaluation;

import java.util.List;

public interface IBizEvaluationService extends IService<BizEvaluation> {
    List<BizEvaluation> selectList();
}