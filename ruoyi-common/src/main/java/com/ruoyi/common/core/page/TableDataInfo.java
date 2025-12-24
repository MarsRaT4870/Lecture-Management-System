package com.ruoyi.common.core.page;

import java.io.Serializable;
import java.util.List;

import com.github.pagehelper.PageInfo;
import com.ruoyi.common.constant.HttpStatus;

/**
 * 表格分页数据对象 (兼容版)
 */
public class TableDataInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private long total;
    private List<?> rows;
    private int code;
    private String msg;

    public TableDataInfo() {
    }

    /**
     * 分页 (兼容 PageHelper List)
     */
    public TableDataInfo(List<?> list, int total) {
        this.rows = list;
        this.total = total;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}