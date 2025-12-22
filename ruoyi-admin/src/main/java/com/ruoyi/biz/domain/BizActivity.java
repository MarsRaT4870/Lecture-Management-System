package com.ruoyi.biz.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 学术讲座活动对象 biz_activity
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true) // 包含父类字段
public class BizActivity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 活动ID */
    private Long activityId;

    /** 活动主题 */
    @Excel(name = "活动主题")
    @NotBlank(message = "活动主题不能为空") // 现代化的校验方式
    private String title;

    /** 主讲人 */
    @Excel(name = "主讲人")
    @NotBlank(message = "主讲人不能为空")
    private String speaker;

    /** 地点 */
    @Excel(name = "地点")
    @NotBlank(message = "活动地点不能为空")
    private String location;

    /** 开始时间 */
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "开始时间不能为空")
    private Date startTime;

    /** 结束时间 */
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 内容详情 */
    @Excel(name = "内容详情")
    private String description;

    /** 状态（0正常 1关闭） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=关闭")
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    // --- 以下是你新增的高级字段 ---

    /** 最大人数限制（0为不限） */
    @Excel(name = "最大人数")
    @Min(value = 0, message = "人数限制不能小于0")
    private Integer maxPeople;

    /** 签到验证码 */
    private String checkinCode;

    /** 审核状态（0待审核 1已通过 2拒绝） */
    @Excel(name = "审核状态", readConverterExp = "0=待审核,1=已通过,2=拒绝")
    private String auditStatus;
}