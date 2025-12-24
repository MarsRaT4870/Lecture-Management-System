package com.ruoyi.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.biz.domain.entity.BizRegistration;
import com.ruoyi.biz.service.IBizRegistrationService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 活动报名签到 Controller
 */
@RestController
@RequestMapping("/biz/registration")
@RequiredArgsConstructor
public class BizRegistrationController extends BaseController {

    private final IBizRegistrationService registrationService;

    /**
     * 查询报名记录列表
     */
    @PreAuthorize("@ss.hasPermi('biz:registration:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizRegistration bizRegistration) {
        Page<BizRegistration> page = PageUtils.buildPage();
        LambdaQueryWrapper<BizRegistration> query = new LambdaQueryWrapper<>();

        // 1. 权限控制：如果是学生，只能看自己的；如果是管理员，可看全部
        Long userId = SecurityUtils.getUserId();
        if (!SecurityUtils.isAdmin(userId)) {
            query.eq(BizRegistration::getUserId, userId);
        } else if (bizRegistration.getUserId() != null) {
            // 管理员如果在搜索特定用户
            query.eq(BizRegistration::getUserId, bizRegistration.getUserId());
        }

        // 2. 动态查询条件
        query.eq(bizRegistration.getActivityId() != null, BizRegistration::getActivityId, bizRegistration.getActivityId())
                .eq(bizRegistration.getStatus() != null, BizRegistration::getStatus, bizRegistration.getStatus())
                .orderByDesc(BizRegistration::getCreateTime);

        registrationService.page(page, query);
        return getDataTable(page);
    }

    /**
     * 学生报名接口 (核心业务)
     */
    @Log(title = "活动报名", businessType = BusinessType.INSERT)
    @PostMapping("/apply")
    public AjaxResult apply(@RequestBody BizRegistration bizRegistration) {
        if (bizRegistration.getActivityId() == null) {
            return error("活动ID不能为空");
        }
        // 调用 Service 的 register 方法，返回 String 消息
        String resultMsg = registrationService.register(bizRegistration.getActivityId());
        return success(resultMsg);
    }

    /**
     * 扫码签到接口 (核心业务)
     */
    @Log(title = "活动签到", businessType = BusinessType.UPDATE)
    @PostMapping("/checkin")
    public AjaxResult checkin(@RequestBody BizRegistration bizRegistration) {
        if (bizRegistration.getActivityId() == null) {
            return error("活动ID不能为空");
        }
        // 前端需将二维码内容的 token 放入 remark 字段，或专门加一个 token 字段
        String token = bizRegistration.getRemark();

        boolean success = registrationService.checkIn(
                bizRegistration.getActivityId(),
                token,
                SecurityUtils.getUserId()
        );

        return success ? success("签到成功") : error("签到失败");
    }

    /**
     * 导出列表
     */
    @PreAuthorize("@ss.hasPermi('biz:registration:export')")
    @Log(title = "活动报名数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizRegistration bizRegistration) {
        // 复用 list 的查询逻辑，但不分页
        LambdaQueryWrapper<BizRegistration> query = new LambdaQueryWrapper<>();
        // ... (此处省略重复的查询构建逻辑，建议封装成私有方法 buildQueryWrapper) ...
        query.eq(bizRegistration.getActivityId() != null, BizRegistration::getActivityId, bizRegistration.getActivityId());

        List<BizRegistration> list = registrationService.list(query);
        ExcelUtil<BizRegistration> util = new ExcelUtil<>(BizRegistration.class);
        util.exportExcel(response, list, "活动报名数据");
    }

    /**
     * 获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('biz:registration:query')")
    @GetMapping(value = "/{regId}")
    public AjaxResult getInfo(@PathVariable("regId") Long regId) {
        return success(registrationService.getById(regId));
    }

    /**
     * 管理员手动修改报名状态
     */
    @PreAuthorize("@ss.hasPermi('biz:registration:edit')")
    @Log(title = "修改报名信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizRegistration bizRegistration) {
        return toAjax(registrationService.updateById(bizRegistration));
    }

    /**
     * 删除报名记录
     */
    @PreAuthorize("@ss.hasPermi('biz:registration:remove')")
    @Log(title = "删除报名信息", businessType = BusinessType.DELETE)
    @DeleteMapping(value = "/{regIds}")
    public AjaxResult remove(@PathVariable Long[] regIds) {
        return toAjax(registrationService.removeBatchByIds(Arrays.asList(regIds)));
    }
}