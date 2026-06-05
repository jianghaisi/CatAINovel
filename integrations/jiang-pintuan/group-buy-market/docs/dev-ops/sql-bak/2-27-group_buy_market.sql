# ************************************************************
# Sequel Ace SQL dump
# 鐗堟湰鍙凤細 20094
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# 涓绘満: 127.0.0.1 (MySQL 8.0.42)
# 鏁版嵁搴? group_buy_market
# 鐢熸垚鏃堕棿: 2025-07-16 00:09:44 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE database if NOT EXISTS `group_buy_market` default character set utf8mb4 collate utf8mb4_0900_ai_ci;
use `group_buy_market`;

# 杞偍琛?crowd_tags
# ------------------------------------------------------------

DROP TABLE IF EXISTS `crowd_tags`;

CREATE TABLE `crowd_tags` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `tag_id` varchar(32) NOT NULL COMMENT '浜虹兢ID',
  `tag_name` varchar(64) NOT NULL COMMENT '浜虹兢鍚嶇О',
  `tag_desc` varchar(256) NOT NULL COMMENT '浜虹兢鎻忚堪',
  `statistics` int NOT NULL COMMENT '浜虹兢鏍囩缁熻閲?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浜虹兢鏍囩';

LOCK TABLES `crowd_tags` WRITE;
/*!40000 ALTER TABLE `crowd_tags` DISABLE KEYS */;

INSERT INTO `crowd_tags` (`id`, `tag_id`, `tag_name`, `tag_desc`, `statistics`, `create_time`, `update_time`)
VALUES
	(1,'RQ_KJHKL98UU78H66554GFDV','娼滃湪娑堣垂鐢ㄦ埛','娼滃湪娑堣垂鐢ㄦ埛',33,'2024-12-28 12:53:28','2025-01-28 08:23:57');

/*!40000 ALTER TABLE `crowd_tags` ENABLE KEYS */;
UNLOCK TABLES;


# 杞偍琛?crowd_tags_detail
# ------------------------------------------------------------

DROP TABLE IF EXISTS `crowd_tags_detail`;

CREATE TABLE `crowd_tags_detail` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `tag_id` varchar(32) NOT NULL COMMENT '浜虹兢ID',
  `user_id` varchar(16) NOT NULL COMMENT '鐢ㄦ埛ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tag_user` (`tag_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浜虹兢鏍囩鏄庣粏';

LOCK TABLES `crowd_tags_detail` WRITE;
/*!40000 ALTER TABLE `crowd_tags_detail` DISABLE KEYS */;

INSERT INTO `crowd_tags_detail` (`id`, `tag_id`, `user_id`, `create_time`, `update_time`)
VALUES
	(4,'RQ_KJHKL98UU78H66554GFDV','jiang','2024-12-28 14:42:30','2024-12-28 14:42:30'),
	(5,'RQ_KJHKL98UU78H66554GFDV','liergou','2024-12-28 14:42:30','2024-12-28 14:42:30'),
	(9,'RQ_KJHKL98UU78H66554GFDV','xfg01','2025-01-25 15:44:55','2025-01-25 15:44:55'),
	(10,'RQ_KJHKL98UU78H66554GFDV','xfg02','2025-01-25 15:44:55','2025-01-25 15:44:55'),
	(11,'RQ_KJHKL98UU78H66554GFDV','xfg03','2025-01-25 15:44:55','2025-01-25 15:44:55'),
	(17,'RQ_KJHKL98UU78H66554GFDV','xfg04','2025-01-26 19:10:36','2025-01-26 19:10:36'),
	(18,'RQ_KJHKL98UU78H66554GFDV','xfg05','2025-01-26 19:10:36','2025-01-26 19:10:36'),
	(19,'RQ_KJHKL98UU78H66554GFDV','xfg06','2025-01-26 19:10:37','2025-01-26 19:10:37'),
	(20,'RQ_KJHKL98UU78H66554GFDV','xfg07','2025-01-26 19:10:37','2025-01-26 19:10:37'),
	(21,'RQ_KJHKL98UU78H66554GFDV','xfg08','2025-01-26 19:10:37','2025-01-26 19:10:37'),
	(22,'RQ_KJHKL98UU78H66554GFDV','xfg09','2025-01-26 19:10:37','2025-01-26 19:10:37');

