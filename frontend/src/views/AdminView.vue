<script setup>
import { computed, onMounted, ref } from 'vue'
import { adminApi } from '../api'

const loading = ref(false)
const saving = ref(false)
const error = ref('')
const message = ref('')
const overview = ref(null)
const users = ref([])
const selectedFiles = ref(null)
const plans = ref([])
const settings = ref({
  linux_do_client_id: '',
  linux_do_redirect_uri: '',
  linux_do_auth_url: '',
  login_mode: 'email_password'
})

const paidPlans = computed(() => plans.value.filter((plan) => plan.id !== 'free'))
const freePlan = computed(() => plans.value.find((plan) => plan.id === 'free'))

async function loadAdmin() {
  loading.value = true
  error.value = ''
  try {
    const [overviewResp, usersResp, settingsResp, plansResp] = await Promise.all([
      adminApi.overview(),
      adminApi.users(),
      adminApi.getSettings(),
      adminApi.getMembershipPlans()
    ])
    overview.value = overviewResp.data
    users.value = usersResp.data.users || []
    settings.value = { ...settings.value, ...settingsResp.data }
    plans.value = normalizePlans(plansResp.data.plans || [])
  } catch (err) {
    error.value = err?.response?.data?.detail || err.message || '加载失败'
  } finally {
    loading.value = false
  }
}

function normalizePlans(rawPlans) {
  return rawPlans.map((plan) => ({
    ...plan,
    features_text: (plan.features || []).join('\n')
  }))
}

function serializePlans() {
  return plans.value.map((plan) => ({
    ...plan,
    features: (plan.features_text || '').split('\n').map((item) => item.trim()).filter(Boolean)
  }))
}

async function saveSettings() {
  saving.value = true
  error.value = ''
  message.value = ''
  try {
    const { data } = await adminApi.updateSettings(settings.value)
    settings.value = data.settings
    message.value = '登录设置已保存'
  } catch (err) {
    error.value = err?.response?.data?.detail || err.message || '保存失败'
  } finally {
    saving.value = false
  }
}

async function savePlans() {
  saving.value = true
  error.value = ''
  message.value = ''
  try {
    const { data } = await adminApi.updateMembershipPlans(serializePlans())
    plans.value = normalizePlans(data.plans || [])
    message.value = '会员套餐已保存'
  } catch (err) {
    error.value = err?.response?.data?.detail || err.message || '保存会员套餐失败'
  } finally {
    saving.value = false
  }
}

async function inspectFiles(userId) {
  try {
    const { data } = await adminApi.userFiles(userId)
    selectedFiles.value = data
  } catch (err) {
    error.value = err?.response?.data?.detail || err.message || '读取用户文件夹失败'
  }
}

function formatTokens(value) {
  const tokens = Number(value || 0)
  if (tokens >= 10000) return `${Math.round(tokens / 10000)} 万`
  return `${tokens}`
}

onMounted(loadAdmin)
</script>

