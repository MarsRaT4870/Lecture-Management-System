package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.BizMessage;
import com.ruoyi.biz.mapper.BizMessageMapper;
import com.ruoyi.biz.service.IBizMessageService;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class BizMessageServiceImpl extends ServiceImpl<BizMessageMapper, BizMessage> implements IBizMessageService {

    @Override
    public void sendMessage(Long userId, String userName, String title, String content, String type) {
        BizMessage msg = new BizMessage();
        msg.setUserId(userId);
        msg.setUserName(userName);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setType(type);
        msg.setReadFlag("0"); // 默认为未读
        msg.setCreateTime(new Date());
        this.save(msg);
    }

    @Override
    public void readAll(Long userId) {
        LambdaUpdateWrapper<BizMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BizMessage::getUserId, userId)
                .set(BizMessage::getReadFlag, "1");
        this.update(updateWrapper);
    }
}