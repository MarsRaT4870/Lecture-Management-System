package com.ruoyi.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.biz.domain.entity.BizCredit;
import com.ruoyi.biz.service.IBizCreditService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学分认证Controller
 */
@RestController
@RequestMapping("/biz/credit")
@RequiredArgsConstructor
public class BizCreditController extends BaseController {

    private final IBizCreditService creditService;

    /**
     * 查询学分记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list(BizCredit bizCredit) {
        Page<BizCredit> page = PageUtils.buildPage();
        LambdaQueryWrapper<BizCredit> query = new LambdaQueryWrapper<>();

        // 权限控制：学生只能看自己的
        Long userId = SecurityUtils.getUserId();
        if (!SecurityUtils.isAdmin(userId)) {
            query.eq(BizCredit::getUserId, userId);
        } else if (bizCredit.getUserId() != null) {
            query.eq(BizCredit::getUserId, bizCredit.getUserId());
        }

        query.eq(bizCredit.getActivityId() != null, BizCredit::getActivityId, bizCredit.getActivityId())
                .eq(bizCredit.getStatus() != null, BizCredit::getStatus, bizCredit.getStatus())
                .orderByDesc(BizCredit::getGrantTime);

        creditService.page(page, query);
        return getDataTable(page);
    }

    /**
     * 获取我的总学分
     */
    @GetMapping("/myTotal")
    public AjaxResult getMyTotalCredits() {
        Long userId = SecurityUtils.getUserId();
        double total = creditService.getUserTotalCredits(userId);
        return success(total);
    }

    /**
     * 获取学分排行榜
     */
    @GetMapping("/ranking")
    public AjaxResult getRanking(@RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> ranking = creditService.getCreditRanking(limit);
        return success(ranking);
    }

    /**
     * 手动发放学分（管理员）
     */
    @PreAuthorize("@ss.hasPermi('biz:credit:grant')")
    @Log(title = "发放学分", businessType = BusinessType.UPDATE)
    @PostMapping("/grant/{activityId}")
    public AjaxResult grantCredits(@PathVariable Long activityId) {
        int count = creditService.autoGrantCredits(activityId);
        return success("成功发放 " + count + " 条学分记录");
    }
}
