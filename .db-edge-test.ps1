$ErrorActionPreference = 'Stop'

$base = 'http://127.0.0.1:8080/api'

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

function Register-TestUser($prefix, $name = '') {
  $stamp = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
  $email = "$prefix-$stamp@example.com"
  $password = 'TestPass123!'
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

Write-Host 'EDGE_TEST_START'

Invoke-ExpectHttpError {
  Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -ContentType 'application/json' -Body (@{
    plan_id = 'pro'
    team_id = ''
  } | ConvertTo-Json)
} 401 'membership order requires login'

$blank = Register-TestUser 'edge-blank' ''
$expectedBlankName = $blank.email.Split('@')[0]
Assert-True ($blank.user.name -eq $expectedBlankName) 'blank nickname falls back to email prefix'

Invoke-ExpectHttpError {
  Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $blank.headers -ContentType 'application/json' -Body (@{
    plan_id = 'not-a-plan'
    team_id = ''
  } | ConvertTo-Json)
} 400 'unknown membership plan is rejected'

$plans = Invoke-RestMethod -Method Get -Uri "$base/membership/plans"
$paidPlans = @($plans.plans | Where-Object { $_.id -ne 'free' })
foreach ($plan in $paidPlans) {
  Assert-True ([int]$plan.group_price -lt [int]$plan.price) "group price is lower than direct price for $($plan.id)"
}

$direct = Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $blank.headers -ContentType 'application/json' -Body (@{
  plan_id = 'pro'
  team_id = ''
} | ConvertTo-Json)
Invoke-ExpectHttpError {
  Invoke-RestMethod -Method Post -Uri "$base/membership/orders/confirm" -Headers $blank.headers -ContentType 'application/json' -Body (@{
    order_id = $direct.order.order_id
  } | ConvertTo-Json)
} 409 'unpaid direct order cannot activate membership'

Invoke-ExpectHttpError {
  Invoke-RestMethod -Method Post -Uri "$base/membership/orders/confirm" -Headers $blank.headers -ContentType 'application/json' -Body (@{
    order_id = 'no-such-order'
  } | ConvertTo-Json)
} 400 'unknown order confirmation is rejected'

$teams = Invoke-RestMethod -Method Get -Uri "$base/membership/group-teams" -Headers $blank.headers
$team = @($teams.teams | Where-Object { $_.plan_id -eq 'plus' -and [int]$_.remaining -ge 1 } | Select-Object -First 1)[0]
Assert-True ($team -and $team.team_id) 'found a recruiting plus team'

$firstGroup = Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $blank.headers -ContentType 'application/json' -Body (@{
  plan_id = 'plus'
  team_id = $team.team_id
} | ConvertTo-Json)
Assert-True ($firstGroup.order.purchase_mode -eq 'group') 'first group order is created'

Invoke-ExpectHttpError {
  Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $blank.headers -ContentType 'application/json' -Body (@{
    plan_id = 'plus'
    team_id = $team.team_id
  } | ConvertTo-Json)
} 400 'same user cannot join the same group team twice'

$named = Register-TestUser 'edge-named' 'EdgeNamedUser'
$teamsAfter = Invoke-RestMethod -Method Get -Uri "$base/membership/group-teams" -Headers $named.headers
$joinedTeam = @($teamsAfter.teams | Where-Object { $_.team_id -eq $team.team_id } | Select-Object -First 1)[0]
if ($joinedTeam) {
  $member = @($joinedTeam.members | Where-Object { $_.user_id -eq $blank.user.id } | Select-Object -First 1)[0]
  Assert-True (-not $member) 'unpaid group order is not shown as a joined paid member'
}

$oneSlotTeam = @($teamsAfter.teams | Where-Object { $_.plan_id -eq 'plus' -and [int]$_.remaining -eq 1 } | Select-Object -First 1)[0]
if ($oneSlotTeam) {
  $filler = Register-TestUser 'edge-fill' 'EdgeFiller'
  $filledOrder = Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $filler.headers -ContentType 'application/json' -Body (@{
    plan_id = 'plus'
    team_id = $oneSlotTeam.team_id
  } | ConvertTo-Json)
  Assert-True ($filledOrder.order.team_id -eq $oneSlotTeam.team_id) 'one-slot team can be filled by a new user'

  $late = Register-TestUser 'edge-late' 'EdgeLate'
  Invoke-ExpectHttpError {
    Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $late.headers -ContentType 'application/json' -Body (@{
      plan_id = 'plus'
      team_id = $oneSlotTeam.team_id
    } | ConvertTo-Json)
  } 400 'full group team cannot be joined again'
} else {
  Write-Host 'SKIP: no one-slot plus team available for full-team boundary'
}

[ordered]@{
  blank_user_id = $blank.user.id
  blank_email = $blank.email
  blank_display_name = $expectedBlankName
  direct_order = $direct.order.order_id
  first_group_order = $firstGroup.order.order_id
  first_group_team = $firstGroup.order.team_id
  checked_full_team = [bool]$oneSlotTeam
} | ConvertTo-Json -Depth 8
