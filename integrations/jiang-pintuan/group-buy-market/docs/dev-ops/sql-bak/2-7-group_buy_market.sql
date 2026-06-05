# ************************************************************
# Sequel Ace SQL dump
# 鐗堟湰鍙凤細 20050
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# 涓绘満: 127.0.0.1 (MySQL 5.6.39)
# 鏁版嵁搴? group_buy_market
# 鐢熸垚鏃堕棿: 2025-01-02 02:30:35 +0000
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
	(1,'RQ_KJHKL98UU78H66554GFDV','娼滃湪娑堣垂鐢ㄦ埛','娼滃湪娑堣垂鐢ㄦ埛',6,'2024-12-28 12:53:28','2024-12-28 16:58:45');

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
	(5,'RQ_KJHKL98UU78H66554GFDV','liergou','2024-12-28 14:42:30','2024-12-28 14:42:30');

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
	(1,100123,'娴嬭瘯娲诲姩','25120208',0,1,1,15,1,'2024-12-07 10:19:40','2024-12-07 10:19:40','1','1','2024-12-07 10:19:40','2025-01-01 18:27:47');

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
