package com.ruoyi.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.biz.domain.dto.ActivityStatsDTO;
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
                .eq(StringUtils.isNotBlank(activity.getActivityType()), BizActivity::getActivityType,
                        activity.getActivityType())
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
        if (notices.size() > 5)
            notices = notices.subList(0, 5);
        data.put("notices", notices);

        return success(data);
    }

    /**
     * 获取数据大屏统计信息
     */
    @GetMapping("/statistics")
    public AjaxResult getStatistics() {
        Map<String, Object> data = new HashMap<>();

        // 1. 学科活跃度排名（按活动类型统计）
        List<ActivityStatsDTO> subjectRank = activityService.getSubjectRank();
        List<Integer> rankData = new ArrayList<>();
        List<String> rankCategories = new ArrayList<>();
        for (ActivityStatsDTO dto : subjectRank) {
            rankData.add(dto.getJoinCount());
            rankCategories.add(dto.getSubjectName());
        }
        data.put("rankData", rankData);
        data.put("rankCategories", rankCategories);

        // 2. 活动类型占比（环形图数据）
        List<Map<String, Object>> pieData = new ArrayList<>();
        long lectureCount = activityService.count(new LambdaQueryWrapper<BizActivity>()
                .eq(BizActivity::getActivityType, "1")
                .eq(BizActivity::getStatus, "1"));
        long activityCount = activityService.count(new LambdaQueryWrapper<BizActivity>()
                .eq(BizActivity::getActivityType, "2")
                .eq(BizActivity::getStatus, "1"));

        Map<String, Object> item1 = new HashMap<>();
        item1.put("value", lectureCount);
        item1.put("name", "学术讲座");
        pieData.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("value", activityCount);
        item2.put("name", "校园活动");
        pieData.add(item2);

        data.put("pieData", pieData);

        // 3. 活动热度趋势（最近7天）
        List<Map<String, Object>> trendData = new ArrayList<>();
        Date now = new Date();
        for (int i = 6; i >= 0; i--) {
            Date date = DateUtils.addDays(now, -i);
            Date nextDate = DateUtils.addDays(date, 1);
            long count = activityService.count(new LambdaQueryWrapper<BizActivity>()
                    .ge(BizActivity::getCreateTime, date)
                    .lt(BizActivity::getCreateTime, nextDate));
            Map<String, Object> trendItem = new HashMap<>();
            trendItem.put("date", DateUtils.parseDateToStr("MM-dd", date));
            trendItem.put("count", count);
            trendData.add(trendItem);
        }
        data.put("trendData", trendData);

        // 4. 报名统计
        long totalReg = registrationService.count();
        long checkedIn = registrationService.count(new LambdaQueryWrapper<BizRegistration>()
                .eq(BizRegistration::getStatus, "2"));
        long waitlist = registrationService.count(new LambdaQueryWrapper<BizRegistration>()
                .eq(BizRegistration::getStatus, "1"));
        data.put("totalRegistrations", totalReg);
        data.put("totalCheckedIn", checkedIn);
        data.put("waitlistCount", waitlist);
        data.put("checkinRate", totalReg > 0 ? String.format("%.1f", (double) checkedIn / totalReg * 100) : "0.0");

        // 5. 活动状态分布
        long draftCount = activityService.count(new LambdaQueryWrapper<BizActivity>()
                .eq(BizActivity::getStatus, "0"));
        long publishedCount = activityService.count(new LambdaQueryWrapper<BizActivity>()
                .eq(BizActivity::getStatus, "1"));
        long finishedCount = activityService.count(new LambdaQueryWrapper<BizActivity>()
                .eq(BizActivity::getStatus, "2"));
        Map<String, Object> statusData = new HashMap<>();
        statusData.put("draft", draftCount);
        statusData.put("published", publishedCount);
        statusData.put("finished", finishedCount);
        data.put("statusDistribution", statusData);

        // 6. 热门活动Top5（按报名人数）
        List<BizActivity> topActivities = activityService.list(new LambdaQueryWrapper<BizActivity>()
                .eq(BizActivity::getStatus, "1")
                .orderByDesc(BizActivity::getCreateTime)
                .last("LIMIT 5"));

        List<Map<String, Object>> topList = new ArrayList<>();
        for (BizActivity act : topActivities) {
            long regCount = registrationService.count(new LambdaQueryWrapper<BizRegistration>()
                    .eq(BizRegistration::getActivityId, act.getActivityId()));
            Map<String, Object> item = new HashMap<>();
            item.put("title", act.getTitle());
            item.put("regCount", regCount);
            item.put("startTime", act.getStartTime());
            topList.add(item);
        }
        data.put("topActivities", topList);

        // 7. 签到方式统计
        long qrCheckin = registrationService.count(new LambdaQueryWrapper<BizRegistration>()
                .eq(BizRegistration::getCheckinType, "1"));
        long faceCheckin = registrationService.count(new LambdaQueryWrapper<BizRegistration>()
                .eq(BizRegistration::getCheckinType, "2"));
        Map<String, Object> checkinTypeData = new HashMap<>();
        checkinTypeData.put("qrCode", qrCheckin);
        checkinTypeData.put("face", faceCheckin);
        data.put("checkinTypeStats", checkinTypeData);

        // 8. 迟到统计
        long lateCount = registrationService.count(new LambdaQueryWrapper<BizRegistration>()
                .eq(BizRegistration::getIsLate, "1"));
        data.put("lateCount", lateCount);
        data.put("punctualRate",
                checkedIn > 0 ? String.format("%.1f", (double) (checkedIn - lateCount) / checkedIn * 100) : "0.0");

        return success(data);
    }

    /**
     * 获取参与热力图数据（按时间段统计参与人数）
     */
    @GetMapping("/heatmap")
    public AjaxResult getHeatmapData(@RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> data = new HashMap<>();

        // 如果没有指定日期范围，默认查询最近30天
        Date end = endDate != null ? DateUtils.parseDate(endDate) : new Date();
        Date start = startDate != null ? DateUtils.parseDate(startDate) : DateUtils.addDays(end, -30);

        // 按日期统计每天的参与人数（报名+签到）
        List<Map<String, Object>> heatmapData = new ArrayList<>();

        // 查询指定时间范围内的活动
        List<BizActivity> activities = activityService.list(new LambdaQueryWrapper<BizActivity>()
                .ge(BizActivity::getStartTime, start)
                .le(BizActivity::getStartTime, end)
                .eq(BizActivity::getStatus, "1"));

        // 按日期分组统计
        Map<String, Integer> dateCountMap = new HashMap<>();
        for (BizActivity activity : activities) {
            String dateKey = DateUtils.parseDateToStr("yyyy-MM-dd", activity.getStartTime());
            // 统计该活动的报名人数
            long count = registrationService.count(new LambdaQueryWrapper<BizRegistration>()
                    .eq(BizRegistration::getActivityId, activity.getActivityId())
                    .in(BizRegistration::getStatus, "0", "2"));
            dateCountMap.put(dateKey, dateCountMap.getOrDefault(dateKey, 0) + (int) count);
        }

        // 转换为前端需要的格式
        for (Map.Entry<String, Integer> entry : dateCountMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", entry.getKey());
            item.put("value", entry.getValue());
            heatmapData.add(item);
        }

        data.put("heatmapData", heatmapData);
        data.put("startDate", DateUtils.parseDateToStr("yyyy-MM-dd", start));
        data.put("endDate", DateUtils.parseDateToStr("yyyy-MM-dd", end));

        return success(data);
    }

    /**
     * 获取反馈趋势统计（按时间段统计评价数量和平均分）
     */
    @GetMapping("/feedback/trend")
    public AjaxResult getFeedbackTrend(@RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> data = new HashMap<>();

        Date end = endDate != null ? DateUtils.parseDate(endDate) : new Date();
        Date start = startDate != null ? DateUtils.parseDate(startDate) : DateUtils.addDays(end, -30);

        // 查询指定时间范围内已结束的活动
        List<BizActivity> activities = activityService.list(new LambdaQueryWrapper<BizActivity>()
                .ge(BizActivity::getEndTime, start)
                .le(BizActivity::getEndTime, end)
                .eq(BizActivity::getStatus, "2")); // 已结束

        // 按周统计
        Map<String, List<Double>> weekScores = new HashMap<>();
        Map<String, Integer> weekCounts = new HashMap<>();

        for (BizActivity activity : activities) {
            // 获取该活动的所有评价
            List<BizRegistration> registrations = registrationService.list(
                    new LambdaQueryWrapper<BizRegistration>()
                            .eq(BizRegistration::getActivityId, activity.getActivityId())
                            .eq(BizRegistration::getStatus, "2") // 已签到
                            .isNotNull(BizRegistration::getScoreContent));

            if (registrations.isEmpty())
                continue;

            // 计算平均分
            double avgScore = registrations.stream()
                    .filter(r -> r.getScoreContent() != null)
                    .mapToInt(BizRegistration::getScoreContent)
                    .average()
                    .orElse(0.0);

            // 获取周数（简化处理，按结束日期所在周）
            String weekKey = getWeekKey(activity.getEndTime());
            weekScores.computeIfAbsent(weekKey, k -> new ArrayList<>()).add(avgScore);
            weekCounts.put(weekKey, weekCounts.getOrDefault(weekKey, 0) + registrations.size());
        }

        // 转换为趋势数据
        List<Map<String, Object>> trendData = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : weekScores.entrySet()) {
            String week = entry.getKey();
            List<Double> scores = entry.getValue();
            double avgScore = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

            Map<String, Object> item = new HashMap<>();
            item.put("week", week);
            item.put("avgScore", Math.round(avgScore * 100.0) / 100.0);
            item.put("feedbackCount", weekCounts.getOrDefault(week, 0));
            trendData.add(item);
        }

        // 按周排序
        trendData.sort((a, b) -> ((String) a.get("week")).compareTo((String) b.get("week")));

        data.put("trendData", trendData);
        return success(data);
    }

    /**
     * 获取周标识（格式：2024-W01）
     */
    private String getWeekKey(Date date) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(java.util.Calendar.YEAR);
        int week = cal.get(java.util.Calendar.WEEK_OF_YEAR);
        return String.format("%d-W%02d", year, week);
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
                activity.getRemark()));
    }

    /**
     * 手动下架活动（归档）
     */
    @PreAuthorize("@ss.hasPermi('biz:activity:remove')") // 复用删除权限，或者新增 biz:activity:archive
    @Log(title = "活动下架", businessType = BusinessType.UPDATE)
    @PutMapping("/archive/{activityId}")
    public AjaxResult archive(@PathVariable Long activityId) {
        // 既然是在 ServiceImpl 里新加的方法，这里需要强转或者在接口加定义
        // 为了方便，我们在 ServiceImpl 里写了 archiveActivity，这里直接调用
        // 注意：你需要确保 BizActivityServiceImpl 被注入为 bean
        // 建议在 IBizActivityService 接口中也加上 boolean archiveActivity(Long id);
        // 这里假设你已经加了，或者直接转换：
        return toAjax(
                ((com.ruoyi.biz.service.impl.BizActivityServiceImpl) activityService).archiveActivity(activityId));
    }

}