/*!40000 ALTER TABLE `crowd_tags_detail` ENABLE KEYS */;
UNLOCK TABLES;


# 杞偍琛?crowd_tags_job
# ------------------------------------------------------------

DROP TABLE IF EXISTS `crowd_tags_job`;

CREATE TABLE `crowd_tags_job` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `tag_id` varchar(32) NOT NULL COMMENT '鏍囩ID',
  `batch_id` varchar(8) NOT NULL COMMENT '鎵规ID',
  `tag_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '鏍囩绫诲瀷锛堝弬涓庨噺銆佹秷璐归噾棰濓級',
  `tag_rule` varchar(8) NOT NULL COMMENT '鏍囩瑙勫垯锛堥檺瀹氱被鍨?N娆★級',
  `stat_start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '缁熻鏁版嵁锛屽紑濮嬫椂闂?,
  `stat_end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '缁熻鏁版嵁锛岀粨鏉熸椂闂?,
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '鐘舵€侊紱0鍒濆銆?璁″垝锛堣繘鍏ユ墽琛岄樁娈碉級銆?閲嶇疆銆?瀹屾垚',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_batch_id` (`batch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浜虹兢鏍囩浠诲姟';

LOCK TABLES `crowd_tags_job` WRITE;
/*!40000 ALTER TABLE `crowd_tags_job` DISABLE KEYS */;

INSERT INTO `crowd_tags_job` (`id`, `tag_id`, `batch_id`, `tag_type`, `tag_rule`, `stat_start_time`, `stat_end_time`, `status`, `create_time`, `update_time`)
VALUES
	(1,'RQ_KJHKL98UU78H66554GFDV','10001',0,'100','2024-12-28 12:55:05','2024-12-28 12:55:05',0,'2024-12-28 12:55:05','2024-12-28 12:55:05');

/*!40000 ALTER TABLE `crowd_tags_job` ENABLE KEYS */;
UNLOCK TABLES;


# 杞偍琛?group_buy_activity
# ------------------------------------------------------------

DROP TABLE IF EXISTS `group_buy_activity`;

CREATE TABLE `group_buy_activity` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷',
  `activity_id` bigint NOT NULL COMMENT '娲诲姩ID',
  `activity_name` varchar(128) NOT NULL COMMENT '娲诲姩鍚嶇О',
  `discount_id` varchar(8) NOT NULL COMMENT '鎶樻墸ID',
  `group_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '鎷煎洟鏂瑰紡锛?鑷姩鎴愬洟銆?杈炬垚鐩爣鎷煎洟锛?,
  `take_limit_count` int NOT NULL DEFAULT '1' COMMENT '鎷煎洟娆℃暟闄愬埗',
  `target` int NOT NULL DEFAULT '1' COMMENT '鎷煎洟鐩爣',
  `valid_time` int NOT NULL DEFAULT '15' COMMENT '鎷煎洟鏃堕暱锛堝垎閽燂級',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '娲诲姩鐘舵€侊紙0鍒涘缓銆?鐢熸晥銆?杩囨湡銆?搴熷純锛?,
  `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '娲诲姩寮€濮嬫椂闂?,
  `end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '娲诲姩缁撴潫鏃堕棿',
  `tag_id` varchar(8) DEFAULT NULL COMMENT '浜虹兢鏍囩瑙勫垯鏍囪瘑',
  `tag_scope` varchar(4) DEFAULT NULL COMMENT '浜虹兢鏍囩瑙勫垯鑼冨洿锛堝閫夛紱1鍙闄愬埗銆?鍙備笌闄愬埗锛?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_activity_id` (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎷煎洟娲诲姩';

LOCK TABLES `group_buy_activity` WRITE;
/*!40000 ALTER TABLE `group_buy_activity` DISABLE KEYS */;

INSERT INTO `group_buy_activity` (`id`, `activity_id`, `activity_name`, `discount_id`, `group_type`, `take_limit_count`, `target`, `valid_time`, `status`, `start_time`, `end_time`, `tag_id`, `tag_scope`, `create_time`, `update_time`)
VALUES
	(1,100123,'娴嬭瘯娲诲姩','25120207',0,1,3,15,1,'2024-12-07 10:19:40','2029-12-07 10:19:40','1','1','2024-12-07 10:19:40','2025-04-05 11:21:03');

/*!40000 ALTER TABLE `group_buy_activity` ENABLE KEYS */;
UNLOCK TABLES;


# 杞偍琛?group_buy_discount
# ------------------------------------------------------------

DROP TABLE IF EXISTS `group_buy_discount`;

CREATE TABLE `group_buy_discount` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `discount_id` varchar(8) NOT NULL COMMENT '鎶樻墸ID',
  `discount_name` varchar(64) NOT NULL COMMENT '鎶樻墸鏍囬',
  `discount_desc` varchar(256) NOT NULL COMMENT '鎶樻墸鎻忚堪',
  `discount_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '鎶樻墸绫诲瀷锛?:base銆?:tag锛?,
  `market_plan` varchar(4) NOT NULL DEFAULT 'ZJ' COMMENT '钀ラ攢浼樻儬璁″垝锛圸J:鐩村噺銆丮J:婊″噺銆丯鍏冭喘锛?,
  `market_expr` varchar(32) NOT NULL COMMENT '钀ラ攢浼樻儬琛ㄨ揪寮?,
  `tag_id` varchar(8) DEFAULT NULL COMMENT '浜虹兢鏍囩锛岀壒瀹氫紭鎯犻檺瀹?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_discount_id` (`discount_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `group_buy_discount` WRITE;
/*!40000 ALTER TABLE `group_buy_discount` DISABLE KEYS */;

INSERT INTO `group_buy_discount` (`id`, `discount_id`, `discount_name`, `discount_desc`, `discount_type`, `market_plan`, `market_expr`, `tag_id`, `create_time`, `update_time`)
VALUES
	(1,'25120207','娴嬭瘯浼樻儬','娴嬭瘯浼樻儬',0,'ZJ','20',NULL,'2024-12-07 10:20:15','2024-12-21 11:13:32');

/*!40000 ALTER TABLE `group_buy_discount` ENABLE KEYS */;
UNLOCK TABLES;


# 杞偍琛?group_buy_order
# ------------------------------------------------------------

DROP TABLE IF EXISTS `group_buy_order`;

CREATE TABLE `group_buy_order` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `team_id` varchar(8) NOT NULL COMMENT '鎷煎崟缁勯槦ID',
  `activity_id` bigint NOT NULL COMMENT '娲诲姩ID',
  `source` varchar(8) NOT NULL COMMENT '娓犻亾',
  `channel` varchar(8) NOT NULL COMMENT '鏉ユ簮',
  `original_price` decimal(8,2) NOT NULL COMMENT '鍘熷浠锋牸',
  `deduction_price` decimal(8,2) NOT NULL COMMENT '鎶樻墸閲戦',
  `pay_price` decimal(8,2) NOT NULL COMMENT '鏀粯浠锋牸',
  `target_count` int NOT NULL COMMENT '鐩爣鏁伴噺',
  `complete_count` int NOT NULL COMMENT '瀹屾垚鏁伴噺',
  `lock_count` int NOT NULL COMMENT '閿佸崟鏁伴噺',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '鐘舵€侊紙0-鎷煎崟涓€?-瀹屾垚銆?-澶辫触锛?,
  `valid_start_time` datetime NOT NULL COMMENT '鎷煎洟寮€濮嬫椂闂?,
  `valid_end_time` datetime NOT NULL COMMENT '鎷煎洟缁撴潫鏃堕棿',
  `notify_type` varchar(8) NOT NULL DEFAULT 'HTTP' COMMENT '鍥炶皟绫诲瀷锛圚TTP銆丮Q锛?,
  `notify_url` varchar(512) DEFAULT NULL COMMENT '鍥炶皟鍦板潃锛圚TTP 鍥炶皟涓嶅彲涓虹┖锛?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_team_id` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `group_buy_order` WRITE;
/*!40000 ALTER TABLE `group_buy_order` DISABLE KEYS */;

INSERT INTO `group_buy_order` (`id`, `team_id`, `activity_id`, `source`, `channel`, `original_price`, `deduction_price`, `pay_price`, `target_count`, `complete_count`, `lock_count`, `status`, `valid_start_time`, `valid_end_time`, `notify_type`, `notify_url`, `create_time`, `update_time`)
VALUES
	(23,'29487599',100123,'s01','c01',100.00,20.00,80.00,3,0,2,0,'2025-04-05 14:54:47','2025-04-05 15:09:47','HTTP','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-04-05 14:54:46','2025-04-05 14:56:23'),
	(24,'15721600',100123,'s01','c01',100.00,20.00,80.00,3,1,2,0,'2025-04-05 15:18:29','2025-04-05 15:33:29','HTTP','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-04-05 15:18:29','2025-07-16 08:03:29');

/*!40000 ALTER TABLE `group_buy_order` ENABLE KEYS */;
UNLOCK TABLES;


# 杞偍琛?group_buy_order_list
# ------------------------------------------------------------

DROP TABLE IF EXISTS `group_buy_order_list`;

CREATE TABLE `group_buy_order_list` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `user_id` varchar(64) NOT NULL COMMENT '鐢ㄦ埛ID',
  `team_id` varchar(8) NOT NULL COMMENT '鎷煎崟缁勯槦ID',
  `order_id` varchar(12) NOT NULL COMMENT '璁㈠崟ID',
  `activity_id` bigint NOT NULL COMMENT '娲诲姩ID',
  `start_time` datetime NOT NULL COMMENT '娲诲姩寮€濮嬫椂闂?,
  `end_time` datetime NOT NULL COMMENT '娲诲姩缁撴潫鏃堕棿',
  `goods_id` varchar(16) NOT NULL COMMENT '鍟嗗搧ID',
  `source` varchar(8) NOT NULL COMMENT '娓犻亾',
  `channel` varchar(8) NOT NULL COMMENT '鏉ユ簮',
  `original_price` decimal(8,2) NOT NULL COMMENT '鍘熷浠锋牸',
  `deduction_price` decimal(8,2) NOT NULL COMMENT '鎶樻墸閲戦',
  `pay_price` decimal(8,2) NOT NULL COMMENT '鏀粯閲戦',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '鐘舵€侊紱0鍒濆閿佸畾銆?娑堣垂瀹屾垚銆?鐢ㄦ埛閫€鍗?,
  `out_trade_no` varchar(12) NOT NULL COMMENT '澶栭儴浜ゆ槗鍗曞彿-纭繚澶栭儴璋冪敤鍞竴骞傜瓑',
  `out_trade_time` datetime DEFAULT NULL COMMENT '澶栭儴浜ゆ槗鏃堕棿',
  `biz_id` varchar(64) NOT NULL COMMENT '涓氬姟鍞竴ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  KEY `idx_user_id_activity_id` (`user_id`,`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `group_buy_order_list` WRITE;
/*!40000 ALTER TABLE `group_buy_order_list` DISABLE KEYS */;

INSERT INTO `group_buy_order_list` (`id`, `user_id`, `team_id`, `order_id`, `activity_id`, `start_time`, `end_time`, `goods_id`, `source`, `channel`, `original_price`, `deduction_price`, `pay_price`, `status`, `out_trade_no`, `out_trade_time`, `biz_id`, `create_time`, `update_time`)
VALUES
	(25,'xfg03','29487599','069645854815',100123,'2024-12-07 10:19:40','2029-12-07 10:19:40','9890001','s01','c01',100.00,20.00,80.00,0,'769515763172',NULL,'100123_xfg03_1','2025-04-05 14:54:46','2025-04-05 14:54:46'),
	(26,'xfg04','29487599','663374201900',100123,'2024-12-07 10:19:40','2029-12-07 10:19:40','9890001','s01','c01',100.00,20.00,80.00,0,'727869517356',NULL,'100123_xfg04_1','2025-04-05 14:56:23','2025-04-05 14:56:23'),
	(27,'xfg01','15721600','146024339576',100123,'2024-12-07 10:19:40','2029-12-07 10:19:40','9890001','s01','c01',100.00,20.00,80.00,0,'441842218120',NULL,'100123_xfg01_1','2025-04-05 15:18:29','2025-04-05 15:18:29'),
	(29,'xfg02','15721600','630306594433',100123,'2024-12-07 10:19:40','2029-12-07 10:19:40','9890001','s01','c01',100.00,20.00,80.00,1,'061974054911','2025-04-05 15:21:51','100123_xfg02_1','2025-04-05 15:21:51','2025-07-16 08:03:35');

/*!40000 ALTER TABLE `group_buy_order_list` ENABLE KEYS */;
UNLOCK TABLES;


# 杞偍琛?notify_task
# ------------------------------------------------------------

DROP TABLE IF EXISTS `notify_task`;

CREATE TABLE `notify_task` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `activity_id` bigint NOT NULL COMMENT '娲诲姩ID',
  `team_id` varchar(8) NOT NULL COMMENT '鎷煎崟缁勯槦ID',
  `notify_type` varchar(8) NOT NULL DEFAULT 'HTTP' COMMENT '鍥炶皟绫诲瀷锛圚TTP銆丮Q锛?,
  `notify_mq` varchar(32) DEFAULT NULL COMMENT '鍥炶皟娑堟伅',
  `notify_url` varchar(128) DEFAULT NULL COMMENT '鍥炶皟鎺ュ彛',
  `notify_count` int NOT NULL COMMENT '鍥炶皟娆℃暟',
  `notify_status` tinyint(1) NOT NULL COMMENT '鍥炶皟鐘舵€併€?鍒濆銆?瀹屾垚銆?閲嶈瘯銆?澶辫触銆?,
  `parameter_json` varchar(256) NOT NULL COMMENT '鍙傛暟瀵硅薄',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_team_id` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `notify_task` WRITE;
/*!40000 ALTER TABLE `notify_task` DISABLE KEYS */;

INSERT INTO `notify_task` (`id`, `activity_id`, `team_id`, `notify_type`, `notify_mq`, `notify_url`, `notify_count`, `notify_status`, `parameter_json`, `create_time`, `update_time`)
VALUES
	(7,100123,'58693013','MQ','topic.team_success',NULL,1,1,'{\"teamId\":\"58693013\",\"outTradeNoList\":[\"214969043474\"]}','2025-03-16 18:23:05','2025-03-16 18:23:05'),
	(8,100123,'16341565','HTTP',NULL,'http://127.0.0.1:8091/api/v1/test/group_buy_notify',1,1,'{\"teamId\":\"16341565\",\"outTradeNoList\":[\"539291175688\"]}','2025-03-16 18:28:59','2025-03-22 09:53:26'),
	(9,100123,'63403622','MQ','topic.team_success',NULL,1,1,'{\"teamId\":\"63403622\",\"outTradeNoList\":[\"904941690333\"]}','2025-03-17 22:12:04','2025-03-17 22:12:04'),
	(10,100123,'44784629','MQ','topic.team_success',NULL,1,1,'{\"teamId\":\"44784629\",\"outTradeNoList\":[\"008981724008\"]}','2025-03-22 10:50:03','2025-03-22 10:50:03'),
	(11,100123,'17743372','MQ','topic.team_success',NULL,1,1,'{\"teamId\":\"17743372\",\"outTradeNoList\":[\"562013701479\"]}','2025-03-22 11:20:40','2025-03-22 11:20:41'),
	(12,100123,'30797609','MQ','topic.team_success',NULL,1,1,'{\"teamId\":\"30797609\",\"outTradeNoList\":[\"482309025222\"]}','2025-03-22 12:04:08','2025-03-22 12:04:08'),
	(13,100123,'46018603','MQ','topic.team_success',NULL,1,1,'{\"teamId\":\"46018603\",\"outTradeNoList\":[\"549111800140\"]}','2025-03-22 12:06:29','2025-03-22 12:06:29'),
	(14,100123,'09150517','MQ','topic.team_success',NULL,1,1,'{\"teamId\":\"09150517\",\"outTradeNoList\":[\"753636951899\"]}','2025-03-29 10:06:53','2025-03-29 10:06:54'),
	(15,100123,'22810944','MQ','topic.team_success',NULL,1,1,'{\"teamId\":\"22810944\",\"outTradeNoList\":[\"147604406597\"]}','2025-03-29 10:10:17','2025-03-29 10:10:17'),
	(16,100123,'98209369','MQ','topic.team_success',NULL,1,1,'{\"teamId\":\"98209369\",\"outTradeNoList\":[\"537455032540\"]}','2025-03-29 10:30:35','2025-03-29 10:30:35'),
	(17,100123,'45338842','MQ','topic.team_success',NULL,1,1,'{\"teamId\":\"45338842\",\"outTradeNoList\":[\"183852291730\"]}','2025-03-29 12:33:28','2025-03-29 12:33:29'),
	(18,100123,'72304503','MQ','topic.team_success',NULL,1,1,'{\"teamId\":\"72304503\",\"outTradeNoList\":[\"303596099292\"]}','2025-03-29 13:07:41','2025-03-29 13:07:41'),
	(19,100123,'14639651','MQ','topic.team_success',NULL,1,1,'{\"teamId\":\"14639651\",\"outTradeNoList\":[\"928263928388\"]}','2025-03-29 13:10:27','2025-03-29 13:10:27'),

/*!40000 ALTER TABLE `notify_task` ENABLE KEYS */;
UNLOCK TABLES;


# 杞偍琛?sc_sku_activity
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sc_sku_activity`;

CREATE TABLE `sc_sku_activity` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `source` varchar(8) NOT NULL COMMENT '娓犻亾',
  `channel` varchar(8) NOT NULL COMMENT '鏉ユ簮',
  `activity_id` bigint NOT NULL COMMENT '娲诲姩ID',
  `goods_id` varchar(16) NOT NULL COMMENT '鍟嗗搧ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_sc_goodsid` (`source`,`channel`,`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='娓犻亾鍟嗗搧娲诲姩閰嶇疆鍏宠仈琛?;

LOCK TABLES `sc_sku_activity` WRITE;
/*!40000 ALTER TABLE `sc_sku_activity` DISABLE KEYS */;

INSERT INTO `sc_sku_activity` (`id`, `source`, `channel`, `activity_id`, `goods_id`, `create_time`, `update_time`)
VALUES
	(1,'s01','c01',100123,'9890001','2025-01-01 13:15:54','2025-01-01 13:15:54');

/*!40000 ALTER TABLE `sc_sku_activity` ENABLE KEYS */;
UNLOCK TABLES;


# 杞偍琛?sku
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sku`;

CREATE TABLE `sku` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `source` varchar(8) NOT NULL COMMENT '娓犻亾',
  `channel` varchar(8) NOT NULL COMMENT '鏉ユ簮',
  `goods_id` varchar(16) NOT NULL COMMENT '鍟嗗搧ID',
  `goods_name` varchar(128) NOT NULL COMMENT '鍟嗗搧鍚嶇О',
  `original_price` decimal(10,2) NOT NULL COMMENT '鍟嗗搧浠锋牸',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍟嗗搧淇℃伅';

LOCK TABLES `sku` WRITE;
/*!40000 ALTER TABLE `sku` DISABLE KEYS */;

INSERT INTO `sku` (`id`, `source`, `channel`, `goods_id`, `goods_name`, `original_price`, `create_time`, `update_time`)
VALUES
	(1,'s01','c01','9890001','銆婃墜鍐橫yBatis锛氭笎杩涘紡婧愮爜瀹炶返銆?,100.00,'2024-12-21 11:10:06','2024-12-21 11:10:06');

/*!40000 ALTER TABLE `sku` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
