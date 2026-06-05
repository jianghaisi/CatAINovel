$ErrorActionPreference = 'Stop'

$base = 'http://127.0.0.1:8080/api'
$stamp = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$email = "itest-chain-$stamp@example.com"
$password = 'TestPass123!'
$expectedName = $email.Split('@')[0]

function Assert-True($condition, $message) {
  if (-not $condition) {
    throw "ASSERT_FAILED: $message"
  }
  Write-Host "PASS: $message"
}

function MysqlScalar($database, $sql) {
  $value = docker compose --env-file deploy\docker.env -f deploy\docker-compose.full.yml exec -T mysql mysql --default-character-set=utf8mb4 -uroot -pnovel-root -N -B "--database=$database" -e $sql
  return ($value | Where-Object { $_ -and -not $_.StartsWith('mysql: [Warning]') } | Select-Object -First 1)
}

function MysqlJson($database, $sql) {
  $rows = docker compose --env-file deploy\docker.env -f deploy\docker-compose.full.yml exec -T mysql mysql --default-character-set=utf8mb4 -uroot -pnovel-root -N -B "--database=$database" -e $sql
  return @($rows | Where-Object { $_ -and -not $_.StartsWith('mysql: [Warning]') })
}

Write-Host "TEST_EMAIL=$email"

$codeResp = Invoke-RestMethod -Method Post -Uri "$base/auth/email/code" -ContentType 'application/json' -Body (@{
  email = $email
  purpose = 'register'
} | ConvertTo-Json)

$reg = Invoke-RestMethod -Method Post -Uri "$base/auth/email/register" -ContentType 'application/json' -Body (@{
  email = $email
  code = $codeResp.dev_code
  password = $password
  name = ''
} | ConvertTo-Json)

$token = $reg.token
$userId = $reg.user.id
$headers = @{ Authorization = "Bearer $token" }

Assert-True ($userId -and $userId.Length -gt 0) "registered user has id"
Assert-True ($reg.user.email -eq $email) "registered user email is returned"
Assert-True ($reg.user.name -eq $expectedName) "blank nickname falls back to email prefix"

$userRow = MysqlJson 'novel_agent' "SELECT id,email,name FROM users WHERE email='$email';"
Assert-True ($userRow.Count -eq 1) "new user is written to novel_agent.users"
$userFields = ([string]@($userRow)[0]).Split("`t")
Assert-True ($userFields[0] -eq $userId) "novel_agent.users.id equals login user id"
Assert-True ($userFields[1] -eq $email) "novel_agent.users.email equals registered email"
Assert-True ($userFields[2] -eq $expectedName) "novel_agent.users.name uses email prefix when nickname is blank"

$directResp = Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $headers -ContentType 'application/json' -Body (@{
  plan_id = 'pro'
  team_id = ''
} | ConvertTo-Json)
$directOrder = $directResp.order
Assert-True ($directOrder.purchase_mode -eq 'direct') "direct order purchase_mode is direct"

$payUserId = MysqlScalar 's-pay-mall-ddd-market' "SELECT user_id FROM pay_order WHERE order_id='$($directOrder.external_order_id)';"
Assert-True ($payUserId -eq $userId) "pay_order.user_id equals login user id for direct order"

$teams = Invoke-RestMethod -Method Get -Uri "$base/membership/group-teams" -Headers $headers
$team = @($teams.teams | Where-Object { $_.plan_id -eq 'plus' -and [int]$_.remaining -ge 2 } | Select-Object -First 1)[0]
if (-not $team) {
  $team = @($teams.teams | Where-Object { $_.plan_id -eq 'plus' -and [int]$_.remaining -ge 1 } | Select-Object -First 1)[0]
}
Assert-True ($team -and $team.team_id) "found a plus recruiting group team"

$groupResp = Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $headers -ContentType 'application/json' -Body (@{
  plan_id = 'plus'
  team_id = $team.team_id
} | ConvertTo-Json)
$groupOrder = $groupResp.order
Assert-True ($groupOrder.purchase_mode -eq 'group') "group order purchase_mode is group"
Assert-True ($groupOrder.team_id -eq $team.team_id) "group order keeps selected team id"

$groupUserId = MysqlScalar 'group_buy_market' "SELECT user_id FROM group_buy_order_list WHERE out_trade_no='$($groupOrder.external_order_id)';"
Assert-True ($groupUserId -eq $userId) "group_buy_order_list.user_id equals login user id for group order"

$teamsAfter = Invoke-RestMethod -Method Get -Uri "$base/membership/group-teams" -Headers $headers
$teamAfter = @($teamsAfter.teams | Where-Object { $_.team_id -eq $team.team_id } | Select-Object -First 1)[0]
Assert-True ($teamAfter -and $teamAfter.members) "joined team is still visible with members"
$member = @($teamAfter.members | Where-Object { $_.user_id -eq $userId } | Select-Object -First 1)[0]
Assert-True ($member -and $member.name -eq $expectedName) "group team member display name is resolved from novel_agent.users"
Assert-True ($member.email -eq $email) "group team member email is resolved from novel_agent.users"

[ordered]@{
  email = $email
  expected_name = $expectedName
  user_id = $userId
  direct_order = $directOrder.external_order_id
  direct_pay_user_id = $payUserId
  group_order = $groupOrder.external_order_id
  group_team_id = $groupOrder.team_id
  group_user_id = $groupUserId
  displayed_member_name = $member.name
  displayed_member_email = $member.email
} | ConvertTo-Json -Depth 8
