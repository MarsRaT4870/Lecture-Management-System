package com.ruoyi.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 场地对象 biz_venue
 */
@Data // 自动生成 Getter, Setter, toString, hashCode, equals
@EqualsAndHashCode(callSuper = true) // 包含父类(BaseEntity)的字段
@TableName("biz_venue") // 必须指定表名
public class BizVenue extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 场地ID (主键)
     */
    @TableId // 必须指定主键
    private Long venueId;

    /**
     * 场地名称
     */
    private String venueName;

    /**
     * 具体位置
     */
    private String location;

    /**
     * 容纳人数
     */
    private Long capacity;

    /**
     * 状态（1正常 0停用）
     */
    private String status;

    /**
     * 设施描述
     */
    private String description;
}