$ErrorActionPreference='Stop'
$base='http://127.0.0.1:8080/api'
$stamp=[DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$email="itest-project-$stamp@example.com"
$password='TestPass123!'
$name="ProjectTest$stamp"
$codeResp=Invoke-RestMethod -Method Post -Uri "$base/auth/email/code" -ContentType 'application/json' -Body (@{email=$email;purpose='register'} | ConvertTo-Json)
$reg=Invoke-RestMethod -Method Post -Uri "$base/auth/email/register" -ContentType 'application/json' -Body (@{email=$email;code=$codeResp.dev_code;password=$password;name=$name} | ConvertTo-Json)
$token=$reg.token
$headers=@{Authorization="Bearer $token"}
$projects=Invoke-RestMethod -Method Get -Uri "$base/projects/list" -Headers $headers
$result=[ordered]@{email=$email;user_id=$reg.user.id;default_project=$reg.user.default_project;project_count=@($projects.projects).Count;first_project=@($projects.projects)[0]}
$result | ConvertTo-Json -Depth 8
