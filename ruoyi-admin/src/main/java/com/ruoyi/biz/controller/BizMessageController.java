package com.ruoyi.biz.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.biz.domain.BizMessage;
import com.ruoyi.biz.service.IBizMessageService;

@RestController
@RequestMapping("/biz/message")
public class BizMessageController extends BaseController {

    @Autowired
    private IBizMessageService messageService;

    /**
     * 查询我的消息列表
     */
    @GetMapping("/list")
    public TableDataInfo list(BizMessage bizMessage) {
        startPage();
        LambdaQueryWrapper<BizMessage> lqw = new LambdaQueryWrapper<>();
        // 只能看自己的
        lqw.eq(BizMessage::getUserId, SecurityUtils.getUserId());
        // 如果前端传了状态筛选
        if (bizMessage.getReadFlag() != null) {
            lqw.eq(BizMessage::getReadFlag, bizMessage.getReadFlag());
        }
        lqw.orderByDesc(BizMessage::getCreateTime);
        return getDataTable(messageService.list(lqw));
    }

    /**
     * 获取未读数量 (用于前端红点)
     */
    @GetMapping("/unreadCount")
    public AjaxResult getUnreadCount() {
        long count = messageService.count(new LambdaQueryWrapper<BizMessage>()
                .eq(BizMessage::getUserId, SecurityUtils.getUserId())
                .eq(BizMessage::getReadFlag, "0"));
        return success(count);
    }

    /**
     * 标记单条已读
     */
    @PutMapping("/read/{messageId}")
    public AjaxResult read(@PathVariable Long messageId) {
        BizMessage msg = new BizMessage();
        msg.setMessageId(messageId);
        msg.setReadFlag("1");
        return toAjax(messageService.updateById(msg));
    }

    /**
     * 全部已读
     */
    @PutMapping("/readAll")
    public AjaxResult readAll() {
        messageService.readAll(SecurityUtils.getUserId());
        return success();
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageIds}")
    public AjaxResult remove(@PathVariable Long[] messageIds) {
        return toAjax(messageService.removeBatchByIds(java.util.Arrays.asList(messageIds)));
    }
}