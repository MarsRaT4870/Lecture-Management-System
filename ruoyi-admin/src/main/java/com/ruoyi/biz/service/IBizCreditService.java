package com.ruoyi.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain.entity.BizCredit;

import java.util.List;

public interface IBizCreditService extends IService<BizCredit> {
    // 生成证书逻辑
    void generateCredit(Long activityId);
}