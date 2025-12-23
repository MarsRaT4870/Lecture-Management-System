package com.ruoyi.biz.domain.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 签到指令对象 (DTO)
 * 严格控制前端传入参数，拒绝直接用 Entity 接收
 */
@Data
public class CheckInCommand {
    @NotNull(message = "活动ID不能为空")
    private Long activityId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 签到码/Token (二维码解析出来的加密串) */
    private String checkInToken;

    /** 经度 (预留给 GPS 校验) */
    private Double longitude;

    /** 纬度 (预留给 GPS 校验) */
    private Double latitude;
}