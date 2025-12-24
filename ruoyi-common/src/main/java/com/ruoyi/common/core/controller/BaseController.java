package com.ruoyi.common.core.controller;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * web层通用数据处理 (双模版)
 */
public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据 (PageHelper)
     */
    protected void startPage() {
        PageUtils.startPage();
    }

    protected void clearPage() {
        PageUtils.clearPage();
    }

    /**
     * 响应请求分页数据 (兼容 PageHelper)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        // 如果是 PageHelper 的 list，它包含 total 信息
        if (list instanceof com.github.pagehelper.Page) {
            rspData.setTotal(((com.github.pagehelper.Page) list).getTotal());
        } else {
            rspData.setTotal(list != null ? list.size() : 0);
        }
        return rspData;
    }

    /**
     * 响应请求分页数据 (MyBatis-Plus)
     */
    protected TableDataInfo getDataTable(IPage<?> page) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setMsg("查询成功");
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    protected TableDataInfo getDataTable(List<?> list, long total) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(total);
        return rspData;
    }

    public AjaxResult success() {
        return AjaxResult.success();
    }

    public AjaxResult error() {
        return AjaxResult.error();
    }

    public AjaxResult success(String message) {
        return AjaxResult.success(message);
    }

    public AjaxResult success(Object data) {
        return AjaxResult.success(data);
    }

    public AjaxResult error(String message) {
        return AjaxResult.error(message);
    }

    public AjaxResult warn(String message) {
        return AjaxResult.warn(message);
    }

    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? success() : error();
    }

    protected AjaxResult toAjax(boolean result) {
        return result ? success() : error();
    }

    public String redirect(String url) {
        return StringUtils.format("redirect:{}", url);
    }

    public LoginUser getLoginUser() {
        return SecurityUtils.getLoginUser();
    }

    public Long getUserId() {
        return getLoginUser().getUserId();
    }

    public Long getDeptId() {
        return getLoginUser().getDeptId();
    }

    public String getUsername() {
        return getLoginUser().getUsername();
    }
}