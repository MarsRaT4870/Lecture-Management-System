package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.entity.BizVenue;
import com.ruoyi.biz.mapper.BizVenueMapper;
import com.ruoyi.biz.service.IBizVenueService;
import org.springframework.stereotype.Service;

/**
 * 场地 Service 业务层处理
 */
@Service
public class BizVenueServiceImpl extends ServiceImpl<BizVenueMapper, BizVenue> implements IBizVenueService
{
    // 所有的基础方法（如 save, removeBatchByIds, getById）都已由父类实现
}