<template>
  <section class="admin-page">
    <header class="page-header">
      <div>
        <p class="eyebrow">Admin Console</p>
        <h1>超级管理员</h1>
        <p>管理登录策略、用户数据目录、免费额度和会员套餐。</p>
      </div>
      <button class="ghost-btn" @click="loadAdmin">刷新</button>
    </header>

    <div v-if="error" class="error-card">{{ error }}</div>
    <div v-if="message" class="success-card">{{ message }}</div>
    <div v-if="loading" class="loading-card">正在加载管理数据…</div>

    <div class="top-grid">
      <article class="panel">
        <h2>登录设置</h2>
        <label>
          <span>登录模式</span>
          <select v-model="settings.login_mode">
            <option value="email_password">邮箱密码 + 验证码注册</option>
            <option value="email_code">仅邮箱验证码</option>
            <option value="linux_do">Linux.do 优先</option>
          </select>
        </label>
        <label>
          <span>Linux.do Client ID</span>
          <input v-model="settings.linux_do_client_id" placeholder="OAuth Client ID" />
        </label>
        <label>
          <span>Linux.do Redirect URI</span>
          <input v-model="settings.linux_do_redirect_uri" placeholder="https://你的域名/api/auth/linux-do/callback" />
        </label>
        <label>
          <span>Linux.do Auth URL</span>
          <input v-model="settings.linux_do_auth_url" placeholder="https://connect.linux.do/oauth2/authorize" />
        </label>
        <button class="primary-btn" :disabled="saving" @click="saveSettings">保存登录设置</button>
      </article>

      <article class="panel overview-panel">
        <h2>系统概览</h2>
        <div class="stat">
          <span>注册用户</span>
          <strong>{{ overview?.users_count ?? users.length }}</strong>
        </div>
        <p class="hint">指定超管：{{ overview?.super_admin_email || '未设置' }}</p>
        <div class="feature-list">
          <span>用户列表</span>
          <span>登录设置</span>
          <span>会员套餐</span>
          <span>用户小说目录</span>
        </div>
      </article>
    </div>

    <article class="panel">
      <div class="panel-title">
        <div>
          <h2>免费账户与会员套餐</h2>
          <p>这里可以编辑普通账户每日额度、签到奖励，以及 Pro / Plus / Max 的 Token 数量。</p>
        </div>
        <button class="primary-btn" :disabled="saving" @click="savePlans">保存会员配置</button>
      </div>

      <div v-if="freePlan" class="free-plan-card">
        <h3>普通账户 Free</h3>
        <div class="plan-grid">
          <label><span>初始 Token</span><input v-model.number="freePlan.initial_tokens" type="number" /></label>
          <label><span>每日使用上限</span><input v-model.number="freePlan.daily_token_limit" type="number" /></label>
          <label><span>签到奖励 Token</span><input v-model.number="freePlan.checkin_reward_tokens" type="number" /></label>
        </div>
      </div>

      <div class="plans-grid">
        <article v-for="plan in paidPlans" :key="plan.id" class="plan-card">
          <div class="plan-head">
            <h3>{{ plan.name }}</h3>
            <span>¥{{ plan.price }} / 月</span>
          </div>
          <div class="plan-grid">
            <label><span>套餐名称</span><input v-model="plan.name" /></label>
            <label><span>价格</span><input v-model.number="plan.price" type="number" /></label>
            <label><span>有效天数</span><input v-model.number="plan.period_days" type="number" /></label>
            <label><span>Token 总额度</span><input v-model.number="plan.monthly_token_quota" type="number" /></label>
            <label><span>商品 ID</span><input v-model="plan.product_id" /></label>
            <label><span>活动 ID</span><input v-model.number="plan.activity_id" type="number" /></label>
            <label><span>营销类型</span><input v-model.number="plan.market_type" type="number" /></label>
            <label><span>拼团人数</span><input v-model.number="plan.team_size" type="number" min="2" /></label>
            <label>
              <span>拼团策略</span>
              <select v-model="plan.group_strategy_type">
                <option value="discount">折扣策略</option>
                <option value="fixed_price">几元购</option>
                <option value="reduction">立减</option>
                <option value="threshold_reduction">满减</option>
              </select>
            </label>
            <label><span>折扣数字（8 折填 0.8）</span><input v-model.number="plan.group_discount_rate" type="number" min="0" max="1" step="0.01" /></label>
            <label><span>几元购价格</span><input v-model.number="plan.group_fixed_price" type="number" min="0" /></label>
            <label><span>立减 / 满减金额</span><input v-model.number="plan.group_reduction_amount" type="number" min="0" /></label>
            <label><span>满减门槛金额</span><input v-model.number="plan.group_threshold_amount" type="number" min="0" /></label>
            <label><span>套餐权益（每行一个）</span><textarea v-model="plan.features_text" rows="5"></textarea></label>
          </div>
          <p class="quota-preview">当前额度：{{ formatTokens(plan.monthly_token_quota) }} Token</p>
        </article>
      </div>
    </article>

    <article class="panel">
      <h2>用户管理</h2>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>邮箱</th>
              <th>昵称</th>
              <th>角色</th>
              <th>状态</th>
              <th>注册时间</th>
              <th>数据目录</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>{{ user.email }}</td>
              <td>{{ user.name }}</td>
              <td>{{ user.role }}</td>
              <td>{{ user.status }}</td>
              <td>{{ user.created_at || '-' }}</td>
              <td class="path-cell">{{ user.data_dir }}</td>
              <td><button class="ghost-btn" @click="inspectFiles(user.id)">查看小说</button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </article>

    <article v-if="selectedFiles" class="panel">
      <h2>用户小说数据</h2>
      <p class="path-cell">{{ selectedFiles.data_dir }}</p>
      <div class="project-list">
        <div v-for="project in selectedFiles.projects" :key="project.path" class="project-card">
          <strong>{{ project.name }}</strong>
          <span>{{ project.chapters }} 章 · {{ (project.size_bytes / 1024).toFixed(1) }} KB</span>
          <small>{{ project.path }}</small>
        </div>
      </div>
    </article>
  </section>
