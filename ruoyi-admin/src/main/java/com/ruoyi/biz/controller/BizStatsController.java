package com.ruoyi.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.biz.domain.entity.BizActivity;
import com.ruoyi.biz.domain.entity.BizRegistration;
import com.ruoyi.biz.domain.entity.BizVenue;
import com.ruoyi.biz.service.IBizActivityService;
import com.ruoyi.biz.service.IBizRegistrationService;
import com.ruoyi.biz.service.IBizVenueService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计分析 Controller
 */
@RestController
@RequestMapping("/biz/stats")
public class BizStatsController extends BaseController {

    @Autowired
    private IBizActivityService activityService;
    @Autowired
    private IBizRegistrationService registrationService;
    @Autowired
    private IBizVenueService venueService;

    /**
     * 获取首页统计数据
     */
    @GetMapping("/dashboard")
    public AjaxResult getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // 1. 核心指标卡片
        data.put("activityCount", activityService.count()); // 总活动数
        data.put("venueCount", venueService.count());       // 场地数

        // 统计总报名人数 (status=0,1,2 都算)
        long totalReg = registrationService.count();
        data.put("totalParticipants", totalReg);

        // 统计签到率 (已签到 / 总报名)
        long checkedIn = registrationService.count(new QueryWrapper<BizRegistration>().eq("status", "2"));
        double rate = totalReg == 0 ? 0 : (double) checkedIn / totalReg * 100;
        data.put("checkinRate", String.format("%.1f", rate));

        // 2. 图表数据：活动类型分布 (模拟聚合，实际可用 group by sql)
        List<Map<String, Object>> typeData = new ArrayList<>();
        typeData.add(Map.of("name", "学术讲座", "value", activityService.count(new QueryWrapper<BizActivity>().eq("activity_type", "1"))));
        typeData.add(Map.of("name", "校园活动", "value", activityService.count(new QueryWrapper<BizActivity>().eq("activity_type", "2"))));
        data.put("typeData", typeData);

        // 3. 图表数据：热门活动 Top5 (按报名人数降序)
        // 这里为了演示简单，直接取前5个活动，实际应写 SQL: select activity_name, count(*) from reg group by activity_id
        List<BizActivity> topActivity = activityService.list(new QueryWrapper<BizActivity>()
                .orderByDesc("create_time").last("limit 5"));

        List<String> actNames = new ArrayList<>();
        List<Integer> actCounts = new ArrayList<>();
        for (BizActivity act : topActivity) {
            actNames.add(act.getTitle());
            // 模拟数据：实际开发中请调用 Mapper 的 count 方法
            actCounts.add((int) (Math.random() * 100 + 20));
        }
        data.put("rankNames", actNames);
        data.put("rankValues", actCounts);

        return success(data);
    }
}