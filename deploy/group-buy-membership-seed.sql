USE `novel_agent`;
INSERT INTO `users`
  (`id`, `email`, `password_hash`, `name`, `provider`, `role`, `status`, `created_at`, `updated_at`)
VALUES
  ('demo-pro-1-1', 'demo-pro-1-1@example.local', NULL, 'Avery Lane', 'seed', 'user', 'active', NOW(), NOW()),
  ('demo-pro-1-2', 'demo-pro-1-2@example.local', NULL, 'Blake Reed', 'seed', 'user', 'active', NOW(), NOW()),
  ('demo-pro-2-1', 'demo-pro-2-1@example.local', NULL, 'Casey North', 'seed', 'user', 'active', NOW(), NOW()),
  ('demo-pro-3-1', 'demo-pro-3-1@example.local', NULL, 'Drew Vale', 'seed', 'user', 'active', NOW(), NOW()),
  ('demo-pro-3-2', 'demo-pro-3-2@example.local', NULL, 'Emery Frost', 'seed', 'user', 'active', NOW(), NOW()),
  ('demo-pro-4-1', 'demo-pro-4-1@example.local', NULL, 'Finch Gray', 'seed', 'user', 'active', NOW(), NOW()),
  ('demo-plus-1-1', 'demo-plus-1-1@example.local', NULL, 'Harper Stone', 'seed', 'user', 'active', NOW(), NOW()),
  ('demo-plus-1-2', 'demo-plus-1-2@example.local', NULL, 'Indigo Park', 'seed', 'user', 'active', NOW(), NOW()),
  ('demo-plus-2-1', 'demo-plus-2-1@example.local', NULL, 'Jules River', 'seed', 'user', 'active', NOW(), NOW()),
  ('demo-plus-3-1', 'demo-plus-3-1@example.local', NULL, 'Kieran Fox', 'seed', 'user', 'active', NOW(), NOW()),
  ('demo-plus-3-2', 'demo-plus-3-2@example.local', NULL, 'Logan Field', 'seed', 'user', 'active', NOW(), NOW()),
  ('demo-max-1-1', 'demo-max-1-1@example.local', NULL, 'Morgan Lake', 'seed', 'user', 'active', NOW(), NOW()),
  ('demo-max-1-2', 'demo-max-1-2@example.local', NULL, 'Nova Quinn', 'seed', 'user', 'active', NOW(), NOW()),
  ('demo-max-2-1', 'demo-max-2-1@example.local', NULL, 'Orion Hale', 'seed', 'user', 'active', NOW(), NOW())
ON DUPLICATE KEY UPDATE `email`=VALUES(`email`), `name`=VALUES(`name`), `provider`=VALUES(`provider`), `updated_at`=NOW();

USE `group_buy_market`;

INSERT INTO `group_buy_discount`
  (`discount_id`, `discount_name`, `discount_desc`, `discount_type`, `market_plan`, `market_expr`, `tag_id`, `create_time`, `update_time`)
