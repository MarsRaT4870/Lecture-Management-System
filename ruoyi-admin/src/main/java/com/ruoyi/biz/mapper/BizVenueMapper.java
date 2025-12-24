package com.ruoyi.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.biz.domain.BizVenue;
import org.apache.ibatis.annotations.Mapper;

/**
 * 场地 Mapper 接口
 * 继承 BaseMapper 后，无需编写 XML 即可拥有 CRUD 能力
 */
@Mapper
public interface BizVenueMapper extends BaseMapper<BizVenue> {
    // 这里留空即可，MyBatis-Plus 会自动处理
}