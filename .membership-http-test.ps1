$ErrorActionPreference='Stop'
$base='http://127.0.0.1:8080/api'
$stamp=[DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$email="itest-$stamp@example.com"
$password='TestPass123!'
$name="接口测试$stamp"
Write-Host "EMAIL=$email"
$codeResp=Invoke-RestMethod -Method Post -Uri "$base/auth/email/code" -ContentType 'application/json' -Body (@{email=$email;purpose='register'} | ConvertTo-Json)
$code=$codeResp.dev_code
Write-Host "CODE=$code"
$reg=Invoke-RestMethod -Method Post -Uri "$base/auth/email/register" -ContentType 'application/json' -Body (@{email=$email;code=$code;password=$password;name=$name} | ConvertTo-Json)
$token=$reg.token
$user=$reg.user
Write-Host "USER_ID=$($user.id) ROLE=$($user.role) PROJECT=$($user.default_project.path)"
$headers=@{Authorization="Bearer $token"}
$me=Invoke-RestMethod -Method Get -Uri "$base/membership/me" -Headers $headers
Write-Host "MEMBERSHIP_BEFORE=$($me.plan.id) active=$($me.active)"
$teams=Invoke-RestMethod -Method Get -Uri "$base/membership/group-teams" -Headers $headers
Write-Host "TEAMS counts pro=$(@($teams.teams | Where-Object plan_id -eq 'pro').Count) plus=$(@($teams.teams | Where-Object plan_id -eq 'plus').Count) max=$(@($teams.teams | Where-Object plan_id -eq 'max').Count)"
$direct=Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $headers -ContentType 'application/json' -Body (@{plan_id='pro';team_id=''} | ConvertTo-Json)
$order=$direct.order
Write-Host "DIRECT_ORDER=$($order.order_id) external=$($order.external_order_id) provider=$($order.provider) amount=$($order.amount)"
$confirm=Invoke-RestMethod -Method Post -Uri "$base/membership/orders/confirm" -Headers $headers -ContentType 'application/json' -Body (@{order_id=$order.order_id} | ConvertTo-Json)
Write-Host "DIRECT_CONFIRM active=$($confirm.active) plan=$($confirm.plan.id) remaining=$($confirm.remaining_tokens)"
$result=[ordered]@{email=$email;user_id=$user.id;direct_order=$order.order_id;external_order=$order.external_order_id;confirm_active=$confirm.active;confirm_plan=$confirm.plan.id;project=$user.default_project.path}
$result | ConvertTo-Json -Depth 8
