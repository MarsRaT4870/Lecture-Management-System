package com.ruoyi.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.biz.domain.BizMessage;

public interface IBizMessageService extends IService<BizMessage> {
    // 发送消息工具方法
    void sendMessage(Long userId, String userName, String title, String content, String type);

    // 全部已读
    void readAll(Long userId);
}