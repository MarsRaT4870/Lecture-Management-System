package com.ruoyi.biz.controller;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.biz.domain.entity.BizCredit;
import com.ruoyi.biz.service.IBizCreditService;

@RestController
@RequestMapping("/biz/credit")
public class BizCreditController extends BaseController
{
    @Autowired
    private IBizCreditService creditService;

    /**
     * 查询我的学分列表
     */
    @GetMapping("/list")
    public TableDataInfo list(BizCredit bizCredit)
    {
        startPage();
        LambdaQueryWrapper<BizCredit> lqw = new LambdaQueryWrapper<>();

        // 如果不是管理员，只能看自己的
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            lqw.eq(BizCredit::getUserId, SecurityUtils.getUserId());
        } else {
            // 管理员可以按名字搜
            if (bizCredit.getUserName() != null) {
                lqw.like(BizCredit::getUserName, bizCredit.getUserName());
            }
        }
        lqw.orderByDesc(BizCredit::getGrantTime);

        List<BizCredit> list = creditService.list(lqw);
        return getDataTable(list);
    }

    /**
     * 一键生成学分 (管理员操作)
     */
    @PreAuthorize("@ss.hasPermi('biz:credit:add')")
    @Log(title = "学分发放", businessType = BusinessType.INSERT)
    @PostMapping("/generate/{activityId}")
    public AjaxResult generate(@PathVariable Long activityId)
    {
        creditService.generateCredit(activityId);
        return success("学分发放成功");
    }
}