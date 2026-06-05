<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { membershipApi } from '../api'
import { useAuthStore } from '../stores/auth'
import { useMembershipStore } from '../stores/membership'

const router = useRouter()
const authStore = useAuthStore()
const membershipStore = useMembershipStore()
const plans = ref([
  { id: 'pro', name: 'Pro', price: 19, monthly_token_quota: 1000000, features: ['日常章节生成', '常用模型', '适合稳定更新'] },
  { id: 'plus', name: 'Plus', price: 49, monthly_token_quota: 5000000, features: ['长篇任务', '高级模型', '更长上下文'] },
  { id: 'max', name: 'Max', price: 99, monthly_token_quota: 20000000, features: ['全部模型', '最高额度', '百万字创作'] }
])

const accountName = computed(() => authStore.user?.name || authStore.user?.email?.split('@')[0] || '小猫用户')
const accountEmail = computed(() => authStore.user?.email || authStore.user?.id || '未登录账号')
const accountInitial = computed(() => accountName.value.slice(0, 1).toUpperCase() || '喵')
const memberLabel = computed(() => membershipStore.isActive ? membershipStore.currentPlan?.name || '会员' : 'Free')

onMounted(async () => {
  await authStore.bootstrap()
  if (authStore.isAuthenticated) membershipStore.refresh()
  try {
    const { data } = await membershipApi.getPlans()
    if (data.plans?.length) plans.value = data.plans.filter((plan) => plan.id !== 'free')
  } catch {}
})

function start() {
  router.push(authStore.isAuthenticated ? '/projects' : '/login')
}

function formatTokens(value) {
  if (!value) return '按套餐配置'
  return `${Math.round(value / 10000)} 万 Token / 月`
}
</script>

<template>
  <main class="landing-page">
    <nav class="nav">
      <div class="brand">
        <img src="/cat-icon.svg" alt="小猫写作" />
        <span>小猫写作 ฅ^•ﻌ•^ฅ</span>
      </div>
      <div class="nav-links">
        <a href="#how">使用方法</a>
        <a href="#pricing">会员价格</a>
        <button v-if="!authStore.isAuthenticated" @click="start">开始使用</button>
        <button v-else class="account-pill" :title="`${accountName} / ${accountEmail}`" @click="start">
          <span>{{ accountInitial }}</span>
          <b>{{ accountName }}</b>
          <em>{{ memberLabel }}</em>
        </button>
      </div>
    </nav>

    <section class="hero">
      <div class="hero-copy">
        <p class="eyebrow">AI NOVEL STUDIO</p>
        <h1>用 AI 搭建你的长篇小说创作中枢</h1>
        <p class="subtitle">从灵感、角色设定、全书总纲到章节续写，小猫写作把网文创作拆成可控、可持续、可复查的工作流。</p>
        <div class="hero-actions">
          <button class="primary" @click="start">{{ authStore.isAuthenticated ? '进入工作台' : '开始使用' }}</button>
          <a href="#how">看看怎么写</a>
        </div>
        <div class="metrics">
          <span>总纲规划</span>
          <span>RAG 长期记忆</span>
          <span>章节审查修复</span>
        </div>
      </div>
      <div class="hero-card">
        <div class="card-top"><span></span><span></span><span></span></div>
        <div class="prompt-box">输入灵感：幼儿园重生 + 亲亲变强系统</div>
        <div class="flow-line active">生成全书总纲与完整卷纲</div>
        <div class="flow-line">拆解章节大纲</div>
        <div class="flow-line">逐章写作、审查、修复</div>
      </div>
    </section>

    <section id="how" class="section">
      <p class="eyebrow">HOW IT WORKS</p>
      <h2>四步开始 AI 小说创作</h2>
      <div class="steps">
        <article><strong>01</strong><h3>输入主题</h3><p>写下小说类型、世界观、主角和金手指创意。</p></article>
        <article><strong>02</strong><h3>生成设定</h3><p>自动生成角色设定、故事总纲、完整卷纲和伏笔线。</p></article>
        <article><strong>03</strong><h3>章节创作</h3><p>选择章节进行 AI 续写、润色、改写和一致性审查。</p></article>
        <article><strong>04</strong><h3>持续沉淀</h3><p>把正文、摘要、角色状态和 RAG 记忆持续写回项目。</p></article>
      </div>
    </section>

    <section id="pricing" class="section pricing-section">
      <p class="eyebrow">MEMBERSHIP</p>
      <h2>选择适合你的会员套餐</h2>
      <div class="pricing">
        <article v-for="plan in plans" :key="plan.id" class="price-card" :class="{ featured: plan.id === 'plus' }">
          <div class="plan-name">{{ plan.name }}</div>
          <div class="price">¥{{ plan.price }}<span>/ 月</span></div>
          <p class="quota">{{ formatTokens(plan.monthly_token_quota) }}</p>
          <ul>
            <li v-for="feature in plan.features" :key="feature">{{ feature }}</li>
          </ul>
          <button @click="router.push('/membership')">查看 {{ plan.name }}</button>
        </article>
      </div>
    </section>

    <footer class="footer">
      <span>© 2026 小猫写作</span>
      <span>面向长篇网文创作的 AI 工作台</span>
    </footer>
  </main>
</template>

