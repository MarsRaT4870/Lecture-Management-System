package com.ruoyi.biz.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.biz.domain.BizRegistration;
import com.ruoyi.biz.service.IBizRegistrationService;

/**
 * 活动报名签到Controller
 */
@RestController
@RequestMapping("/biz/registration")
public class BizRegistrationController extends BaseController {
    @Autowired
    private IBizRegistrationService bizRegistrationService;

    /**
     * 查询列表
     */
    @PreAuthorize("@ss.hasPermi('biz:registration:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizRegistration bizRegistration) {
        startPage();
        // 非管理员只能看自己的
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            bizRegistration.setUserId(SecurityUtils.getUserId());
        }
        List<BizRegistration> list = bizRegistrationService.selectBizRegistrationList(bizRegistration);
        return getDataTable(list);
    }

    /**
     * 报名接口
     */
    @Log(title = "活动报名", businessType = BusinessType.INSERT)
    @PostMapping("/apply")
    public AjaxResult apply(@RequestBody BizRegistration bizRegistration) {
        if (bizRegistration.getActivityId() == null) {
            return error("活动ID不能为空");
        }
        return toAjax(bizRegistrationService.registerActivity(bizRegistration.getActivityId()));
    }

    /**
     * 签到接口
     */
    @Log(title = "活动签到", businessType = BusinessType.UPDATE)
    @PostMapping("/checkin")
    public AjaxResult checkin(@RequestBody BizRegistration bizRegistration) {
        if (bizRegistration.getActivityId() == null) {
            return error("活动ID不能为空");
        }
        // 借用 remark 字段传递签到码
        String code = bizRegistration.getRemark();
        bizRegistrationService.checkInActivity(bizRegistration.getActivityId(), code);
        return success("签到成功");
    }

    /**
     * 统计接口
     */
    @GetMapping("/statistics")
    public AjaxResult statistics() {
        return success(bizRegistrationService.getDeptStatistics());
    }

    // --- 导出、详情、新增、修改、删除 ---

    @PreAuthorize("@ss.hasPermi('biz:registration:export')")
    @Log(title = "活动报名签到", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizRegistration bizRegistration) {
        List<BizRegistration> list = bizRegistrationService.selectBizRegistrationList(bizRegistration);
        ExcelUtil<BizRegistration> util = new ExcelUtil<BizRegistration>(BizRegistration.class);
        util.exportExcel(response, list, "活动报名签到数据");
    }

    @PreAuthorize("@ss.hasPermi('biz:registration:query')")
    @GetMapping(value = "/{regId}")
    public AjaxResult getInfo(@PathVariable("regId") Long regId) {
        return success(bizRegistrationService.selectBizRegistrationByRegId(regId));
    }

    @PreAuthorize("@ss.hasPermi('biz:registration:add')")
    @Log(title = "活动报名签到", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizRegistration bizRegistration) {
        return toAjax(bizRegistrationService.insertBizRegistration(bizRegistration));
    }

    @PreAuthorize("@ss.hasPermi('biz:registration:edit')")
    @Log(title = "活动报名签到", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizRegistration bizRegistration) {
        return toAjax(bizRegistrationService.updateBizRegistration(bizRegistration));
    }

    @PreAuthorize("@ss.hasPermi('biz:registration:remove')")
    @Log(title = "活动报名签到", businessType = BusinessType.DELETE)
    @DeleteMapping(value = "/{regIds}")
    public AjaxResult remove(@PathVariable Long[] regIds) {
        return toAjax(bizRegistrationService.deleteBizRegistrationByRegIds(regIds));
    }
}