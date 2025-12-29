package com.ruoyi.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动归档对象 biz_activity_archive
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_activity_archive")
public class BizActivityArchive extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 归档ID */
    @TableId(value = "archive_id", type = IdType.AUTO)
    private Long archiveId;

    /** 活动ID */
    private Long activityId;

    /** PPT下载链接 */
    private String pptUrl;

    /** 视频回放链接 */
    private String videoUrl;

    /** 精彩瞬间照片（JSON数组） */
    private String photoUrls;

    /** 活动总结 */
    private String summary;

    /** 下载次数 */
    private Integer downloadCount;

    /** 观看次数 */
    private Integer viewCount;
}