VALUES
  ('25120207', 'Membership Group Buy 20 Percent Off', 'Kitten Writing membership group-buy discount strategy: 20 percent off', 0, 'ZK', '0.8', NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  `discount_name`=VALUES(`discount_name`), `discount_desc`=VALUES(`discount_desc`),
  `discount_type`=VALUES(`discount_type`), `market_plan`=VALUES(`market_plan`),
  `market_expr`=VALUES(`market_expr`), `update_time`=NOW();
INSERT INTO `group_buy_activity`
  (`activity_id`, `activity_name`, `discount_id`, `group_type`, `take_limit_count`, `target`, `valid_time`, `status`, `start_time`, `end_time`, `tag_id`, `tag_scope`, `create_time`, `update_time`)
VALUES
  (100123, 'Kitten Writing Pro Group Buy', '25120207', 0, 99, 3, 5256000, 1, '2024-01-01 00:00:00', '2029-12-31 23:59:59', NULL, NULL, NOW(), NOW()),
  (100124, 'Kitten Writing Plus Group Buy', '25120207', 0, 99, 3, 5256000, 1, '2024-01-01 00:00:00', '2029-12-31 23:59:59', NULL, NULL, NOW(), NOW()),
  (100125, 'Kitten Writing Max Group Buy', '25120207', 0, 99, 3, 5256000, 1, '2024-01-01 00:00:00', '2029-12-31 23:59:59', NULL, NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE `activity_name`=VALUES(`activity_name`), `discount_id`=VALUES(`discount_id`), `take_limit_count`=VALUES(`take_limit_count`), `target`=VALUES(`target`), `valid_time`=VALUES(`valid_time`), `status`=VALUES(`status`), `start_time`=VALUES(`start_time`), `end_time`=VALUES(`end_time`), `update_time`=NOW();
INSERT INTO `sku` (`source`, `channel`, `goods_id`, `goods_name`, `original_price`, `create_time`, `update_time`)
VALUES
  ('s01', 'c01', 'member-pro', 'Kitten Writing Pro Membership', 19.00, NOW(), NOW()),
  ('s01', 'c01', 'member-plus', 'Kitten Writing Plus Membership', 49.00, NOW(), NOW()),
  ('s01', 'c01', 'member-max', 'Kitten Writing Max Membership', 99.00, NOW(), NOW())
ON DUPLICATE KEY UPDATE `goods_name`=VALUES(`goods_name`), `original_price`=VALUES(`original_price`), `update_time`=NOW();
DELETE FROM `sc_sku_activity` WHERE `goods_id` IN ('member-pro','member-plus','member-max');
INSERT INTO `sc_sku_activity` (`source`, `channel`, `activity_id`, `goods_id`, `create_time`, `update_time`) VALUES
  ('s01', 'c01', 100123, 'member-pro', NOW(), NOW()),
  ('s01', 'c01', 100124, 'member-plus', NOW(), NOW()),
  ('s01', 'c01', 100125, 'member-max', NOW(), NOW());
DELETE FROM `group_buy_order_list` WHERE `team_id` BETWEEN '12000000' AND '12000099' OR `user_id` LIKE 'demo-pro-%' OR `user_id` LIKE 'demo-plus-%' OR `user_id` LIKE 'demo-max-%';
DELETE FROM `group_buy_order` WHERE `team_id` BETWEEN '12000000' AND '12000099';
INSERT INTO `group_buy_order`
  (`team_id`, `activity_id`, `source`, `channel`, `original_price`, `deduction_price`, `pay_price`, `target_count`, `complete_count`, `lock_count`, `status`, `valid_start_time`, `valid_end_time`, `notify_type`, `notify_url`, `create_time`, `update_time`)
VALUES
  ('12000000', 100123, 's01', 'c01', 19.00, 3.80, 15.20, 3, 2, 2, 0, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'MQ', NULL, DATE_SUB(NOW(), INTERVAL 1 MINUTE), NOW()),
  ('12000001', 100123, 's01', 'c01', 19.00, 3.80, 15.20, 3, 1, 1, 0, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'MQ', NULL, DATE_SUB(NOW(), INTERVAL 2 MINUTE), NOW()),
  ('12000002', 100123, 's01', 'c01', 19.00, 3.80, 15.20, 3, 2, 2, 0, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'MQ', NULL, DATE_SUB(NOW(), INTERVAL 3 MINUTE), NOW()),
  ('12000003', 100123, 's01', 'c01', 19.00, 3.80, 15.20, 3, 1, 1, 0, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'MQ', NULL, DATE_SUB(NOW(), INTERVAL 4 MINUTE), NOW()),
  ('12000004', 100124, 's01', 'c01', 49.00, 9.80, 39.20, 3, 2, 2, 0, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'MQ', NULL, DATE_SUB(NOW(), INTERVAL 5 MINUTE), NOW()),
  ('12000005', 100124, 's01', 'c01', 49.00, 9.80, 39.20, 3, 1, 1, 0, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'MQ', NULL, DATE_SUB(NOW(), INTERVAL 6 MINUTE), NOW()),
  ('12000006', 100124, 's01', 'c01', 49.00, 9.80, 39.20, 3, 2, 2, 0, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'MQ', NULL, DATE_SUB(NOW(), INTERVAL 7 MINUTE), NOW()),
  ('12000007', 100125, 's01', 'c01', 99.00, 19.80, 79.20, 3, 2, 2, 0, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'MQ', NULL, DATE_SUB(NOW(), INTERVAL 8 MINUTE), NOW()),
  ('12000008', 100125, 's01', 'c01', 99.00, 19.80, 79.20, 3, 1, 1, 0, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'MQ', NULL, DATE_SUB(NOW(), INTERVAL 9 MINUTE), NOW());
INSERT INTO `group_buy_order_list`
  (`user_id`, `team_id`, `order_id`, `activity_id`, `start_time`, `end_time`, `goods_id`, `source`, `channel`, `original_price`, `deduction_price`, `pay_price`, `status`, `out_trade_no`, `out_trade_time`, `biz_id`, `create_time`, `update_time`)
VALUES
  ('demo-pro-1-1', '12000000', '220000000000', 100123, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'member-pro', 's01', 'c01', 19.00, 3.80, 15.20, 1, '720000000000', NOW(), '100123_demo-pro-1-1_1', NOW(), NOW()),
  ('demo-pro-1-2', '12000000', '220000000001', 100123, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'member-pro', 's01', 'c01', 19.00, 3.80, 15.20, 1, '720000000001', NOW(), '100123_demo-pro-1-2_2', NOW(), NOW()),
  ('demo-pro-2-1', '12000001', '220000000002', 100123, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'member-pro', 's01', 'c01', 19.00, 3.80, 15.20, 1, '720000000002', NOW(), '100123_demo-pro-2-1_1', NOW(), NOW()),
  ('demo-pro-3-1', '12000002', '220000000003', 100123, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'member-pro', 's01', 'c01', 19.00, 3.80, 15.20, 1, '720000000003', NOW(), '100123_demo-pro-3-1_1', NOW(), NOW()),
  ('demo-pro-3-2', '12000002', '220000000004', 100123, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'member-pro', 's01', 'c01', 19.00, 3.80, 15.20, 1, '720000000004', NOW(), '100123_demo-pro-3-2_2', NOW(), NOW()),
  ('demo-pro-4-1', '12000003', '220000000005', 100123, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'member-pro', 's01', 'c01', 19.00, 3.80, 15.20, 1, '720000000005', NOW(), '100123_demo-pro-4-1_1', NOW(), NOW()),
  ('demo-plus-1-1', '12000004', '220000000006', 100124, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'member-plus', 's01', 'c01', 49.00, 9.80, 39.20, 1, '720000000006', NOW(), '100124_demo-plus-1-1_1', NOW(), NOW()),
  ('demo-plus-1-2', '12000004', '220000000007', 100124, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'member-plus', 's01', 'c01', 49.00, 9.80, 39.20, 1, '720000000007', NOW(), '100124_demo-plus-1-2_2', NOW(), NOW()),
  ('demo-plus-2-1', '12000005', '220000000008', 100124, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'member-plus', 's01', 'c01', 49.00, 9.80, 39.20, 1, '720000000008', NOW(), '100124_demo-plus-2-1_1', NOW(), NOW()),
  ('demo-plus-3-1', '12000006', '220000000009', 100124, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'member-plus', 's01', 'c01', 49.00, 9.80, 39.20, 1, '720000000009', NOW(), '100124_demo-plus-3-1_1', NOW(), NOW()),
  ('demo-plus-3-2', '12000006', '220000000010', 100124, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'member-plus', 's01', 'c01', 49.00, 9.80, 39.20, 1, '720000000010', NOW(), '100124_demo-plus-3-2_2', NOW(), NOW()),
  ('demo-max-1-1', '12000007', '220000000011', 100125, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'member-max', 's01', 'c01', 99.00, 19.80, 79.20, 1, '720000000011', NOW(), '100125_demo-max-1-1_1', NOW(), NOW()),
  ('demo-max-1-2', '12000007', '220000000012', 100125, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'member-max', 's01', 'c01', 99.00, 19.80, 79.20, 1, '720000000012', NOW(), '100125_demo-max-1-2_2', NOW(), NOW()),
  ('demo-max-2-1', '12000008', '220000000013', 100125, '2024-01-01 00:00:00', '2029-12-31 23:59:59', 'member-max', 's01', 'c01', 99.00, 19.80, 79.20, 1, '720000000013', NOW(), '100125_demo-max-2-1_1', NOW(), NOW());
