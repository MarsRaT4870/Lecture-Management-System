package com.ruoyi.biz.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.biz.domain.BizEvaluation;
import com.ruoyi.biz.service.IBizEvaluationService;

@RestController
@RequestMapping("/biz/evaluation")
public class BizEvaluationController extends BaseController {
    @Autowired
    private IBizEvaluationService evaluationService;

    /**
     * 查询评价列表
     */
    @PreAuthorize("@ss.hasPermi('biz:evaluation:list')")
    @GetMapping("/list")
    public TableDataInfo list() {
        startPage();
        List<BizEvaluation> list = evaluationService.selectList();
        return getDataTable(list);
    }

    /**
     * 新增评价 (学生端提交)
     */
    @Log(title = "活动评价", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizEvaluation evaluation) {
        evaluation.setUserId(SecurityUtils.getUserId());
        evaluation.setUserName(SecurityUtils.getUsername());
        evaluation.setCreateTime(new Date());
        return toAjax(evaluationService.save(evaluation));
    }

    /**
     * 删除评价 (管理员管理)
     */
    @PreAuthorize("@ss.hasPermi('biz:evaluation:remove')")
    @Log(title = "活动评价", businessType = BusinessType.DELETE)
    @DeleteMapping("/{evalIds}")
    public AjaxResult remove(@PathVariable Long[] evalIds) {
        return toAjax(evaluationService.removeBatchByIds(Arrays.asList(evalIds)));
    }
}