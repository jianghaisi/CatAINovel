$ErrorActionPreference = 'Stop'

$base = 'http://127.0.0.1:8080/api'
$stamp = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
$password = 'TestPass123!'
$teamId = ('' + ($stamp % 90000000 + 10000000)).Substring(0, 8)
$seedA = "qa-pay-seed-a-$stamp"
$seedB = "qa-pay-seed-b-$stamp"
$seedEmailA = "$seedA@example.local"
$seedEmailB = "$seedB@example.local"
$seedOrderA = ('' + (($stamp + 101) % 900000000000 + 100000000000)).Substring(0, 12)
$seedOrderB = ('' + (($stamp + 102) % 900000000000 + 100000000000)).Substring(0, 12)

function Assert-True($condition, $message) {
  if (-not $condition) {
    throw "ASSERT_FAILED: $message"
  }
  Write-Host "PASS: $message"
}

function MysqlExec($database, $sql) {
  docker compose --env-file deploy\docker.env -f deploy\docker-compose.full.yml exec -T mysql mysql --default-character-set=utf8mb4 -uroot -pnovel-root -N -B "--database=$database" -e $sql |
    Where-Object { $_ -and -not $_.StartsWith('mysql: [Warning]') }
}

function Register-TestUser($prefix, $name = '') {
  $email = "$prefix-$stamp@example.com"
  $codeResp = Invoke-RestMethod -Method Post -Uri "$base/auth/email/code" -ContentType 'application/json' -Body (@{
    email = $email
    purpose = 'register'
  } | ConvertTo-Json)
  $reg = Invoke-RestMethod -Method Post -Uri "$base/auth/email/register" -ContentType 'application/json' -Body (@{
    email = $email
    code = $codeResp.dev_code
    password = $password
    name = $name
  } | ConvertTo-Json)
  return [ordered]@{
    email = $email
    token = $reg.token
    user = $reg.user
    headers = @{ Authorization = "Bearer $($reg.token)" }
  }
}

Write-Host "PAYMENT_FLOW_TEST_START stamp=$stamp team=$teamId"

# Keep previous automated users out of the public recruiting list.
$testTeamRows = MysqlExec 'novel_agent' @"
SELECT DISTINCT l.team_id
FROM group_buy_market.group_buy_order_list l
JOIN users u ON CONVERT(u.id USING utf8mb4) COLLATE utf8mb4_0900_ai_ci = CONVERT(l.user_id USING utf8mb4) COLLATE utf8mb4_0900_ai_ci
WHERE u.email LIKE 'itest-%@example.com'
   OR u.email LIKE 'edge-%@example.com'
   OR u.email LIKE 'qa-pay-%@example.com'
   OR u.name LIKE 'GroupTest%';
"@
foreach ($row in @($testTeamRows)) {
  $oldTeamId = ([string]$row).Trim()
  if ($oldTeamId) {
    MysqlExec 'group_buy_market' "UPDATE group_buy_order SET status=1, update_time=NOW() WHERE team_id='$oldTeamId';" | Out-Null
  }
}

# Direct purchase: create an order, simulate the sandbox callback result in pay-mall, then confirm via backend.
$directUser = Register-TestUser 'qa-pay-direct' ''
$directOrderResp = Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $directUser.headers -ContentType 'application/json' -Body (@{
  plan_id = 'pro'
  team_id = ''
} | ConvertTo-Json)
$directOrder = $directOrderResp.order
Assert-True ($directOrder.purchase_mode -eq 'direct') 'direct payment order is created'
$directMarketType = MysqlExec 's-pay-mall-ddd-market' "SELECT market_type FROM pay_order WHERE order_id='$($directOrder.external_order_id)' AND user_id='$($directUser.user.id)';"
Assert-True ([int]([string]@($directMarketType)[0]) -eq 0) 'direct pay-mall order uses market_type 0'
$directGroupRows = MysqlExec 'group_buy_market' "SELECT COUNT(*) FROM group_buy_order_list WHERE out_trade_no='$($directOrder.external_order_id)' OR user_id='$($directUser.user.id)';"
Assert-True ([int]([string]@($directGroupRows)[0]) -eq 0) 'direct order does not create a group-buy row'

MysqlExec 's-pay-mall-ddd-market' "UPDATE pay_order SET status='PAY_SUCCESS', pay_time=NOW(), update_time=NOW() WHERE order_id='$($directOrder.external_order_id)' AND user_id='$($directUser.user.id)';" | Out-Null

$directConfirm = Invoke-RestMethod -Method Post -Uri "$base/membership/orders/confirm" -Headers $directUser.headers -ContentType 'application/json' -Body (@{
  order_id = $directOrder.order_id
} | ConvertTo-Json)
Assert-True ($directConfirm.active -eq $true) 'paid direct order activates membership'
Assert-True ($directConfirm.plan.id -eq 'pro') 'direct order activates the selected pro plan'

# Build a controlled 2/3 paid group. The third user joins through the real order API.
MysqlExec 'novel_agent' @"
INSERT INTO users (id,email,password_hash,name,provider,role,status,created_at,updated_at)
VALUES
  ('$seedA','$seedEmailA',NULL,'QaPaySeedA','seed','user','active',NOW(),NOW()),
  ('$seedB','$seedEmailB',NULL,'QaPaySeedB','seed','user','active',NOW(),NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name), updated_at=NOW();
"@ | Out-Null

MysqlExec 'group_buy_market' @"
DELETE FROM group_buy_order_list WHERE team_id='$teamId';
DELETE FROM group_buy_order WHERE team_id='$teamId';
INSERT INTO group_buy_order
  (team_id, activity_id, source, channel, original_price, deduction_price, pay_price, target_count, complete_count, lock_count, status, valid_start_time, valid_end_time, notify_type, notify_url, create_time, update_time)
