$ErrorActionPreference = 'Stop'
$base = 'http://127.0.0.1:8080/api'
$stamp = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
$password = 'TestPass123!'
$cancelTeam = ('' + (($stamp + 401) % 90000000 + 10000000)).Substring(0, 8)
$payTeam = ('' + (($stamp + 402) % 90000000 + 10000000)).Substring(0, 8)
function Assert-True($condition, $message) { if (-not $condition) { throw "ASSERT_FAILED: $message" }; Write-Host "PASS: $message" }
function MysqlExec($database, $sql) {
  docker compose --env-file deploy\docker.env -f deploy\docker-compose.full.yml exec -T mysql mysql --default-character-set=utf8mb4 -uroot -pnovel-root -N -B "--database=$database" -e $sql |
    Where-Object { $_ -and -not $_.StartsWith('mysql: [Warning]') }
}
function Register-TestUser($prefix) {
  $email = "$prefix-$stamp@example.com"
  $codeResp = Invoke-RestMethod -Method Post -Uri "$base/auth/email/code" -ContentType 'application/json' -Body (@{ email = $email; purpose = 'register' } | ConvertTo-Json)
  $reg = Invoke-RestMethod -Method Post -Uri "$base/auth/email/register" -ContentType 'application/json' -Body (@{ email = $email; code = $codeResp.dev_code; password = $password; name = '' } | ConvertTo-Json)
  return [ordered]@{ email = $email; token = $reg.token; user = $reg.user; headers = @{ Authorization = "Bearer $($reg.token)" } }
}
function Seed-Team($teamId, $paidCount) {
  $seedRows = @()
  $orderRows = @()
  for ($i=1; $i -le $paidCount; $i++) {
    $uid = "qa-local-seed-$teamId-$i"
    $email = "$uid@example.local"
    $orderId = "1$teamId$i"
    $seedRows += "('$uid','$email',NULL,'LocalSeed$i','seed','user','active',NOW(),NOW())"
    $orderRows += "('$uid','$teamId','$orderId',100124,NOW(),DATE_ADD(NOW(), INTERVAL 3650 DAY),'member-plus','s01','c01',49.00,9.80,39.20,1,'$orderId',NOW(),CONCAT('100124_','$uid','_$i'),NOW(),NOW())"
  }
  MysqlExec 'novel_agent' ("INSERT INTO users (id,email,password_hash,name,provider,role,status,created_at,updated_at) VALUES " + ($seedRows -join ',') + " ON DUPLICATE KEY UPDATE name=VALUES(name), updated_at=NOW();") | Out-Null
  MysqlExec 'group_buy_market' @"
DELETE FROM group_buy_order_list WHERE team_id='$teamId';
DELETE FROM group_buy_order WHERE team_id='$teamId';
INSERT INTO group_buy_order (team_id, activity_id, source, channel, original_price, deduction_price, pay_price, target_count, complete_count, lock_count, status, valid_start_time, valid_end_time, notify_type, notify_url, create_time, update_time)
VALUES ('$teamId', 100124, 's01', 'c01', 49.00, 9.80, 39.20, 3, $paidCount, $paidCount, 0, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 'MQ', NULL, NOW(), NOW());
INSERT INTO group_buy_order_list (user_id, team_id, order_id, activity_id, start_time, end_time, goods_id, source, channel, original_price, deduction_price, pay_price, status, out_trade_no, out_trade_time, biz_id, create_time, update_time)
VALUES $($orderRows -join ',');
"@ | Out-Null
}
Write-Host "LOCAL_PAYMENT_ACTION_TEST_START stamp=$stamp cancelTeam=$cancelTeam payTeam=$payTeam"
Seed-Team $cancelTeam 1
$cancelUser = Register-TestUser 'qa-cancel-order'
$beforeLock = MysqlExec 'group_buy_market' "SELECT lock_count FROM group_buy_order WHERE team_id='$cancelTeam';"
Assert-True ([int]([string]@($beforeLock)[0]) -eq 1) 'cancel test team starts with one locked paid seat'
$cancelOrderResp = Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $cancelUser.headers -ContentType 'application/json' -Body (@{ plan_id='plus'; team_id=$cancelTeam } | ConvertTo-Json)
$cancelOrder = $cancelOrderResp.order
$afterCreateLock = MysqlExec 'group_buy_market' "SELECT lock_count FROM group_buy_order WHERE team_id='$cancelTeam';"
Assert-True ([int]([string]@($afterCreateLock)[0]) -eq 2) 'creating unpaid group order locks one additional seat'
$cancelResp = Invoke-RestMethod -Method Post -Uri "$base/membership/orders/cancel" -Headers $cancelUser.headers -ContentType 'application/json' -Body (@{ order_id=$cancelOrder.order_id } | ConvertTo-Json)
Assert-True ($cancelResp.success -eq $true) 'cancel endpoint returns success'
$afterCancel = MysqlExec 'group_buy_market' "SELECT lock_count FROM group_buy_order WHERE team_id='$cancelTeam';"
Assert-True ([int]([string]@($afterCancel)[0]) -eq 1) 'cancel endpoint releases the pending group-buy seat'

Seed-Team $payTeam 2
$payUser = Register-TestUser 'qa-test-pay'
$payOrderResp = Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $payUser.headers -ContentType 'application/json' -Body (@{ plan_id='plus'; team_id=$payTeam } | ConvertTo-Json)
$payOrder = $payOrderResp.order
Assert-True ($payOrder.test_payment_enabled -eq $true) 'test payment flag is exposed only when local env enables it'
$testPayResp = Invoke-RestMethod -Method Post -Uri "$base/membership/orders/test-pay" -Headers $payUser.headers -ContentType 'application/json' -Body (@{ order_id=$payOrder.order_id } | ConvertTo-Json)
Assert-True ($testPayResp.active -eq $true -and $testPayResp.plan.id -eq 'plus') 'test payment activates membership after real group-buy settlement'
$settled = MysqlExec 'group_buy_market' "SELECT complete_count, lock_count, status FROM group_buy_order WHERE team_id='$payTeam';"
$parts = ([string]@($settled)[0]).Split("`t")
Assert-True ([int]$parts[0] -eq 3 -and [int]$parts[1] -eq 3 -and [int]$parts[2] -eq 1) 'test payment path forms the team through group-buy settlement'
MysqlExec 'group_buy_market' "UPDATE group_buy_order SET status=1, update_time=NOW() WHERE team_id IN ('$cancelTeam','$payTeam');" | Out-Null
[ordered]@{ cancel_team=$cancelTeam; pay_team=$payTeam; cancel_order=$cancelOrder.external_order_id; test_pay_order=$payOrder.external_order_id; test_user=$payUser.user.id } | ConvertTo-Json -Depth 6
