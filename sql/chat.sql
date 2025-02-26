use cat;

#聊天信息
DROP TABLE IF EXISTS `chat`;
CREATE TABLE `chat` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '聊天消息ID',
  `chat_id` varchar(50) NOT NULL DEFAULT '' COMMENT '聊天ID=聊天者ID（小）+聊天者ID（大）+ 类型（暂定：1:私聊 2:群聊，3聊天室 4客服 5系统）',
  `sender_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '发送者ID',
  `target_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '接收者ID',
  `msg_type` smallint(10) NOT NULL DEFAULT 0 COMMENT '消息类型1文字,2图片,3emoji,4音频,5文件',
  `content` mediumtext NOT NULL COMMENT '消息内容',
  `barrage` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否弹幕:0:否，1：是',
  `sname` VARCHAR ( 64 ) NOT NULL DEFAULT '' COMMENT '发送则名称',
  `tname` VARCHAR ( 64 ) NOT NULL DEFAULT '' COMMENT '接受者名称',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `deleted_time` bigint(20) DEFAULT NULL COMMENT '删除时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志:0:未删除，1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_chat_id` (`chat_id`) USING BTREE,
  KEY `idx_sender_id` (`sender_id`) USING BTREE,
  KEY `idx_target_id` (`target_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='聊天记录表';

