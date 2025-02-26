use aitown

#TG 用户
DROP TABLE IF EXISTS `telegram_user`;
CREATE TABLE `telegram_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `chat_id` bigint(20) NOT NULL DEFAULT 0 COMMENT 'TG ID',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT 'TG 名称',
  `source` varchar(64) NOT NULL DEFAULT '' COMMENT '来源渠道',
  `state` tinyint(2) NOT NULL DEFAULT 0 COMMENT '消息状态：0=未发送,1=已发送',
  `create_date` bigint(20) DEFAULT 0 COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '删除时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志:0:未删除，1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_chat_id` (`chat_id`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='TG用户数据';