<style scoped>
.landing-page {
  min-height: 100vh;
  overflow-x: hidden;
  color: #0f172a;
  background:
    radial-gradient(circle at 12% 20%, rgba(191, 219, 254, .7), transparent 28%),
    radial-gradient(circle at 80% 10%, rgba(221, 214, 254, .65), transparent 26%),
    linear-gradient(180deg, #f8fbff 0%, #eef7ff 42%, #fff 100%);
}

.nav {
  position: sticky;
  top: 0;
  z-index: 10;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 clamp(20px, 5vw, 72px);
  backdrop-filter: blur(18px);
  background: rgba(248, 251, 255, .78);
  border-bottom: 1px solid rgba(148, 163, 184, .18);
}

.brand,
.nav-links,
.hero-actions,
.metrics {
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand {
  font-weight: 950;
  font-size: 18px;
  color: #1d4ed8;
}

.brand img {
  width: 34px;
  height: 34px;
}

.nav-links {
  gap: 24px;
  font-weight: 800;
}

.nav-links a,
.hero-actions a {
  color: #475569;
  text-decoration: none;
}

button {
  border: 0;
  cursor: pointer;
  font-weight: 900;
  border-radius: 999px;
}

.nav button,
.primary,
.price-card button {
  color: #fff;
  background: linear-gradient(135deg, #38bdf8, #2563eb);
  box-shadow: 0 16px 34px rgba(37, 99, 235, .2);
}

.nav button {
  padding: 11px 18px;
}

.account-pill {
  max-width: 250px;
  display: inline-grid;
  grid-template-columns: 26px minmax(0, 1fr) auto;
  align-items: center;
  gap: 8px;
}

.account-pill span {
  width: 26px;
  height: 26px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, .28);
}

.account-pill b {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.account-pill em {
  font-style: normal;
  font-size: 12px;
  opacity: .9;
}

.hero {
  min-height: calc(100vh - 72px);
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(320px, .75fr);
  gap: 48px;
  align-items: center;
  padding: 64px clamp(20px, 6vw, 96px);
}

.eyebrow {
  margin: 0 0 12px;
  color: #2563eb;
  font-weight: 950;
  letter-spacing: .18em;
  text-transform: uppercase;
}

h1 {
  margin: 0;
  max-width: 880px;
  font-size: clamp(42px, 6vw, 76px);
  line-height: 1.02;
  letter-spacing: -.06em;
}

.subtitle {
  max-width: 720px;
  color: #475569;
  font-size: 20px;
  line-height: 1.8;
}

.hero-actions {
  margin-top: 30px;
}

.primary {
  padding: 16px 28px;
  font-size: 17px;
}

.metrics {
  flex-wrap: wrap;
  margin-top: 28px;
  color: #1e40af;
  font-weight: 900;
}

.metrics span {
  padding: 9px 14px;
  border: 1px solid rgba(37, 99, 235, .16);
  border-radius: 999px;
  background: rgba(255, 255, 255, .62);
}

.hero-card {
  border: 1px solid rgba(147, 197, 253, .55);
  border-radius: 30px;
  padding: 22px;
  background: rgba(255, 255, 255, .78);
  box-shadow: 0 30px 80px rgba(37, 99, 235, .16);
  transform: rotate(1deg);
}

.card-top {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
}

.card-top span {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #93c5fd;
}

.prompt-box,
.flow-line {
  border-radius: 18px;
  border: 1px solid #e2e8f0;
}

.prompt-box {
  padding: 18px;
  background: #eff6ff;
  border-color: #bfdbfe;
  color: #1e3a8a;
  font-weight: 900;
}

.flow-line {
  margin-top: 16px;
  padding: 16px 18px;
  color: #64748b;
  background: #fff;
}

.flow-line.active {
  color: #1d4ed8;
  border-color: #93c5fd;
  box-shadow: 0 12px 30px rgba(37, 99, 235, .12);
}

.section {
  padding: 76px clamp(20px, 6vw, 96px);
}

h2 {
  margin: 0 0 28px;
  font-size: clamp(30px, 4vw, 48px);
  letter-spacing: -.04em;
}

.steps,
.pricing {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.steps article,
.price-card {
  border: 1px solid rgba(147, 197, 253, .42);
  border-radius: 26px;
  padding: 24px;
  background: rgba(255, 255, 255, .84);
  box-shadow: 0 18px 50px rgba(37, 99, 235, .08);
}

.steps strong {
  color: #2563eb;
}

.steps p,
.quota,
.price-card li {
  color: #64748b;
  line-height: 1.7;
}

.pricing-section {
  background: linear-gradient(180deg, rgba(239, 246, 255, .7), rgba(255, 255, 255, .4));
}

.pricing {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.price-card.featured {
  border-color: #2563eb;
  transform: translateY(-8px);
}

.plan-name {
  color: #1d4ed8;
  font-weight: 950;
  font-size: 22px;
}

.price {
  margin: 16px 0 4px;
  font-size: 44px;
  font-weight: 950;
}

.price span {
  font-size: 15px;
  color: #64748b;
}

.price-card ul {
  min-height: 116px;
  padding-left: 18px;
}

.price-card button {
  width: 100%;
  padding: 13px 18px;
}

.footer {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 28px clamp(20px, 6vw, 96px);
  color: #64748b;
  border-top: 1px solid rgba(148, 163, 184, .18);
}

@media (max-width: 960px) {
  .hero,
  .steps,
  .pricing {
    grid-template-columns: 1fr;
  }

  .nav-links a {
    display: none;
  }

  .price-card.featured {
    transform: none;
  }

  .footer {
    flex-direction: column;
  }
}
</style>
