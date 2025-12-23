package com.ruoyi.biz.controller; // ★注意：请确认这里的包名和你实际文件夹路径一致

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
 * 报名管理 Controller
 */
@RestController
@RequestMapping("/biz/registration")
public class BizRegistrationController extends BaseController
{
    @Autowired
    private IBizRegistrationService bizRegistrationService;

    /**
     * 查询报名记录列表
     */
    // @PreAuthorize("@ss.hasPermi('biz:registration:list')")
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
    // @PreAuthorize("@ss.hasPermi('biz:registration:export')")
    @Log(title = "报名管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizRegistration bizRegistration)
    {
        List<BizRegistration> list = bizRegistrationService.selectBizRegistrationList(bizRegistration);
        ExcelUtil<BizRegistration> util = new ExcelUtil<BizRegistration>(BizRegistration.class);
        util.exportExcel(response, list, "报名数据");
    }

    /**
     * 获取报名详细信息
     * (对应前端 getRegistration API)
     */
    // @PreAuthorize("@ss.hasPermi('biz:registration:query')")
    @GetMapping(value = "/{regId}")
    public AjaxResult getInfo(@PathVariable("regId") Long regId)
    {
        return AjaxResult.success(bizRegistrationService.selectBizRegistrationById(regId));
    }

    /**
     * 新增报名 (核心功能：学生点击报名时调用)
     * (对应前端 addRegistration API)
     */
    // @PreAuthorize("@ss.hasPermi('biz:registration:add')")
    @Log(title = "报名管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizRegistration bizRegistration)
    {
        // 这里会调用我们在 Service 写的“防重复、防爆满”逻辑
        return toAjax(bizRegistrationService.insertBizRegistration(bizRegistration));
    }

    /**
     * 删除报名
     * (对应前端 delRegistration API)
     */
    // @PreAuthorize("@ss.hasPermi('biz:registration:remove')")
    @Log(title = "报名管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{regIds}")
    public AjaxResult remove(@PathVariable Long[] regIds)
    {
        return toAjax(bizRegistrationService.deleteBizRegistrationByRegIds(regIds));
    }
}