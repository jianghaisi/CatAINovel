# ************************************************************
# Sequel Ace SQL dump
# 鐗堟湰鍙凤細 20050
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# 涓绘満: 127.0.0.1 (MySQL 5.6.39)
# 鏁版嵁搴? group_buy_market
# 鐢熸垚鏃堕棿: 2025-02-03 03:08:22 +0000
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
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `tag_id` varchar(32) NOT NULL COMMENT '浜虹兢ID',
  `tag_name` varchar(64) NOT NULL COMMENT '浜虹兢鍚嶇О',
  `tag_desc` varchar(256) NOT NULL COMMENT '浜虹兢鎻忚堪',
  `statistics` int(8) NOT NULL COMMENT '浜虹兢鏍囩缁熻閲?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浜虹兢鏍囩';

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
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `tag_id` varchar(32) NOT NULL COMMENT '浜虹兢ID',
  `user_id` varchar(16) NOT NULL COMMENT '鐢ㄦ埛ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tag_user` (`tag_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浜虹兢鏍囩鏄庣粏';

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
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浜虹兢鏍囩浠诲姟';

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
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷',
  `activity_id` bigint(8) NOT NULL COMMENT '娲诲姩ID',
  `activity_name` varchar(128) NOT NULL COMMENT '娲诲姩鍚嶇О',
  `discount_id` varchar(8) NOT NULL COMMENT '鎶樻墸ID',
  `group_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '鎷煎洟鏂瑰紡锛?鑷姩鎴愬洟銆?杈炬垚鐩爣鎷煎洟锛?,
  `take_limit_count` int(4) NOT NULL DEFAULT '1' COMMENT '鎷煎洟娆℃暟闄愬埗',
  `target` int(5) NOT NULL DEFAULT '1' COMMENT '鎷煎洟鐩爣',
  `valid_time` int(4) NOT NULL DEFAULT '15' COMMENT '鎷煎洟鏃堕暱锛堝垎閽燂級',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '娲诲姩鐘舵€侊紙0鍒涘缓銆?鐢熸晥銆?杩囨湡銆?搴熷純锛?,
  `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '娲诲姩寮€濮嬫椂闂?,
  `end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '娲诲姩缁撴潫鏃堕棿',
  `tag_id` varchar(32) DEFAULT NULL COMMENT '浜虹兢鏍囩瑙勫垯鏍囪瘑',
  `tag_scope` varchar(4) DEFAULT NULL COMMENT '浜虹兢鏍囩瑙勫垯鑼冨洿锛堝閫夛紱1鍙闄愬埗銆?鍙備笌闄愬埗锛?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_activity_id` (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鎷煎洟娲诲姩';

LOCK TABLES `group_buy_activity` WRITE;
/*!40000 ALTER TABLE `group_buy_activity` DISABLE KEYS */;

INSERT INTO `group_buy_activity` (`id`, `activity_id`, `activity_name`, `discount_id`, `group_type`, `take_limit_count`, `target`, `valid_time`, `status`, `start_time`, `end_time`, `tag_id`, `tag_scope`, `create_time`, `update_time`)
VALUES
	(1,100123,'娴嬭瘯娲诲姩','25120208',0,1,3,600,1,'2024-12-07 10:19:40','2026-12-07 10:19:40','RQ_KJHKL98UU78H66554GFDV','1,2','2024-12-07 10:19:40','2025-02-02 17:07:12');

/*!40000 ALTER TABLE `group_buy_activity` ENABLE KEYS */;
UNLOCK TABLES;


# 杞偍琛?group_buy_discount
# ------------------------------------------------------------

DROP TABLE IF EXISTS `group_buy_discount`;

CREATE TABLE `group_buy_discount` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `discount_id` varchar(8) NOT NULL COMMENT '鎶樻墸ID',
  `discount_name` varchar(64) NOT NULL COMMENT '鎶樻墸鏍囬',
  `discount_desc` varchar(256) NOT NULL COMMENT '鎶樻墸鎻忚堪',
  `discount_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '鎶樻墸绫诲瀷锛?:base銆?:tag锛?,
  `market_plan` varchar(4) NOT NULL DEFAULT 'ZJ' COMMENT '钀ラ攢浼樻儬璁″垝锛圸J:鐩村噺銆丮J:婊″噺銆乑K:鎶樻墸銆丯鍏冭喘锛?,
  `market_expr` varchar(32) NOT NULL COMMENT '钀ラ攢浼樻儬琛ㄨ揪寮?,
  `tag_id` varchar(32) DEFAULT NULL COMMENT '浜虹兢鏍囩锛岀壒瀹氫紭鎯犻檺瀹?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_discount_id` (`discount_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `group_buy_discount` WRITE;
/*!40000 ALTER TABLE `group_buy_discount` DISABLE KEYS */;

INSERT INTO `group_buy_discount` (`id`, `discount_id`, `discount_name`, `discount_desc`, `discount_type`, `market_plan`, `market_expr`, `tag_id`, `create_time`, `update_time`)
VALUES
	(1,'25120207','鐩村噺浼樻儬20鍏?,'鐩村噺浼樻儬20鍏?,0,'ZJ','20',NULL,'2024-12-07 10:20:15','2024-12-22 12:09:45'),
	(2,'25120208','婊″噺浼樻儬100-10鍏?,'婊″噺浼樻儬100-10鍏?,0,'MJ','100,10',NULL,'2024-12-07 10:20:15','2024-12-22 12:09:47'),
	(4,'25120209','鎶樻墸浼樻儬8鎶?,'鎶樻墸浼樻儬8鎶?,0,'ZK','0.8',NULL,'2024-12-07 10:20:15','2024-12-22 12:11:36'),
	(5,'25120210','N鍏冭喘涔颁紭鎯?,'N鍏冭喘涔颁紭鎯?,0,'N','1.99',NULL,'2024-12-07 10:20:15','2024-12-22 12:11:39');

/*!40000 ALTER TABLE `group_buy_discount` ENABLE KEYS */;
UNLOCK TABLES;


# 杞偍琛?group_buy_order
# ------------------------------------------------------------

DROP TABLE IF EXISTS `group_buy_order`;

CREATE TABLE `group_buy_order` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `team_id` varchar(8) NOT NULL COMMENT '鎷煎崟缁勯槦ID',
  `activity_id` bigint(8) NOT NULL COMMENT '娲诲姩ID',
  `source` varchar(8) NOT NULL COMMENT '娓犻亾',
  `channel` varchar(8) NOT NULL COMMENT '鏉ユ簮',
  `original_price` decimal(8,2) NOT NULL COMMENT '鍘熷浠锋牸',
  `deduction_price` decimal(8,2) NOT NULL COMMENT '鎶樻墸閲戦',
  `pay_price` decimal(8,2) NOT NULL COMMENT '鏀粯浠锋牸',
  `target_count` int(5) NOT NULL COMMENT '鐩爣鏁伴噺',
  `complete_count` int(5) NOT NULL COMMENT '瀹屾垚鏁伴噺',
  `lock_count` int(5) NOT NULL COMMENT '閿佸崟鏁伴噺',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '鐘舵€侊紙0-鎷煎崟涓€?-瀹屾垚銆?-澶辫触锛?,
  `valid_start_time` datetime NOT NULL COMMENT '鎷煎洟寮€濮嬫椂闂?,
  `valid_end_time` datetime NOT NULL COMMENT '鎷煎洟缁撴潫鏃堕棿',
  `notify_url` varchar(512) NOT NULL COMMENT '鍥炶皟鍦板潃',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_team_id` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `group_buy_order` WRITE;
/*!40000 ALTER TABLE `group_buy_order` DISABLE KEYS */;

INSERT INTO `group_buy_order` (`id`, `team_id`, `activity_id`, `source`, `channel`, `original_price`, `deduction_price`, `pay_price`, `target_count`, `complete_count`, `lock_count`, `status`, `valid_start_time`, `valid_end_time`, `notify_url`, `create_time`, `update_time`)
VALUES
	(3,'80759049',100123,'s01','c01',100.00,10.00,90.00,3,3,3,1,'2025-01-31 17:28:19','2025-01-31 18:28:19','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-01-31 17:28:19','2025-01-31 17:51:38'),
	(4,'64151034',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-01 09:58:27','2025-02-01 10:58:27','http://127.0.0.1:8091/api/tewst/group_buy_notify','2025-02-01 09:58:26','2025-02-01 09:58:26'),
	(5,'06966349',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-01 09:59:43','2025-02-01 10:59:43','http://127.0.0.1:8091/api/tewst/group_buy_notify','2025-02-01 09:59:43','2025-02-01 09:59:43'),
	(6,'58773266',100123,'s01','c01',100.00,10.00,90.00,3,3,3,1,'2025-02-01 10:00:48','2025-02-01 11:00:48','http://127.0.0.1:8091/api/tewst/group_buy_notify','2025-02-01 10:00:48','2025-02-01 10:03:23'),
	(7,'90828077',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-02 15:50:32','2025-02-02 16:50:32','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 15:50:32','2025-02-02 15:50:32'),
	(8,'56568426',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-02 15:50:57','2025-02-02 16:50:57','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 15:50:57','2025-02-02 15:50:57'),
	(9,'83992827',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-02 15:52:01','2025-02-02 16:52:01','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 15:52:01','2025-02-02 15:52:01'),
	(10,'55757690',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-02 15:52:17','2025-02-02 16:52:17','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 15:52:17','2025-02-02 15:52:17'),
	(11,'44597681',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-02 15:52:34','2025-02-02 16:52:34','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 15:52:33','2025-02-02 15:52:33'),
	(12,'90490076',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-02 15:52:50','2025-02-02 16:52:50','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 15:52:49','2025-02-02 15:52:49'),
	(13,'35697265',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-02 17:08:21','2025-02-03 03:08:21','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 17:08:21','2025-02-02 17:08:21'),
	(14,'09890613',100123,'s01','c01',100.00,10.00,90.00,3,1,1,0,'2025-02-02 17:08:21','2025-02-03 03:08:21','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 17:08:21','2025-02-02 18:55:52'),
	(15,'04607169',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-02 17:08:21','2025-02-03 03:08:21','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 17:08:21','2025-02-02 17:08:21'),
	(16,'19622463',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-02 17:08:21','2025-02-03 03:08:21','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 17:08:21','2025-02-02 17:08:21'),
	(17,'56373577',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-02 17:08:21','2025-02-03 03:08:21','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 17:08:21','2025-02-02 17:08:21'),
	(18,'88841975',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-02 17:08:21','2025-02-03 03:08:21','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 17:08:21','2025-02-02 17:08:21'),
	(19,'67227224',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-02 17:08:21','2025-02-03 03:08:21','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 17:08:21','2025-02-02 17:08:21'),
	(20,'60316215',100123,'s01','c01',100.00,10.00,90.00,3,0,2,0,'2025-02-02 17:08:21','2025-02-03 03:08:21','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 17:08:21','2025-02-02 17:58:59'),
	(21,'54868631',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-02 17:08:21','2025-02-03 03:08:21','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 17:08:21','2025-02-02 17:08:21'),
	(22,'37426875',100123,'s01','c01',100.00,10.00,90.00,3,0,1,0,'2025-02-02 17:13:39','2025-02-03 03:13:39','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 17:13:39','2025-02-02 17:13:39'),
	(23,'24552977',100123,'s01','c01',100.00,10.00,90.00,3,3,3,1,'2025-02-02 18:21:28','2025-02-03 04:21:28','http://127.0.0.1:8091/api/v1/test/group_buy_notify','2025-02-02 18:21:27','2025-02-02 18:57:25');

/*!40000 ALTER TABLE `group_buy_order` ENABLE KEYS */;
UNLOCK TABLES;


# 杞偍琛?group_buy_order_list
# ------------------------------------------------------------

DROP TABLE IF EXISTS `group_buy_order_list`;

CREATE TABLE `group_buy_order_list` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `user_id` varchar(64) NOT NULL COMMENT '鐢ㄦ埛ID',
  `team_id` varchar(8) NOT NULL COMMENT '鎷煎崟缁勯槦ID',
  `order_id` varchar(12) NOT NULL COMMENT '璁㈠崟ID',
  `activity_id` bigint(8) NOT NULL COMMENT '娲诲姩ID',
  `start_time` datetime NOT NULL COMMENT '娲诲姩寮€濮嬫椂闂?,
  `end_time` datetime NOT NULL COMMENT '娲诲姩缁撴潫鏃堕棿',
  `goods_id` varchar(16) NOT NULL COMMENT '鍟嗗搧ID',
  `source` varchar(8) NOT NULL COMMENT '娓犻亾',
  `channel` varchar(8) NOT NULL COMMENT '鏉ユ簮',
  `original_price` decimal(8,2) NOT NULL COMMENT '鍘熷浠锋牸',
  `deduction_price` decimal(8,2) NOT NULL COMMENT '鎶樻墸閲戦',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '鐘舵€侊紱0鍒濆閿佸畾銆?娑堣垂瀹屾垚銆?鐢ㄦ埛閫€鍗?,
  `out_trade_no` varchar(12) NOT NULL COMMENT '澶栭儴浜ゆ槗鍗曞彿-纭繚澶栭儴璋冪敤鍞竴骞傜瓑',
  `out_trade_time` datetime DEFAULT NULL COMMENT '澶栭儴浜ゆ槗鏃堕棿',
  `biz_id` varchar(64) NOT NULL COMMENT '涓氬姟鍞竴ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  KEY `idx_user_id_activity_id` (`user_id`,`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `group_buy_order_list` WRITE;
/*!40000 ALTER TABLE `group_buy_order_list` DISABLE KEYS */;

INSERT INTO `group_buy_order_list` (`id`, `user_id`, `team_id`, `order_id`, `activity_id`, `start_time`, `end_time`, `goods_id`, `source`, `channel`, `original_price`, `deduction_price`, `status`, `out_trade_no`, `out_trade_time`, `biz_id`, `create_time`, `update_time`)
VALUES
	(1,'xfg01','35697265','949146494634',100123,'2024-12-07 10:19:40','2026-12-07 10:19:40','9890001','s01','c01',100.00,10.00,0,'541992556240',NULL,'100123_xfg01_1','2025-02-02 17:08:21','2025-02-02 17:08:21'),
	(2,'xfg02','09890613','686042630691',100123,'2024-12-07 10:19:40','2026-12-07 10:19:40','9890001','s01','c01',100.00,10.00,1,'178478899445','2025-02-02 18:55:52','100123_xfg02_1','2025-02-02 17:08:21','2025-02-02 18:55:52'),
	(3,'xfg03','04607169','944044790496',100123,'2024-12-07 10:19:40','2026-12-07 11:19:40','9890001','s01','c01',100.00,10.00,0,'121206947656',NULL,'100123_xfg03_1','2025-02-02 17:08:21','2025-02-02 17:10:04'),
	(4,'xfg04','19622463','868831086487',100123,'2024-12-07 10:19:40','2026-12-07 10:25:40','9890001','s01','c01',100.00,10.00,0,'017542787135',NULL,'100123_xfg04_1','2025-02-02 17:08:21','2025-02-02 17:10:35'),
	(5,'xfg05','56373577','903766977951',100123,'2024-12-07 10:19:40','2026-12-07 10:19:40','9890001','s01','c01',100.00,10.00,0,'837411187306',NULL,'100123_xfg05_1','2025-02-02 17:08:21','2025-02-02 17:08:21'),
	(6,'xfg06','88841975','063807986187',100123,'2024-12-07 10:19:40','2026-12-07 08:19:40','9890001','s01','c01',100.00,10.00,0,'857009972534',NULL,'100123_xfg06_1','2025-02-02 17:08:21','2025-02-02 17:10:11'),
	(7,'xfg07','67227224','637895723838',100123,'2024-12-07 10:19:40','2026-12-07 10:19:40','9890001','s01','c01',100.00,10.00,0,'084833045305',NULL,'100123_xfg07_1','2025-02-02 17:08:21','2025-02-02 17:08:21'),
	(12,'jiang','24552977','349119248610',100123,'2024-12-07 10:19:40','2026-12-07 10:19:40','9890001','s01','c01',100.00,10.00,1,'330015345410','2025-02-02 18:33:45','100123_jiang_1','2025-02-02 18:21:27','2025-02-02 18:33:48'),
	(13,'xfg09','24552977','422517160235',100123,'2024-12-07 10:19:40','2026-12-07 10:19:40','9890001','s01','c01',100.00,10.00,1,'967832705657','2025-02-02 18:56:53','100123_xfg09_1','2025-02-02 18:56:51','2025-02-02 18:56:52'),
	(14,'xfg08','24552977','021061078912',100123,'2024-12-07 10:19:40','2026-12-07 10:19:40','9890001','s01','c01',100.00,10.00,1,'396963466950','2025-02-02 18:57:26','100123_xfg08_1','2025-02-02 18:57:25','2025-02-02 18:57:25');

/*!40000 ALTER TABLE `group_buy_order_list` ENABLE KEYS */;
UNLOCK TABLES;


# 杞偍琛?notify_task
# ------------------------------------------------------------

DROP TABLE IF EXISTS `notify_task`;

CREATE TABLE `notify_task` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `activity_id` bigint(8) NOT NULL COMMENT '娲诲姩ID',
  `team_id` varchar(8) NOT NULL COMMENT '鎷煎崟缁勯槦ID',
  `notify_url` varchar(128) NOT NULL COMMENT '鍥炶皟鎺ュ彛',
  `notify_count` int(8) NOT NULL COMMENT '鍥炶皟娆℃暟',
  `notify_status` tinyint(1) NOT NULL COMMENT '鍥炶皟鐘舵€併€?鍒濆銆?瀹屾垚銆?閲嶈瘯銆?澶辫触銆?,
  `parameter_json` varchar(256) NOT NULL COMMENT '鍙傛暟瀵硅薄',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_team_id` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `notify_task` WRITE;
/*!40000 ALTER TABLE `notify_task` DISABLE KEYS */;

INSERT INTO `notify_task` (`id`, `activity_id`, `team_id`, `notify_url`, `notify_count`, `notify_status`, `parameter_json`, `create_time`, `update_time`)
VALUES
	(1,100123,'46832479','鏆傛棤',1,1,'{\"teamId\":\"46832479\",\"outTradeNoList\":[\"581909866926\",\"155123092895\",\"451517755304\"]}','2025-01-26 19:11:46','2025-01-31 17:21:30'),
	(2,100123,'38795123','鏆傛棤',1,1,'{\"teamId\":\"38795123\",\"outTradeNoList\":[\"134597814295\",\"154310924273\",\"228984300880\"]}','2025-01-28 08:27:26','2025-01-31 17:21:30'),
	(3,100123,'57199993','鏆傛棤',1,1,'{\"teamId\":\"57199993\",\"outTradeNoList\":[\"038426231487\",\"652896391719\",\"619401409195\"]}','2025-01-28 09:13:00','2025-01-31 17:21:30'),
	(9,100123,'80759049','http://127.0.0.1:8091/api/v1/test/group_buy_notify',1,1,'{\"teamId\":\"80759049\",\"outTradeNoList\":[\"555024425070\",\"812787347025\",\"536311764349\"]}','2025-01-31 17:51:39','2025-01-31 17:52:10'),
	(10,100123,'58773266','http://127.0.0.1:8091/api/v1/test/group_buy_notify',1,1,'{\"teamId\":\"58773266\",\"outTradeNoList\":[\"442660948430\",\"134346482148\",\"400764981631\"]}','2025-02-01 10:03:24','2025-02-01 10:05:15'),
	(11,100123,'24552977','http://127.0.0.1:8091/api/v1/test/group_buy_notify',1,1,'{\"teamId\":\"24552977\",\"outTradeNoList\":[\"330015345410\",\"967832705657\",\"396963466950\"]}','2025-02-02 18:57:26','2025-02-02 18:57:26');

/*!40000 ALTER TABLE `notify_task` ENABLE KEYS */;
UNLOCK TABLES;


# 杞偍琛?sc_sku_activity
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sc_sku_activity`;

CREATE TABLE `sc_sku_activity` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `source` varchar(8) NOT NULL COMMENT '娓犻亾',
  `channel` varchar(8) NOT NULL COMMENT '鏉ユ簮',
  `activity_id` bigint(8) NOT NULL COMMENT '娲诲姩ID',
  `goods_id` varchar(16) NOT NULL COMMENT '鍟嗗搧ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_sc_goodsid` (`source`,`channel`,`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='娓犻亾鍟嗗搧娲诲姩閰嶇疆鍏宠仈琛?;

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
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `source` varchar(8) NOT NULL COMMENT '娓犻亾',
  `channel` varchar(8) NOT NULL COMMENT '鏉ユ簮',
  `goods_id` varchar(16) NOT NULL COMMENT '鍟嗗搧ID',
  `goods_name` varchar(128) NOT NULL COMMENT '鍟嗗搧鍚嶇О',
  `original_price` decimal(10,2) NOT NULL COMMENT '鍟嗗搧浠锋牸',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鍟嗗搧淇℃伅';

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
