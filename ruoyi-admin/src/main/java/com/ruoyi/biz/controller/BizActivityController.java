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
import com.ruoyi.biz.domain.BizActivity;
import com.ruoyi.biz.service.IBizActivityService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 学术讲座Controller
 * 
 * @author ruoyi
 * @date 2025-12-17
 */
@RestController
@RequestMapping("/activity/lecture")
public class BizActivityController extends BaseController
{
    @Autowired
    private IBizActivityService bizActivityService;

    /**
     * 查询学术讲座列表
     */
    @PreAuthorize("@ss.hasPermi('activity:lecture:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizActivity bizActivity)
    {
        startPage();
        List<BizActivity> list = bizActivityService.selectBizActivityList(bizActivity);
        return getDataTable(list);
    }

    /**
     * 导出学术讲座列表
     */
    @PreAuthorize("@ss.hasPermi('activity:lecture:export')")
    @Log(title = "学术讲座", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizActivity bizActivity)
    {
        List<BizActivity> list = bizActivityService.selectBizActivityList(bizActivity);
        ExcelUtil<BizActivity> util = new ExcelUtil<BizActivity>(BizActivity.class);
        util.exportExcel(response, list, "学术讲座数据");
    }

    /**
     * 获取学术讲座详细信息
     */
    @PreAuthorize("@ss.hasPermi('activity:lecture:query')")
    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable("activityId") Long activityId)
    {
        return success(bizActivityService.selectBizActivityByActivityId(activityId));
    }

    /**
     * 新增学术讲座
     */
    @PreAuthorize("@ss.hasPermi('activity:lecture:add')")
    @Log(title = "学术讲座", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizActivity bizActivity)
    {
        return toAjax(bizActivityService.insertBizActivity(bizActivity));
    }

    /**
     * 修改学术讲座
     */
    @PreAuthorize("@ss.hasPermi('activity:lecture:edit')")
    @Log(title = "学术讲座", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizActivity bizActivity)
    {
        return toAjax(bizActivityService.updateBizActivity(bizActivity));
    }

    /**
     * 删除学术讲座
     */
    @PreAuthorize("@ss.hasPermi('activity:lecture:remove')")
    @Log(title = "学术讲座", businessType = BusinessType.DELETE)
	@DeleteMapping("/{activityIds}")
    public AjaxResult remove(@PathVariable Long[] activityIds)
    {
        return toAjax(bizActivityService.deleteBizActivityByActivityIds(activityIds));
    }
}
