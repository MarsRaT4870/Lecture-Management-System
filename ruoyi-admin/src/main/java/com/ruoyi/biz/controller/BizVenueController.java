package com.ruoyi.biz.controller;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.biz.domain.BizVenue;
import com.ruoyi.biz.service.IBizVenueService;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/biz/venue")
public class BizVenueController extends BaseController {
    @Autowired
    private IBizVenueService venueService;

    /**
     * 查询场地列表
     */
    @PreAuthorize("@ss.hasPermi('biz:venue:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizVenue bizVenue) {
        startPage(); // RuoYi 的分页工具，MP 也兼容
        // 使用 MP 的 LambdaQueryWrapper 构造查询条件
        LambdaQueryWrapper<BizVenue> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotBlank(bizVenue.getVenueName()), BizVenue::getVenueName, bizVenue.getVenueName());
        lqw.eq(StringUtils.isNotBlank(bizVenue.getStatus()), BizVenue::getStatus, bizVenue.getStatus());
        lqw.orderByDesc(BizVenue::getCreateTime); // 按创建时间倒序

        List<BizVenue> list = venueService.list(lqw);
        return getDataTable(list);
    }

    /**
     * 获取所有可用场地 (下拉框用)
     */
    @GetMapping("/listAll")
    public AjaxResult listAll() {
        return success(venueService.list(new LambdaQueryWrapper<BizVenue>().eq(BizVenue::getStatus, "1")));
    }

    /**
     * 获取场地详细信息
     */
    @PreAuthorize("@ss.hasPermi('biz:venue:query')")
    @GetMapping(value = "/{venueId}")
    public AjaxResult getInfo(@PathVariable("venueId") Long venueId) {
        return success(venueService.getById(venueId)); // MP 内置方法
    }

    /**
     * 新增场地
     */
    @PreAuthorize("@ss.hasPermi('biz:venue:add')")
    @Log(title = "场地管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizVenue bizVenue) {
        return toAjax(venueService.save(bizVenue)); // MP 内置方法
    }

    /**
     * 修改场地
     */
    @PreAuthorize("@ss.hasPermi('biz:venue:edit')")
    @Log(title = "场地管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizVenue bizVenue) {
        return toAjax(venueService.updateById(bizVenue)); // MP 内置方法
    }

    /**
     * 删除场地
     */
    @PreAuthorize("@ss.hasPermi('biz:venue:remove')")
    @Log(title = "场地管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{venueIds}")
    public AjaxResult remove(@PathVariable Long[] venueIds) {
        // MP 批量删除需要传入 Collection
        return toAjax(venueService.removeBatchByIds(Arrays.asList(venueIds)));
    }
}