CREATE DATABASE IF NOT EXISTS `novel_agent` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `s-pay-mall-ddd-market` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `s-pay-mall-ddd-market`;

CREATE TABLE IF NOT EXISTS `pay_order` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷ID',
  `user_id` varchar(64) NOT NULL COMMENT '鐢ㄦ埛ID',
  `product_id` varchar(64) NOT NULL COMMENT '鍟嗗搧ID',
  `product_name` varchar(64) NOT NULL COMMENT '鍟嗗搧鍚嶇О',
  `order_id` varchar(32) NOT NULL COMMENT '璁㈠崟ID',
  `order_time` datetime NOT NULL COMMENT '涓嬪崟鏃堕棿',
  `total_amount` decimal(8,2) unsigned DEFAULT NULL COMMENT '璁㈠崟閲戦',
  `status` varchar(32) NOT NULL COMMENT '璁㈠崟鐘舵€?,
  `pay_url` varchar(4096) DEFAULT NULL COMMENT '鏀粯淇℃伅',
  `pay_time` datetime DEFAULT NULL COMMENT '鏀粯鏃堕棿',
  `market_type` tinyint(1) DEFAULT NULL COMMENT '钀ラ攢绫诲瀷',
  `market_deduction_amount` decimal(8,2) DEFAULT NULL COMMENT '钀ラ攢浼樻儬閲戦',
  `pay_amount` decimal(8,2) NOT NULL COMMENT '瀹為檯鏀粯閲戦',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  KEY `idx_user_id_product_id` (`user_id`,`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
