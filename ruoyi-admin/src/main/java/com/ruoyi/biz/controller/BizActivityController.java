package com.ruoyi.biz.controller; // 或者是 package com.ruoyi.biz.controller; 取决于你的目录

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.biz.domain.BizRegistration;
import com.ruoyi.biz.mapper.BizActivityMapper;
import com.ruoyi.biz.service.IBizRegistrationService;
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


    // 【新增】注入报名服务，为了统计人数
    @Autowired
    private IBizRegistrationService bizRegistrationService;

    // 【新增】注入Mapper，为了查图表
    @Autowired
    private BizActivityMapper bizActivityMapper;


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


    /**
     * 获取首页统计数据
     */
    @GetMapping("/stats")
    public AjaxResult getStats() {
        AjaxResult ajax = AjaxResult.success();

        // 1. 统计活动总数
        List<BizActivity> activities = bizActivityService.selectBizActivityList(new BizActivity());
        ajax.put("activityCount", activities.size());

        // 2. 统计报名数据
        List<BizRegistration> regs = bizRegistrationService.selectBizRegistrationList(new BizRegistration());
        ajax.put("regCount", regs.size());

        // 3. 统计签到人数 (过滤 status = '2' 已签到)
        long checkinCount = regs.stream().filter(r -> "2".equals(r.getStatus())).count();
        ajax.put("checkinCount", checkinCount);

        // 4. 统计待审核/其他 (这里演示过滤 status = '0' 待审核)
        long auditCount = regs.stream().filter(r -> "0".equals(r.getStatus())).count();
        ajax.put("auditCount", auditCount);

        // 5. 图表数据：热门活动 Top 5
        List<Map<String, Object>> chartData = bizActivityMapper.selectActivityStats();

        // 处理一下数据格式，把 title 和 count 分开，方便前端 ECharts 使用
        List<String> chartX = new ArrayList<>();
        List<Long> chartY = new ArrayList<>();
        for (Map<String, Object> map : chartData) {
            chartX.add(map.get("title").toString());
            chartY.add((Long) map.get("count"));
        }
        ajax.put("chartX", chartX);
        ajax.put("chartY", chartY);

        return ajax;
    }



}