$ErrorActionPreference = 'Stop'

$base = 'http://127.0.0.1:8080/api'
$stamp = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
$password = 'TestPass123!'
$teamId = ('' + ($stamp % 90000000 + 10000000)).Substring(0, 8)
$seedUser = "qa-unpaid-seed-$stamp"
$seedOrder = ('' + (($stamp + 301) % 900000000000 + 100000000000)).Substring(0, 12)

function Assert-True($condition, $message) {
  if (-not $condition) {
    throw "ASSERT_FAILED: $message"
  }
  Write-Host "PASS: $message"
}

function Invoke-ExpectHttpError($scriptBlock, $expectedStatus, $message) {
  try {
    & $scriptBlock | Out-Null
    throw "EXPECTED_HTTP_$expectedStatus"
  } catch {
    $status = $null
    if ($_.Exception.Response) {
      $status = [int]$_.Exception.Response.StatusCode
    }
    if ($status -ne $expectedStatus) {
      throw "ASSERT_FAILED: $message expected=$expectedStatus actual=$status error=$($_.Exception.Message)"
    }
    Write-Host "PASS: $message"
  }
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

Write-Host "UNPAID_GROUP_BOUNDARY_TEST_START stamp=$stamp team=$teamId"

MysqlExec 'novel_agent' @"
INSERT INTO users (id,email,password_hash,name,provider,role,status,created_at,updated_at)
VALUES
  ('$seedUser','$seedUser@example.local',NULL,'UnpaidSeed','seed','user','active',NOW(),NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name), updated_at=NOW();
"@ | Out-Null

MysqlExec 'group_buy_market' @"
DELETE FROM group_buy_order_list WHERE team_id='$teamId';
DELETE FROM group_buy_order WHERE team_id='$teamId';
INSERT INTO group_buy_order
  (team_id, activity_id, source, channel, original_price, deduction_price, pay_price, target_count, complete_count, lock_count, status, valid_start_time, valid_end_time, notify_type, notify_url, create_time, update_time)
VALUES
  ('$teamId', 100124, 's01', 'c01', 49.00, 9.80, 39.20, 3, 1, 1, 0, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 'MQ', NULL, NOW(), NOW());
INSERT INTO group_buy_order_list
  (user_id, team_id, order_id, activity_id, start_time, end_time, goods_id, source, channel, original_price, deduction_price, pay_price, status, out_trade_no, out_trade_time, biz_id, create_time, update_time)
VALUES
  ('$seedUser', '$teamId', '$seedOrder', 100124, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 'member-plus', 's01', 'c01', 49.00, 9.80, 39.20, 1, '$seedOrder', NOW(), CONCAT('100124_', '$seedUser', '_1'), NOW(), NOW());
"@ | Out-Null

$buyer = Register-TestUser 'qa-unpaid-buyer' ''
$before = Invoke-RestMethod -Method Get -Uri "$base/membership/group-teams" -Headers $buyer.headers
$teamBefore = @($before.teams | Where-Object { $_.team_id -eq $teamId } | Select-Object -First 1)[0]
Assert-True ($teamBefore -and [int]$teamBefore.paid_count -eq 1 -and [int]$teamBefore.joined -eq 1 -and [int]$teamBefore.remaining -eq 2) 'controlled team starts with one paid member and two open seats'

$orderResp = Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $buyer.headers -ContentType 'application/json' -Body (@{
  plan_id = 'plus'
  team_id = $teamId
} | ConvertTo-Json)
$order = $orderResp.order
Assert-True ($order.purchase_mode -eq 'group') 'unpaid buyer can create a group payment order'

$payStatus = MysqlExec 's-pay-mall-ddd-market' "SELECT status FROM pay_order WHERE order_id='$($order.external_order_id)' AND user_id='$($buyer.user.id)';"
Assert-True (([string]@($payStatus)[0]) -eq 'PAY_WAIT') 'new group payment order remains PAY_WAIT before payment'
$memberStatus = MysqlExec 'group_buy_market' "SELECT status FROM group_buy_order_list WHERE team_id='$teamId' AND out_trade_no='$($order.external_order_id)' AND user_id='$($buyer.user.id)';"
Assert-True ([int]([string]@($memberStatus)[0]) -eq 0) 'unpaid group order is only a locked pending row'

Invoke-ExpectHttpError {
  Invoke-RestMethod -Method Post -Uri "$base/membership/orders/confirm" -Headers $buyer.headers -ContentType 'application/json' -Body (@{
    order_id = $order.order_id
  } | ConvertTo-Json)
} 409 'unpaid group order cannot activate membership'

$membershipJson = docker compose --env-file deploy\docker.env -f deploy\docker-compose.full.yml exec -T backend python -c "from services.membership_service import get_membership; import json; print(json.dumps(get_membership('$($buyer.user.id)'), ensure_ascii=False))"
$membership = ($membershipJson | Select-Object -Last 1) | ConvertFrom-Json
Assert-True (-not $membership) 'unpaid group buyer has no membership'

$after = Invoke-RestMethod -Method Get -Uri "$base/membership/group-teams" -Headers $buyer.headers
$teamAfter = @($after.teams | Where-Object { $_.team_id -eq $teamId } | Select-Object -First 1)[0]
Assert-True ($teamAfter -and [int]$teamAfter.paid_count -eq 1 -and [int]$teamAfter.joined -eq 2 -and [int]$teamAfter.remaining -eq 1) 'pending payment reserves a seat but does not count as paid'
$unpaidMember = @($teamAfter.members | Where-Object { $_.user_id -eq $buyer.user.id } | Select-Object -First 1)[0]
Assert-True (-not $unpaidMember) 'unpaid buyer is not shown as a paid group member'

# Hide this controlled team after verification so pending QA seats do not pollute the public page.
MysqlExec 'group_buy_market' "UPDATE group_buy_order SET status=1, update_time=NOW() WHERE team_id='$teamId';" | Out-Null

[ordered]@{
  buyer_user_id = $buyer.user.id
  order = $order.external_order_id
  team_id = $teamId
} | ConvertTo-Json -Depth 8
