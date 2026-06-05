$ErrorActionPreference='Stop'
$base='http://127.0.0.1:8080/api'
$stamp=[DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$email="itest-group-$stamp@example.com"
$password='TestPass123!'
$name="GroupTest$stamp"
$codeResp=Invoke-RestMethod -Method Post -Uri "$base/auth/email/code" -ContentType 'application/json' -Body (@{email=$email;purpose='register'} | ConvertTo-Json)
$reg=Invoke-RestMethod -Method Post -Uri "$base/auth/email/register" -ContentType 'application/json' -Body (@{email=$email;code=$codeResp.dev_code;password=$password;name=$name} | ConvertTo-Json)
$headers=@{Authorization="Bearer $($reg.token)"}
$teams=Invoke-RestMethod -Method Get -Uri "$base/membership/group-teams" -Headers $headers
$team=@($teams.teams | Where-Object {$_.plan_id -eq 'plus'} | Select-Object -First 1)[0]
Write-Host "USER_ID=$($reg.user.id) TEAM_ID=$($team.team_id) joined=$($team.joined) remaining=$($team.remaining)"
try {
  $orderResp=Invoke-RestMethod -Method Post -Uri "$base/membership/orders" -Headers $headers -ContentType 'application/json' -Body (@{plan_id='plus';team_id=$team.team_id} | ConvertTo-Json)
  $order=$orderResp.order
  Write-Host "GROUP_ORDER=$($order.order_id) provider=$($order.provider) amount=$($order.amount) direct=$($order.direct_price) team=$($order.team_id)"
  [ordered]@{email=$email;user_id=$reg.user.id;team_id=$team.team_id;order_id=$order.order_id;external_order=$order.external_order_id;amount=$order.amount;direct=$order.direct_price} | ConvertTo-Json -Depth 6
} catch {
  Write-Host "GROUP_CREATE_ERROR=$($_.Exception.Message)"
  if ($_.ErrorDetails.Message) { Write-Host $_.ErrorDetails.Message }
  [ordered]@{email=$email;user_id=$reg.user.id;team_id=$team.team_id;error=$_.Exception.Message;detail=$_.ErrorDetails.Message} | ConvertTo-Json -Depth 6
}
