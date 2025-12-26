package com.ruoyi.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.biz.domain.entity.BizActivity;
import com.ruoyi.biz.domain.entity.BizRegistration;
import com.ruoyi.biz.service.IBizActivityService;
import com.ruoyi.biz.service.IBizRegistrationService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.service.ISysNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 活动管理 Controller
 */
@RestController
@RequestMapping("/biz/activity")
@RequiredArgsConstructor
public class BizActivityController extends BaseController {

    private final IBizActivityService activityService;
    private final IBizRegistrationService registrationService; // 新增注入
    private final ISysNoticeService noticeService; // 新增注入



    /**
     * 查询全校活动资源池
     */
    @GetMapping("/list")
    public TableDataInfo list(BizActivity activity) {
        // 1. 构建分页对象 (自动获取前端传递的 pageNum, pageSize)
        Page<BizActivity> page = PageUtils.buildPage();

        // 2. 构建查询条件
        LambdaQueryWrapper<BizActivity> query = new LambdaQueryWrapper<>();
        query.like(StringUtils.isNotBlank(activity.getTitle()), BizActivity::getTitle, activity.getTitle())
                .eq(StringUtils.isNotBlank(activity.getActivityType()), BizActivity::getActivityType, activity.getActivityType())
                // 如果有时间筛选
                .ge(activity.getStartTime() != null, BizActivity::getStartTime, activity.getStartTime())
                // 默认排序：创建时间倒序
                .orderByDesc(BizActivity::getCreateTime);

        // 3. 执行查询
        activityService.page(page, query);

        // 4. 返回结果 (适配我们修改后的 getDataTable)
        return getDataTable(page);
    }

    /**
     * 发布活动
     */
    @PreAuthorize("@ss.hasPermi('biz:activity:add')")
    @PostMapping
    public AjaxResult add(@RequestBody BizActivity activity) {
        activity.setCreateBy(getUsername());
        // 调用之前 Service 实现的 submitActivity
        return toAjax(activityService.submitActivity(activity));
    }

    /**
     * 修改活动
     */
    @PreAuthorize("@ss.hasPermi('biz:activity:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody BizActivity activity) {
        return toAjax(activityService.updateById(activity));
    }

    /**
     * 删除活动
     */
    @PreAuthorize("@ss.hasPermi('biz:activity:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(activityService.removeBatchByIds(Arrays.asList(ids)));
    }

    /**
     * 获取详情
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(activityService.getById(id));
    }

    /**
     * 生成签到二维码 (管理员/主讲人)
     */
    @GetMapping("/qrcode/{id}")
    public AjaxResult getQrCode(@PathVariable Long id) {
        return AjaxResult.success("生成成功", activityService.generateCheckinCode(id));
    }


    /**
     * [新增] 获取首页综合数据（核心指标 + 最新公告 + 图表数据）
     */
    @GetMapping("/index-data")
    public AjaxResult getIndexData() {
        Map<String, Object> data = new HashMap<>();
        Long userId = SecurityUtils.getUserId();

        // 1. 核心指标统计
        // (1) 全校活动总数 (已发布)
        long totalActivity = activityService.count(new LambdaQueryWrapper<BizActivity>()
                .eq(BizActivity::getStatus, "1"));
        data.put("totalActivity", totalActivity);

        // (2) 本周即将开始 (未来7天)
        Date now = new Date();
        Date nextWeek = DateUtils.addDays(now, 7);
        long weekActivity = activityService.count(new LambdaQueryWrapper<BizActivity>()
                .eq(BizActivity::getStatus, "1")
                .between(BizActivity::getStartTime, now, nextWeek));
        data.put("weekActivity", weekActivity);

        // (3) 累计参与人次 (所有报名成功的)
        long totalPeople = registrationService.count(new LambdaQueryWrapper<BizRegistration>()
                .in(BizRegistration::getStatus, "0", "2")); // 已报名或已签到
        data.put("totalPeople", totalPeople);

        // (4) 我的报名记录
        long myReg = registrationService.count(new LambdaQueryWrapper<BizRegistration>()
                .eq(BizRegistration::getUserId, userId));
        data.put("myReg", myReg);

        // 2. 最新公告 (取前5条)
        SysNotice noticeQuery = new SysNotice();
        noticeQuery.setStatus("0"); // 正常状态
        PageUtils.startPage(); // 开启分页
        List<SysNotice> notices = noticeService.selectNoticeList(noticeQuery);
        // PageHelper会自动拦截第一条查询，所以这里手动清除可能的副作用并只取前5
        if (notices.size() > 5) notices = notices.subList(0, 5);
        data.put("notices", notices);

        return success(data);
    }


    /**
     * 获取数据大屏统计信息
     */
    @GetMapping("/statistics")
    public AjaxResult getStatistics() {
        // 这里暂时返回模拟数据，确保前端图表能展示
        // 后续可以在 Service 层实现真实的数据库聚合查询
        Map<String, Object> data = new HashMap<>();

        // 模拟学科排名数据
        data.put("rankData", Arrays.asList(120, 200, 150, 80, 70));
        data.put("rankCategories", Arrays.asList("计算机", "经管", "人文", "建筑", "艺术"));

        // 模拟活动类型占比
        List<Map<String, Object>> pieData = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("value", 1048);
        item1.put("name", "学术讲座");
        Map<String, Object> item2 = new HashMap<>();
        item2.put("value", 735);
        item2.put("name", "校园活动");
        Map<String, Object> item3 = new HashMap<>();
        item3.put("value", 580);
        item3.put("name", "社团竞赛");
        pieData.add(item1);
        pieData.add(item2);
        pieData.add(item3);
        data.put("pieData", pieData);

        return success(data);
    }


    /**
     * 审核活动
     */
    @PreAuthorize("@ss.hasPermi('biz:activity:audit')") // 只有拥有审核权限的人能调
    @Log(title = "活动审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody BizActivity activity) {
        // 前端传过来: activityId, auditStatus, remark(审核意见)
        if (activity.getActivityId() == null || activity.getAuditStatus() == null) {
            return error("参数不完整");
        }
        return toAjax(activityService.auditActivity(
                activity.getActivityId(),
                activity.getAuditStatus(),
                activity.getRemark()
        ));
    }


}