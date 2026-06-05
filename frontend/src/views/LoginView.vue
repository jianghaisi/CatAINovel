<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const name = ref('')
const code = ref('')
const codeSent = ref(false)
const message = ref('')

function nextPath() {
  return route.query.redirect || '/projects'
}

onMounted(async () => {
  const token = route.query.auth_token
  if (token) {
    const ok = await authStore.loginWithToken(String(token))
    if (ok) router.replace(nextPath())
  }
})

async function sendCode() {
  message.value = ''
  const ok = await authStore.sendCode(email.value)
  if (!ok) return
  codeSent.value = true
  message.value = authStore.lastCodeResponse?.message || '验证码已发送。未配置 SMTP 时，请查看后端日志。'
  if (authStore.lastCodeResponse?.dev_code) {
    code.value = authStore.lastCodeResponse.dev_code
    message.value = `开发模式验证码 ${authStore.lastCodeResponse.dev_code} 已自动填入。`
  }
}

async function loginByCode() {
  const ok = password.value
    ? await authStore.registerWithEmail(email.value, code.value, password.value, name.value)
    : await authStore.loginWithEmail(email.value, code.value)
  if (ok) router.replace(nextPath())
}

async function loginByPassword() {
  const ok = await authStore.loginWithPassword(email.value, password.value)
  if (ok) router.replace(nextPath())
}
</script>

<template>
  <main class="login-page">
    <nav class="login-nav">
      <button class="back-home" @click="router.push('/')">← 返回首页</button>
      <div class="brand">
        <img src="/cat-icon.svg" alt="小猫写作" />
        <span>小猫写作 ฅ^•ﻌ•^ฅ</span>
      </div>
      <button class="ghost" @click="router.push('/membership')">会员价格</button>
    </nav>

    <section class="login-hero">
      <div class="login-card">
        <div class="card-brand">
          <img src="/cat-icon.svg" alt="" />
          <span>小猫写作</span>
        </div>
        <h1>登录 / 注册</h1>
        <p class="sub">登录后进入 AI 小说创作平台；未注册邮箱会通过验证码创建账号。</p>

        <button class="oauth-btn" @click="authStore.openLinuxDoLogin">
          <span class="linux-dot">●</span>
          使用 Linux.do 继续
        </button>

        <div class="divider"><span>或</span></div>

        <label class="field">
          <span>邮箱</span>
          <input v-model="email" type="email" placeholder="name@example.com" />
        </label>
        <label class="field">
          <span>密码</span>
          <input v-model="password" type="password" placeholder="已有账号可直接密码登录；新账号配合验证码注册" @keyup.enter="loginByPassword" />
        </label>
        <label class="field">
          <span>昵称（注册时可填）</span>
          <input v-model="name" type="text" placeholder="例如：小猫作者" />
        </label>

        <button class="secondary-btn" :disabled="authStore.loading || !email || !password" @click="loginByPassword">
          邮箱密码登录
        </button>
        <button class="primary-btn" :disabled="authStore.loading || !email" @click="sendCode">
          {{ codeSent ? '重新发送验证码' : '发送验证码注册 / 免密登录' }}
        </button>

        <label v-if="codeSent" class="field">
          <span>验证码</span>
          <input v-model="code" inputmode="numeric" maxlength="6" placeholder="6 位验证码" @keyup.enter="loginByCode" />
        </label>
        <button v-if="codeSent" class="primary-btn" :disabled="authStore.loading || !code" @click="loginByCode">
          {{ password ? '验证码注册并登录' : '验证码免密登录' }}
        </button>

        <p v-if="message" class="hint">{{ message }}</p>
        <p v-if="authStore.lastCodeResponse?.dev_code" class="dev-code">
          当前验证码：<strong>{{ authStore.lastCodeResponse.dev_code }}</strong>
        </p>
        <p v-if="authStore.error" class="error">{{ authStore.error }}</p>
      </div>
    </section>
  </main>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  color: #0f172a;
  background:
    radial-gradient(circle at 16% 58%, rgba(207, 250, 254, .7), transparent 22%),
    radial-gradient(circle at 62% 18%, rgba(219, 234, 254, .9), transparent 28%),
    linear-gradient(180deg, #f8fbff 0%, #fff 70%);
}
.login-nav {
  height: 68px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
}
.brand, .card-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #2563eb;
  font-weight: 900;
}
.brand img, .card-brand img { width: 30px; height: 30px; }
.back-home, .ghost {
  border: 1px solid #bfdbfe;
  border-radius: 999px;
  padding: 10px 14px;
  background: rgba(255, 255, 255, .78);
  color: #2563eb;
  font-weight: 800;
  cursor: pointer;
}
.login-hero {
  min-height: calc(100vh - 68px);
  display: grid;
  place-items: center;
  padding: 32px 16px 64px;
}
.login-card {
  width: min(460px, 100%);
  padding: 34px 30px;
  border: 1px solid rgba(147, 197, 253, .5);
  border-radius: 28px;
  background: rgba(255, 255, 255, .86);
  box-shadow: 0 28px 80px rgba(37, 99, 235, .13);
  backdrop-filter: blur(18px);
}
.card-brand { justify-content: center; margin-bottom: 18px; }
h1 { margin: 0; text-align: center; font-size: 34px; letter-spacing: -.04em; }
.sub { margin: 12px 0 24px; text-align: center; color: #64748b; line-height: 1.7; }
.oauth-btn, .primary-btn, .secondary-btn {
  width: 100%;
  min-height: 44px;
  border-radius: 999px;
  font-weight: 900;
  cursor: pointer;
}
.oauth-btn {
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #111827;
}
.linux-dot { margin-right: 10px; color: #f59e0b; }
.divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 20px 0;
  color: #475569;
  font-weight: 900;
}
.divider::before, .divider::after {
  content: "";
  flex: 1;
  height: 1px;
  background: #dbeafe;
}
.field { display: grid; gap: 8px; margin-top: 12px; color: #1e293b; font-weight: 800; }
.field input {
  min-height: 44px;
  border: 1px solid #bfdbfe;
  border-radius: 999px;
  padding: 0 16px;
  outline: none;
  background: rgba(255, 255, 255, .9);
}
.field input:focus {
  border-color: #60a5fa;
  box-shadow: 0 0 0 4px rgba(96, 165, 250, .16);
}
.primary-btn {
  border: 0;
  margin-top: 12px;
  color: #fff;
  background: linear-gradient(135deg, #38bdf8, #2563eb);
}
.secondary-btn {
  border: 1px solid #bfdbfe;
  margin-top: 14px;
  background: #fff;
  color: #2563eb;
}
.primary-btn:disabled, .secondary-btn:disabled {
  opacity: .55;
  cursor: not-allowed;
}
.hint { color: #2563eb; font-size: 13px; line-height: 1.6; }
.dev-code {
  padding: 10px 12px;
  border: 1px dashed #60a5fa;
  border-radius: 14px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 14px;
}
.error { color: #dc2626; font-size: 13px; line-height: 1.6; }
</style>
