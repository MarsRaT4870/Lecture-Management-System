package com.ruoyi.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.biz.domain.entity.BizEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BizEvaluationMapper extends BaseMapper<BizEvaluation> {
    // 自定义查询：关联查询活动名称，方便管理端展示
    @Select("SELECT e.*, a.title as activity_name FROM biz_evaluation e " +
            "LEFT JOIN biz_activity a ON e.activity_id = a.activity_id " +
            "ORDER BY e.create_time DESC")
    List<BizEvaluation> selectEvaluationListWithActivity();
}