VALUES
  ('$teamId', 100124, 's01', 'c01', 49.00, 9.80, 39.20, 3, 2, 2, 0, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 'MQ', NULL, NOW(), NOW());
INSERT INTO group_buy_order_list
  (user_id, team_id, order_id, activity_id, start_time, end_time, goods_id, source, channel, original_price, deduction_price, pay_price, status, out_trade_no, out_trade_time, biz_id, create_time, update_time)
VALUES
  ('$seedA', '$teamId', '$seedOrderA', 100124, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 'member-plus', 's01', 'c01', 49.00, 9.80, 39.20, 1, '$seedOrderA', NOW(), CONCAT('100124_', '$seedA', '_1'), NOW(), NOW()),
  ('$seedB', '$teamId', '$seedOrderB', 100124, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 'member-plus', 's01', 'c01', 49.00, 9.80, 39.20, 1, '$seedOrderB', NOW(), CONCAT('100124_', '$seedB', '_2'), NOW(), NOW());
"@ | Out-Null

$groupUser = Register-TestUser 'qa-pay-group' ''
$teamsBefore = Invoke-RestMethod -Method Get -Uri "$base/membership/group-teams" -Headers $groupUser.headers
$controlledBefore = @($teamsBefore.teams | Where-Object { $_.team_id -eq $teamId } | Select-Object -First 1)[0]
Assert-True ($controlledBefore -and [int]$controlledBefore.joined -eq 2 -and [int]$controlledBefore.remaining -eq 1) 'controlled test group is visible as 2/3 before payment'

$groupOrderResp = Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $groupUser.headers -ContentType 'application/json' -Body (@{
  plan_id = 'plus'
  team_id = $teamId
} | ConvertTo-Json)
$groupOrder = $groupOrderResp.order
Assert-True ($groupOrder.purchase_mode -eq 'group') 'group payment order is created'
Assert-True ($groupOrder.team_id -eq $teamId) 'group order uses the controlled test team'
$groupMarketType = MysqlExec 's-pay-mall-ddd-market' "SELECT market_type FROM pay_order WHERE order_id='$($groupOrder.external_order_id)' AND user_id='$($groupUser.user.id)';"
Assert-True ([int]([string]@($groupMarketType)[0]) -eq 1) 'group pay-mall order uses market_type 1'

MysqlExec 's-pay-mall-ddd-market' "UPDATE pay_order SET status='PAY_SUCCESS', pay_time=NOW(), update_time=NOW() WHERE order_id='$($groupOrder.external_order_id)' AND user_id='$($groupUser.user.id)';" | Out-Null

$groupConfirm = Invoke-RestMethod -Method Post -Uri "$base/membership/orders/confirm" -Headers $groupUser.headers -ContentType 'application/json' -Body (@{
  order_id = $groupOrder.order_id
} | ConvertTo-Json)

$settledMemberStatus = MysqlExec 'group_buy_market' "SELECT status FROM group_buy_order_list WHERE team_id='$teamId' AND out_trade_no='$($groupOrder.external_order_id)' AND user_id='$($groupUser.user.id)';"
Assert-True ([int]([string]@($settledMemberStatus)[0]) -eq 1) 'backend confirm settles paid group row through group-buy service'
$settledTeam = MysqlExec 'group_buy_market' "SELECT complete_count, lock_count, status FROM group_buy_order WHERE team_id='$teamId';"
$settledParts = ([string]@($settledTeam)[0]).Split("`t")
Assert-True ([int]$settledParts[0] -eq 3 -and [int]$settledParts[1] -eq 3 -and [int]$settledParts[2] -eq 1) 'group-buy service marks the team formed after third paid member'
Assert-True ($groupConfirm.active -eq $true) 'paid third group member activates after team forms'
Assert-True ($groupConfirm.plan.id -eq 'plus') 'formed group activates plus plan'

$teamsAfter = Invoke-RestMethod -Method Get -Uri "$base/membership/group-teams" -Headers $groupUser.headers
$controlledAfter = @($teamsAfter.teams | Where-Object { $_.team_id -eq $teamId } | Select-Object -First 1)
Assert-True ($controlledAfter.Count -eq 0) 'formed group disappears from recruiting list'

$seedMembershipRows = docker compose --env-file deploy\docker.env -f deploy\docker-compose.full.yml exec -T backend python -c "from services.membership_service import get_membership; import json; ids=['$seedA','$seedB','$($groupUser.user.id)']; print(json.dumps({i:get_membership(i) for i in ids}, ensure_ascii=False))"
$seedMembershipJson = ($seedMembershipRows | Select-Object -Last 1)
$seedMembership = $seedMembershipJson | ConvertFrom-Json
$seedAMembership = $seedMembership.PSObject.Properties[$seedA].Value
$seedBMembership = $seedMembership.PSObject.Properties[$seedB].Value
Assert-True ($seedAMembership.plan_id -eq 'plus') 'seed paid member A receives plus membership after formation'
Assert-True ($seedBMembership.plan_id -eq 'plus') 'seed paid member B receives plus membership after formation'

# Hide this controlled team after verification so it never pollutes the normal page.
MysqlExec 'group_buy_market' "UPDATE group_buy_order SET status=1, update_time=NOW() WHERE team_id='$teamId';" | Out-Null

[ordered]@{
  direct_user_id = $directUser.user.id
  direct_order = $directOrder.external_order_id
  group_user_id = $groupUser.user.id
  group_order = $groupOrder.external_order_id
  group_team_id = $teamId
  seed_a = $seedA
  seed_b = $seedB
  direct_plan = $directConfirm.plan.id
  group_plan = $groupConfirm.plan.id
} | ConvertTo-Json -Depth 8
