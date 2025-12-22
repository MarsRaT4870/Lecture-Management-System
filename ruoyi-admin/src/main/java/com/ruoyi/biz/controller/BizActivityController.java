package com.ruoyi.biz.controller; // 或者是 package com.ruoyi.biz.controller; 取决于你的目录

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
 * 活动管理 Controller
 */
@RestController
// 关键点：这里的路径必须和前端 api.js 里的 url 对应
@RequestMapping("/biz/activity")
public class BizActivityController extends BaseController {
    @Autowired
    private IBizActivityService bizActivityService;

    /**
     * 查询活动列表
     */
    // @PreAuthorize("@ss.hasPermi('biz:activity:list')") // 如果你配置了权限字符，可以开启这个
    @GetMapping("/list")
    public TableDataInfo list(BizActivity bizActivity) {
        startPage();
        List<BizActivity> list = bizActivityService.selectBizActivityList(bizActivity);
        return getDataTable(list);
    }

    /**
     * 获取活动详细信息
     */
    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable("activityId") Long activityId) {
        return AjaxResult.success(bizActivityService.selectBizActivityByActivityId(activityId));
    }

    /**
     * 新增活动
     */
    @Log(title = "活动管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizActivity bizActivity) {
        return toAjax(bizActivityService.insertBizActivity(bizActivity));
    }

    /**
     * 修改活动
     */
    @Log(title = "活动管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizActivity bizActivity) {
        return toAjax(bizActivityService.updateBizActivity(bizActivity));
    }

    /**
     * 删除活动
     */
    @Log(title = "活动管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{activityIds}")
    public AjaxResult remove(@PathVariable Long[] activityIds) {
        return toAjax(bizActivityService.deleteBizActivityByActivityIds(activityIds));
    }
}