</template>

<style scoped>
.admin-page {
  min-height: 100%;
  padding: 32px;
  background:
    radial-gradient(circle at 8% 18%, rgba(191, 219, 254, .65), transparent 28%),
    linear-gradient(135deg, #f8fbff 0%, #eef7ff 100%);
  color: #0f172a;
}
.page-header, .panel-title, .plan-head {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: flex-start;
}
.page-header { margin-bottom: 22px; }
.eyebrow { color: #2563eb; font-weight: 900; letter-spacing: .12em; text-transform: uppercase; }
h1 { font-size: 34px; margin: 4px 0; }
h2 { margin: 0 0 16px; }
h3 { margin: 0 0 12px; }
.top-grid { display: grid; grid-template-columns: minmax(0, 1.4fr) minmax(280px, .65fr); gap: 18px; margin-bottom: 18px; }
.panel, .error-card, .success-card, .loading-card {
  border: 1px solid #dbeafe;
  border-radius: 24px;
  background: rgba(255, 255, 255, .9);
  box-shadow: 0 18px 50px rgba(37, 99, 235, .08);
  padding: 22px;
  margin-bottom: 18px;
}
.error-card { color: #b91c1c; }
.success-card { color: #1d4ed8; background: #eff6ff; }
.loading-card { color: #2563eb; }
label { display: grid; gap: 8px; margin-bottom: 12px; font-weight: 800; color: #334155; }
input, select, textarea {
  width: 100%;
  border: 1px solid #bfdbfe;
  border-radius: 14px;
  padding: 10px 12px;
  outline: none;
  background: #fff;
  color: #0f172a;
}
input, select { height: 42px; }
textarea { resize: vertical; line-height: 1.6; }
.primary-btn, .ghost-btn {
  border-radius: 999px;
  padding: 9px 16px;
  cursor: pointer;
  font-weight: 900;
}
.primary-btn { border: 0; background: linear-gradient(135deg, #38bdf8, #2563eb); color: white; }
.ghost-btn { border: 1px solid #bfdbfe; background: white; color: #2563eb; }
.primary-btn:disabled { opacity: .55; cursor: wait; }
.stat { display: grid; gap: 8px; }
.stat strong { font-size: 44px; color: #2563eb; }
.hint, .panel-title p, .quota-preview { color: #64748b; margin-top: 8px; }
.feature-list { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 12px; }
.feature-list span {
  padding: 6px 10px;
  border-radius: 999px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 12px;
  font-weight: 900;
}
.free-plan-card, .plan-card {
  border: 1px solid #bfdbfe;
  border-radius: 20px;
  padding: 18px;
  background: linear-gradient(180deg, #ffffff, #f8fbff);
}
.free-plan-card { margin-bottom: 18px; }
.plans-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 16px; }
.plan-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }
.plan-card .plan-grid { grid-template-columns: 1fr; }
.plan-head span {
  padding: 6px 10px;
  border-radius: 999px;
  background: #dbeafe;
  color: #1d4ed8;
  font-weight: 900;
}
.table-wrap { overflow: auto; max-height: 420px; }
table { width: 100%; min-width: 980px; border-collapse: collapse; }
th, td { padding: 12px; border-bottom: 1px solid #e2e8f0; text-align: left; }
th { color: #475569; background: #f8fbff; position: sticky; top: 0; }
.path-cell { max-width: 360px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: #64748b; }
.project-list { display: grid; gap: 12px; margin-top: 12px; max-height: 320px; overflow: auto; }
.project-card { display: grid; gap: 4px; padding: 14px; border: 1px solid #dbeafe; border-radius: 14px; background: #f8fbff; }
@media (max-width: 1180px) {
  .top-grid, .plans-grid, .plan-grid { grid-template-columns: 1fr; }
}
</style>
