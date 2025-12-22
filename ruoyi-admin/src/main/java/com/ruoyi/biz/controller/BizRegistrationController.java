package com.ruoyi.biz.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.biz.domain.BizRegistration;
import com.ruoyi.biz.service.IBizRegistrationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报名记录Controller
 * 
 * @author ruoyi
 * @date 2025-12-17
 */
@RestController
@RequestMapping("/activity/register")
public class BizRegistrationController extends BaseController
{
    @Autowired
    private IBizRegistrationService bizRegistrationService;

    /**
     * 查询报名记录列表
     */
    @PreAuthorize("@ss.hasPermi('activity:register:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizRegistration bizRegistration)
    {
        startPage();
        List<BizRegistration> list = bizRegistrationService.selectBizRegistrationList(bizRegistration);
        return getDataTable(list);
    }

    /**
     * 导出报名记录列表
     */
    @PreAuthorize("@ss.hasPermi('activity:register:export')")
    @Log(title = "报名记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizRegistration bizRegistration)
    {
        List<BizRegistration> list = bizRegistrationService.selectBizRegistrationList(bizRegistration);
        ExcelUtil<BizRegistration> util = new ExcelUtil<BizRegistration>(BizRegistration.class);
        util.exportExcel(response, list, "报名记录数据");
    }

    /**
     * 获取报名记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('activity:register:query')")
    @GetMapping(value = "/{regId}")
    public AjaxResult getInfo(@PathVariable("regId") Long regId)
    {
        return success(bizRegistrationService.selectBizRegistrationByRegId(regId));
    }

    /**
     * 新增报名记录
     */
    @PreAuthorize("@ss.hasPermi('activity:register:add')")
    @Log(title = "报名记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizRegistration bizRegistration)
    {
        return toAjax(bizRegistrationService.insertBizRegistration(bizRegistration));
    }

    /**
     * 修改报名记录
     */
    @PreAuthorize("@ss.hasPermi('activity:register:edit')")
    @Log(title = "报名记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizRegistration bizRegistration)
    {
        return toAjax(bizRegistrationService.updateBizRegistration(bizRegistration));
    }

    /**
     * 删除报名记录
     */
    @PreAuthorize("@ss.hasPermi('activity:register:remove')")
    @Log(title = "报名记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{regIds}")
    public AjaxResult remove(@PathVariable Long[] regIds)
    {
        return toAjax(bizRegistrationService.deleteBizRegistrationByRegIds(regIds));
    }
}
