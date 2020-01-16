CREATE DATABASE  IF NOT EXISTS `prophecy`;
USE `prophecy`;

--
-- Table structure for table `user`
--
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `name` varchar(45) NOT NULL COMMENT '用户名',
  `age` int(11) NOT NULL COMMENT '年龄',
  `password` varchar(45) NOT NULL COMMENT '用户密码',
  `sex` varchar(45) NOT NULL COMMENT '性别',
  `school` varchar(45) NOT NULL COMMENT '所属院校/毕业院校',
  `subordinate_class` varchar(45) NOT NULL COMMENT '所属班级',
  `graduation_time` int(11) NOT NULL COMMENT '毕业年份',
  `register_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `uid` varchar(45) NOT NULL COMMENT '用户编号',
  `balance` int(11) DEFAULT '0' COMMENT '账户余额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
--  Table structure for `message`
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `uid` int(11) NOT NULL COMMENT '目标用户ID，0-发给所有人',
  `type` varchar(15) NOT NULL COMMENT '消息类型：sys-系统通知，report-报告通知',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `content` varchar(1023) DEFAULT NULL COMMENT '内容',
  `target` varchar(511) DEFAULT NULL COMMENT '关联目标，JSON格式，用于跳转到目标点',
  `publisher_uid` int(11) DEFAULT NULL COMMENT '发布人用户ID',
  `flag` int(1) DEFAULT '1' COMMENT '数据有效性标识：0-无效，1-有效',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `uid_idx` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户消息表';

-- ----------------------------
--  Table structure for `message_status`
-- ----------------------------
DROP TABLE IF EXISTS `message_status`;
CREATE TABLE `message_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `uid` int(11) NOT NULL COMMENT '用户ID',
  `mid` int(11) NOT NULL COMMENT '消息ID',
  `read_flag` int(1) DEFAULT NULL COMMENT '读取状态',
  `read_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '读取时间',
  `del_flag` int(1) DEFAULT NULL COMMENT '删除状态',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `mid_UNIQUE` (`mid`,`uid`),
  KEY `uid_idx` (`uid`),
  KEY `mid_idx` (`mid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户消息状态表';

-- ----------------------------
--  Table structure for `virtual_currency`
-- ----------------------------
DROP TABLE IF EXISTS `virtual_currency`;
CREATE TABLE `virtual_currency` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `account_name` varchar(511) DEFAULT NULL COMMENT '账户名称',
  `transaction` int(11) DEFAULT NULL COMMENT '交易额',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='虚拟货币表';

-- ----------------------------
--  Table structure for `post`
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `uid` VARCHAR(45) NULL COMMENT '发帖人uid',
  `title` VARCHAR(511) NULL COMMENT '标题',
  `content` VARCHAR(1023) NULL COMMENT '内容',
  `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发帖时间',
  `flag` INT(1) NULL DEFAULT 1 COMMENT '删除标志 1-有效 0-无效',
  `likes` INT(11) NULL DEFAULT 0 COMMENT '点赞数',
  PRIMARY KEY (`id`))
  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '帖子信息表';

-- ----------------------------
--  Table structure for `post_reply`
-- ----------------------------
DROP TABLE IF EXISTS `post_reply`;
CREATE TABLE `post_reply` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `uid` varchar(511) DEFAULT NULL COMMENT '用户编号',
  `content` varchar(511) DEFAULT NULL COMMENT '内容',
  `reply_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '回复时间',
  `pid` int(11) DEFAULT NULL COMMENT '帖子id',
  `rid` int(11) DEFAULT NULL COMMENT '回复id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='评论回复表';

-- ----------------------------
--  Table structure for `interface_usage_statistics`
-- ----------------------------
DROP TABLE IF EXISTS `interface_usage_statistics`;
CREATE TABLE `interface_usage_statistics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(45) DEFAULT NULL,
  `uri` varchar(45) DEFAULT NULL,
  `response_time` int(11) DEFAULT NULL COMMENT '响应时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `args` varchar(511) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `fail_message` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `like_statistics`
-- ----------------------------
DROP TABLE IF EXISTS `like_statistics`;
CREATE TABLE `like_statistics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(45) NOT NULL,
  `like_flag` int(1) DEFAULT NULL COMMENT '点赞标记',
  `pid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `attention_statistics`
-- ----------------------------
DROP TABLE IF EXISTS `attention_statistics`;
CREATE TABLE `attention_statistics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `aid` varchar(45) COLLATE utf8mb4_general_ci NOT NULL COMMENT '关注人uid',
  `pid` varchar(45) COLLATE utf8mb4_general_ci NOT NULL COMMENT '被关注人uid',
  `attention_time` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `attention_operation` int(11) DEFAULT NULL COMMENT '关注操作，1-关注，2-取关',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
--  Table structure for `attention_status`
-- ----------------------------
DROP TABLE IF EXISTS `attention_status`;
CREATE TABLE `attention_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `aid` varchar(45) COLLATE utf8mb4_general_ci NOT NULL COMMENT '关注人uid',
  `pid` varchar(45) COLLATE utf8mb4_general_ci NOT NULL COMMENT '被关注人uid',
  `status` int(11) NOT NULL COMMENT '关注状态，1-有效，0-无效',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `attention_status_aid_pid_uindex` (`aid`,`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

ALTER TABLE `prophecy`.`interface_usage_statistics` CHANGE COLUMN `status` `status` VARCHAR(45) NULL DEFAULT '00' ;
ALTER TABLE `prophecy`.`interface_usage_statistics` CHANGE COLUMN `fail_message` `fail_message` VARCHAR(1023) NULL DEFAULT NULL ;
ALTER TABLE message MODIFY uid varchar(511) NOT NULL COMMENT '目标用户ID，0-发给所有人';
ALTER TABLE message MODIFY publisher_uid varchar(511) COMMENT '发布人用户ID';
ALTER TABLE message_status MODIFY uid varchar(511) NOT NULL COMMENT '用户ID';
ALTER TABLE message MODIFY type varchar(15) DEFAULT null  COMMENT '消息类型：sys-系统通知，report-报告通知';
ALTER TABLE user MODIFY register_time varchar(20) NOT NULL COMMENT '注册时间';
ALTER TABLE post MODIFY create_time varchar(20) COMMENT '发帖时间';
ALTER TABLE post_reply MODIFY reply_time varchar(20) COMMENT '回复时间';
ALTER TABLE interface_usage_statistics MODIFY fail_message varchar(10239);




