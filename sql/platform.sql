use aitown;

#玩家信息
DROP TABLE IF EXISTS `player`;
CREATE TABLE `player` (
	`id` BIGINT ( 20 ) NOT NULL COMMENT '玩家ID',
	`name` VARCHAR ( 700 ) NOT NULL DEFAULT '' COMMENT '玩家登录账号',
	`nickname` VARCHAR ( 700 ) NOT NULL DEFAULT '' COMMENT '玩家昵称',
	`pwd` VARCHAR ( 64 ) NOT NULL DEFAULT '' COMMENT '登录密码',
	`userno` VARCHAR ( 64 ) NOT NULL DEFAULT '' COMMENT '玩家ID',
	#`avatar` VARCHAR ( 64 ) NOT NULL DEFAULT '' COMMENT '玩家头像',
	#`sex` TINYINT ( 2 ) NOT NULL DEFAULT '0' COMMENT '性别 0:未知，1:男,2女',
	`loginip` VARCHAR ( 64 ) NOT NULL DEFAULT '' COMMENT '登录IP地址:端口',
	`lasttime` BIGINT ( 20 ) NOT NULL DEFAULT 0 COMMENT '最后登录时间',
	`lastofftime` BIGINT ( 20 ) NOT NULL DEFAULT 0 COMMENT '上一次退出时间',
	`createdate` BIGINT ( 20 ) NOT NULL DEFAULT 0 COMMENT '注册时间',
	`status` TINYINT ( 2 ) NOT NULL DEFAULT 0 COMMENT '状态：0=正常,1=封号',
	`gm` TINYINT ( 2 ) NOT NULL DEFAULT 0 COMMENT '是否GM，0:否，1：是',
	`v` LONGBLOB DEFAULT NULL COMMENT '玩家字段',
	PRIMARY KEY ( `id` ) USING BTREE,
	UNIQUE KEY `idx_player_name` (`name`) USING HASH,
	UNIQUE KEY `idx_player_user_no` (`userno`) USING HASH,
	KEY `idx_create_date` ( `createdate` ) USING BTREE
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '玩家';


#游戏数据
DROP TABLE IF EXISTS `game_data`;
CREATE TABLE `game_data` (
	`id` BIGINT ( 20 ) NOT NULL COMMENT '记录ID',
	`name` VARCHAR ( 1024 ) NOT NULL DEFAULT '' COMMENT '数据名称',
	`v` LONGBLOB DEFAULT NULL COMMENT '数据字段',
	PRIMARY KEY ( `id` ) USING BTREE
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '游戏数据';

#创建角色(作废)
/**DROP TABLE IF EXISTS `charater`;
CREATE TABLE `charater` (
	`id` BIGINT ( 20 ) NOT NULL default '0' COMMENT 'NPC ID',
  `playerId` bigint(20) unsigned NOT NULL default 0 COMMENT '玩家ID',
	`model` TINYINT ( 2 ) NOT NULL DEFAULT '0' COMMENT '角色模型',
	`name` VARCHAR ( 64 ) NOT NULL DEFAULT '' COMMENT '角色名称',
	`occupation` VARCHAR ( 64 ) NOT NULL DEFAULT '' COMMENT '角色职业',
	`keyword` VARCHAR ( 128 ) NOT NULL DEFAULT '' COMMENT '关键词',
	`hair` TINYINT ( 2 )  NOT NULL DEFAULT '0' COMMENT '发型',
	`top` TINYINT ( 2 )  NOT NULL DEFAULT '0' COMMENT 'top',
	`bottoms` TINYINT ( 2 )  NOT NULL DEFAULT '0' COMMENT 'bottoms',
	PRIMARY KEY ( `id` ) USING BTREE,
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '玩家角色信息';**/


#NPC
DROP TABLE IF EXISTS `npc`;
CREATE TABLE `npc` (
	`id` BIGINT ( 20 ) UNSIGNED NOT NULL COMMENT 'NPC ID',
	`playerId` BIGINT ( 20 ) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'NPC 所属玩家',
	`name` VARCHAR ( 64 ) NOT NULL DEFAULT '' COMMENT 'NPC的名称',
	`type` TINYINT ( 2 ) NOT NULL DEFAULT 0 COMMENT 'NPC的类型：',
	`model` INT ( 10 ) NOT NULL DEFAULT 0 COMMENT '模型ID',
	`career` VARCHAR ( 256 ) NOT NULL DEFAULT '' COMMENT '职业',
	`keyword` VARCHAR ( 256 ) NOT NULL DEFAULT '' COMMENT '关键词',
	`hair` INT ( 10 ) NOT NULL DEFAULT 0 COMMENT '发型',
	`top` INT ( 10 ) NOT NULL DEFAULT 0 COMMENT 'top',
	`bottoms` INT ( 10 ) NOT NULL DEFAULT 0 COMMENT 'bottoms',
	`speed` INT ( 10 ) NOT NULL DEFAULT 0 COMMENT 'NPC移动速度',
	`x` INT ( 10 ) NOT NULL DEFAULT 0 COMMENT 'npc当前位置X轴',
	`y` INT ( 10 ) NOT NULL DEFAULT 0 COMMENT 'npc当前位置Y轴',
	`dressId` INT ( 10 ) NOT NULL DEFAULT 0 COMMENT '时装Id',
	`createdate` BIGINT ( 20 ) NOT NULL COMMENT '创建时间',
	`endTime` BIGINT ( 20 ) NOT NULL COMMENT '结束时间',
	#`deleted_time` bigint(20) DEFAULT NULL COMMENT '删除时间',
  #`deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志:0:未删除，1:已删除',
	`v` LONGBLOB DEFAULT NULL COMMENT 'NPC其他字段',
	PRIMARY KEY ( `id` ) USING BTREE,
	KEY `idx_playerId` ( `playerId` ) USING BTREE
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = 'npc信息表';



#行为日志表
DROP TABLE IF EXISTS `action`;
CREATE TABLE `action` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `npc_id` bigint(20) unsigned NOT NULL default 0 COMMENT 'NPC ID',
  `aid` varchar(50) NOT NULL DEFAULT '' COMMENT '当前行为ID',
  `paid` bigint(20) NOT NULL DEFAULT 0 COMMENT '前一个行为ID',
  `start_time` bigint(20) NOT NULL DEFAULT 0 COMMENT '行为开始时间',
  `end_time` bigint(20) NOT NULL DEFAULT 0 COMMENT '行为结束时间',
  `content` mediumtext NOT NULL COMMENT '行为内容',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志:0:未删除，1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_aid` (`aid`) USING BTREE,
  KEY `idx_start_time` (`start_time`) USING BTREE,
  KEY `idx_end_time` (`end_time`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='行为日志表';

#聊天信息
DROP TABLE IF EXISTS `chat`;
CREATE TABLE `chat` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '聊天消息ID',
  `chat_id` varchar(50) NOT NULL DEFAULT '' COMMENT '聊天ID=聊天者ID（小）+聊天者ID（大）+ 类型（暂定：1:私聊 2:群聊，3聊天室 4客服 5系统）',
  `sender_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '发送者ID',
  `target_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '接收者ID',
  `msg_type` smallint(10) NOT NULL DEFAULT 0 COMMENT '消息类型1文字,2图片,3emoji,4音频,5文件',
  `content` mediumtext NOT NULL COMMENT '消息内容',
  `other` varchar(4000) DEFAULT NULL COMMENT '其他信息',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `deleted_time` bigint(20) DEFAULT NULL COMMENT '删除时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志:0:未删除，1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_chat_id` (`chat_id`) USING BTREE,
  KEY `idx_sender_id` (`sender_id`) USING BTREE,
  KEY `idx_target_id` (`target_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='聊天记录表';



#NPC 活动日志(作废)
DROP TABLE IF EXISTS `npc_activity`;
CREATE TABLE `npc_activity` (
  `id` bigint(20) unsigned NOT NULL COMMENT '活动ID',
  `npc_id` bigint(20) unsigned NOT NULL default 0 COMMENT 'NPC ID',
  `type` TINYINT ( 2 ) NOT NULL DEFAULT 0 COMMENT '活动类型',
  `context` varchar(4000) DEFAULT NULL COMMENT 'NPC活动内容',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `deleted_time` bigint(20) DEFAULT NULL COMMENT '删除时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志:0:未删除，1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_npc_id` (`npc_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='npc活动表';



#==============NPC股票=======
DROP TABLE IF EXISTS `stock`;
CREATE TABLE `stock` (
	`id` BIGINT ( 20 ) UNSIGNED NOT NULL COMMENT 'stock Id',
	`code` VARCHAR ( 32 ) NOT NULL DEFAULT '' COMMENT '股票代码',
	`name` VARCHAR ( 32 ) NOT NULL DEFAULT '' COMMENT '股票名称',
	`npc_id` BIGINT ( 20 ) NOT NULL DEFAULT 0 COMMENT '所属NPC',
	`total_shares` BIGINT ( 20 ) NOT NULL DEFAULT '0' COMMENT '股票总数',
	`available_shares` BIGINT ( 20 ) NOT NULL DEFAULT '0' COMMENT '可交易的股票数',
	`issue_price` decimal ( 12,2 ) NOT NULL DEFAULT 0 COMMENT '发行价格',
	`share_price` decimal ( 12,2 ) NOT NULL DEFAULT 0 COMMENT '每股价格',
	`status` TINYINT ( 2 ) NOT NULL DEFAULT 0 COMMENT '状态：0:IPO,1:交易,2:停牌,3:退市',
	`marketdate` BIGINT ( 20 ) NOT NULL COMMENT '上市时间',
	`createdate` BIGINT ( 20 ) NOT NULL COMMENT '创建时间',
	#`deleted_time` bigint(20) DEFAULT NULL COMMENT '删除时间',
  #`deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志:0:未删除，1:已删除',
	`v` LONGBLOB DEFAULT NULL COMMENT 'NPC其他字段',
	PRIMARY KEY ( `id` ) USING BTREE,
	UNIQUE KEY `idx_code` (`code`) USING HASH,
	KEY `idx_npc_id` ( `npc_id` ) USING BTREE
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = 'NPC股票信息表';

#IPO玩家出价表
DROP TABLE IF EXISTS `stock_bid`;
CREATE TABLE `stock_bid` (
	`id` BIGINT ( 20 ) UNSIGNED NOT NULL COMMENT 'ID',
	`player_id` BIGINT ( 20 ) UNSIGNED NOT NULL DEFAULT 0 COMMENT '对应的玩家',
	`stock_id` BIGINT ( 20 ) NOT NULL COMMENT '对应的股票ID',
	`price` decimal ( 12,2 ) NOT NULL DEFAULT 0 COMMENT '股票报价',
	`createdate` BIGINT ( 20 ) NOT NULL COMMENT '创建时间',
	`deleted_time` bigint(20) DEFAULT NULL COMMENT '删除时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志:0:未删除，1:已删除',
	PRIMARY KEY ( `id` ) USING BTREE,
	KEY `idx_player_id` ( `player_id` ) USING BTREE,
	KEY `idx_stock_id` ( `stock_id` ) USING BTREE
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = 'IPO玩家出价表';


#玩家持有的股票信息表
DROP TABLE IF EXISTS `player_stock`;
CREATE TABLE `player_stock` (
	`id` BIGINT ( 20 ) UNSIGNED NOT NULL COMMENT '玩家股票记录的唯一标识',
	`player_id` BIGINT ( 20 ) UNSIGNED NOT NULL DEFAULT 0 COMMENT '对应的玩家',
	`stock_id` BIGINT ( 20 ) NOT NULL COMMENT '对应的股票ID',
	`shares` BIGINT ( 20 ) NOT NULL DEFAULT 0 COMMENT '持有的股票数',
	`createdate` BIGINT ( 20 ) NOT NULL COMMENT '创建时间',
	`deleted_time` bigint(20) DEFAULT NULL COMMENT '删除时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志:0:未删除，1:已删除',
	PRIMARY KEY ( `id` ) USING BTREE,
	UNIQUE KEY `idx_player_stock_id` (`player_id`,`stock_id`) USING HASH,
	KEY `idx_stock_id` ( `stock_id` ) USING BTREE
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '玩家持有的股票信息表';

#交易记录信息表
DROP TABLE IF EXISTS `stock_trans`;
CREATE TABLE `stock_trans` (
	`id` BIGINT ( 20 ) UNSIGNED NOT NULL COMMENT '交易记录的唯一标识',
	`stock_id` BIGINT ( 20 ) NOT NULL COMMENT '对应的股票',
	`buyer_id` BIGINT ( 20 ) NOT NULL DEFAULT 0 COMMENT '买家玩家ID',
	`seller_id` BIGINT ( 32 ) NOT NULL DEFAULT 0 COMMENT '卖家玩家ID（如果是NPC发行股票则为NULL）',
	`shares` BIGINT ( 20 ) NOT NULL DEFAULT 0 COMMENT '交易的股票数',
	`price` decimal ( 12,2 ) NOT NULL DEFAULT 0 COMMENT '每股交易价格',
	`createdate` BIGINT ( 20 ) NOT NULL COMMENT '创建时间',
	`deleted_time` bigint(20) DEFAULT NULL COMMENT '删除时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志:0:未删除，1:已删除',
	PRIMARY KEY ( `id` ) USING BTREE,
	KEY `idx_stock_id` ( `stock_id` ) USING BTREE,
	KEY `idx_buyer_id` ( `buyer_id` ) USING BTREE,
	KEY `idx_seller_id` ( `seller_id` ) USING BTREE
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '交易记录信息表';


#记录交易订单信息
DROP TABLE IF EXISTS `stock_order`;
CREATE TABLE `stock_order` (
	`id` BIGINT ( 20 ) UNSIGNED NOT NULL COMMENT '订单的唯一标识',
	`player_id` BIGINT ( 20 ) NOT NULL DEFAULT 0 COMMENT '买家玩家ID',
  `order_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '订单类型（"BUY"或"SELL"）',
	`stock_id` BIGINT ( 20 ) NOT NULL COMMENT '对应的股票',
	`shares` BIGINT ( 20 ) NOT NULL DEFAULT 0 COMMENT '订单数量/交易的股票数',
	`price` decimal ( 12,2 ) NOT NULL DEFAULT 0 COMMENT '每股交易价格',
	`status` TINYINT ( 2 ) NOT NULL DEFAULT 0 COMMENT '订单状态（"PENDING", "COMPLETED", "CANCELLED"）',
	`createdate` BIGINT ( 20 ) NOT NULL COMMENT '创建时间',
	`deleted_time` bigint(20) DEFAULT NULL COMMENT '删除时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志:0:未删除，1:已删除',
	PRIMARY KEY ( `id` ) USING BTREE,
	KEY `idx_player_id` ( `player_id` ) USING BTREE,
	KEY `idx_stock_id` ( `stock_id` ) USING BTREE
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '记录交易订单信息';