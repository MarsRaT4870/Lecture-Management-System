-- ----------------------------
-- 1. 活动信息表 (biz_activity) - 核心改造
-- ----------------------------
DROP TABLE IF EXISTS `biz_activity`;
CREATE TABLE `biz_activity`
(
    `activity_id`     bigint       NOT NULL AUTO_INCREMENT COMMENT '活动ID',
    `title`           varchar(200) NOT NULL COMMENT '活动主题',
    `activity_type`   char(1)      NOT NULL DEFAULT '1' COMMENT '活动类型（1学术讲座 2校园活动）',
    `applicant_type`  char(1)      NOT NULL DEFAULT '1' COMMENT '申报主体（1校内部门 2校外机构 3教师个人）',
    `speaker`         varchar(100) NOT NULL COMMENT '主讲人',
    `speaker_title`   varchar(100)          DEFAULT NULL COMMENT '主讲人职称',
    `speaker_affil`   varchar(200)          DEFAULT NULL COMMENT '主讲人所属单位',
    `location`        varchar(200) NOT NULL COMMENT '活动地点',
    `start_time`      datetime     NOT NULL COMMENT '开始时间',
    `end_time`        datetime     NOT NULL COMMENT '结束时间',
    `reg_deadline`    datetime     NOT NULL COMMENT '报名截止时间',
    `target_audience` varchar(100)          DEFAULT NULL COMMENT '面向人群',
    `description`     text COMMENT '内容详情',
    `max_people`      int          NOT NULL DEFAULT 0 COMMENT '最大参与人数（0不限）',
    `status`          char(1)               DEFAULT '0' COMMENT '发布状态（0草稿 1已发布 2已结束 3已取消）',
    `audit_status`    char(1)               DEFAULT '0' COMMENT '审核状态（0待校内审 1待校外审 2已通过 3已驳回）',
    `qr_code_token`   varchar(64)           DEFAULT NULL COMMENT '当前有效签到Token',
    `del_flag`        char(1)               DEFAULT '0' COMMENT '删除标志',
    `create_by`       varchar(64)           DEFAULT '' COMMENT '创建者',
    `create_time`     datetime              DEFAULT NULL COMMENT '创建时间',
    `update_by`       varchar(64)           DEFAULT '' COMMENT '更新者',
    `update_time`     datetime              DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`activity_id`),
    KEY               `idx_activity_time` (`start_time`),
    KEY               `idx_activity_speaker` (`speaker`),
    KEY               `idx_activity_dept` (`applicant_type`)
) ENGINE=InnoDB COMMENT='学术活动发布表';

-- ----------------------------
-- 2. 报名签到反馈表 (biz_registration) - 全流程闭环
-- ----------------------------
DROP TABLE IF EXISTS `biz_registration`;
CREATE TABLE `biz_registration`
(
    `reg_id`        bigint  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id`   bigint  NOT NULL COMMENT '活动ID',
    `user_id`       bigint  NOT NULL COMMENT '用户ID',
    `student_no`    varchar(30)      DEFAULT NULL COMMENT '学号/工号',
    `user_name`     varchar(50)      DEFAULT NULL COMMENT '姓名',
    `dept_name`     varchar(100)     DEFAULT NULL COMMENT '学院/部门',
    `status`        char(1) NOT NULL DEFAULT '0' COMMENT '状态（0已报名 1候补中 2已签到 3已取消 4候补失败 5缺席）',
    `source`        char(1)          DEFAULT '0' COMMENT '来源（0 PC端 1 移动端）',
    `create_time`   datetime         DEFAULT NULL COMMENT '报名时间',
    `checkin_time`  datetime         DEFAULT NULL COMMENT '签到时间',
    `checkin_type`  char(1)          DEFAULT NULL COMMENT '签到方式（1二维码 2人脸 3代签）',
    `is_late`       char(1)          DEFAULT '0' COMMENT '是否迟到（0否 1是）',
    `cancel_time`   datetime         DEFAULT NULL COMMENT '取消时间',
    -- 反馈部分
    `score_content` int              DEFAULT 0 COMMENT '内容质量评分(1-5)',
    `score_speaker` int              DEFAULT 0 COMMENT '主讲人表现评分(1-5)',
    `score_org`     int              DEFAULT 0 COMMENT '组织安排评分(1-5)',
    `feedback`      varchar(500)     DEFAULT NULL COMMENT '文字建议',
    PRIMARY KEY (`reg_id`),
    UNIQUE KEY `uk_activity_user` (`activity_id`,`user_id`),
    KEY             `idx_reg_status` (`status`)
) ENGINE=InnoDB COMMENT='活动报名与反馈表';

-- ----------------------------
-- 3. 通知记录表 (biz_notification) - 自动化通知追踪
-- ----------------------------
DROP TABLE IF EXISTS `biz_notification`;
CREATE TABLE `biz_notification`
(
    `notify_id`   bigint NOT NULL AUTO_INCREMENT,
    `user_id`     bigint NOT NULL COMMENT '接收人',
    `activity_id` bigint       DEFAULT NULL COMMENT '关联活动',
    `title`       varchar(100) DEFAULT NULL COMMENT '标题',
    `content`     varchar(500) DEFAULT NULL COMMENT '内容',
    `type`        char(1)      DEFAULT '1' COMMENT '类型（1报名成功 2活动提醒 3变更预警 4问卷推送）',
    `channel`     char(1)      DEFAULT '1' COMMENT '渠道（1站内信 2短信）',
    `status`      char(1)      DEFAULT '0' COMMENT '状态（0未读 1已读）',
    `send_time`   datetime     DEFAULT NULL COMMENT '发送时间',
    PRIMARY KEY (`notify_id`),
    KEY           `idx_notify_user` (`user_id`,`status`)
) ENGINE=InnoDB COMMENT='系统通知表';