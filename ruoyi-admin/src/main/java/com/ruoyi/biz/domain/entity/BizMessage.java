package com.ruoyi.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("biz_message")
public class BizMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long messageId;

    /**
     * 接收人ID
     */
    private Long userId;

    /**
     * 接收人姓名
     */
    private String userName;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 类型 (1系统 2活动)
     */
    private String type;

    /**
     * 0未读 1已读
     */
    private String readFlag;

    private Date createTime;
}