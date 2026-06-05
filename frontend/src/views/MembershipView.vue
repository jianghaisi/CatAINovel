<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMembershipStore } from '../stores/membership'

const route = useRoute()
const router = useRouter()
const membership = useMembershipStore()
const selectedPlan = ref('plus')
const message = ref('')
const pageByPlan = ref({})
const nowTick = ref(Date.now())
let timer = null

const fallbackPlans = [
  { id: 'pro', name: 'Pro', price: 19, group_price: 15, monthly_token_quota: 1000000, team_size: 3, features: ['日常章节生成', '基础 RAG 检索', '100 万 Token / 月'] },
  { id: 'plus', name: 'Plus', price: 49, group_price: 39, monthly_token_quota: 5000000, team_size: 3, features: ['长篇自动任务', '更高生成额度', '500 万 Token / 月'] },
  { id: 'max', name: 'Max', price: 99, group_price: 79, monthly_token_quota: 20000000, team_size: 3, features: ['最高 Token 额度', '适合百万字长篇', '2000 万 Token / 月'] }
]

const plans = computed(() => (membership.plans.length ? membership.plans : fallbackPlans).filter((plan) => plan.id !== 'free'))
const allRecruitingTeams = computed(() => membership.groupTeams.filter((team) => team.status === 'recruiting'))

onMounted(async () => {
  await Promise.all([membership.loadPlans(), membership.loadGroupTeams(), membership.refresh()])
  if (membership.isActive) message.value = `当前 ${membership.currentPlan?.name || '会员'} 已开通。`
  timer = window.setInterval(() => { nowTick.value = Date.now() }, 1000)
})
onUnmounted(() => {
  if (timer) window.clearInterval(timer)
})

function formatTokens(value) {
  if (value === -1 || membership.status?.unlimited) return '无限 Token'
  return `${Math.round((value || 0) / 10000)} 万 Token / 月`
}
function teamsForPlan(planId) { return allRecruitingTeams.value.filter((team) => team.plan_id === planId) }
function pageCount(planId) { return Math.max(1, Math.ceil(teamsForPlan(planId).length / 2)) }
function currentPage(planId) { return Math.min(pageByPlan.value[planId] || 0, pageCount(planId) - 1) }
function visibleTeams(planId) { return teamsForPlan(planId).slice(currentPage(planId) * 2, currentPage(planId) * 2 + 2) }
function moveTeams(planId, direction) {
  const next = currentPage(planId) + direction
  pageByPlan.value = { ...pageByPlan.value, [planId]: Math.min(Math.max(next, 0), pageCount(planId) - 1) }
}
function groupPrice(plan) { return Number(plan.group_price || Math.round(Number(plan.price || 0) * 0.8)) }
function paidCount(team) { return Number(team.paid_count ?? team.complete_count ?? team.members?.length ?? 0) }
function lockedCount(team) { return Number(team.locked_count ?? team.joined ?? paidCount(team)) }
function pendingLocks(team) { return Math.max(0, lockedCount(team) - paidCount(team)) }
function openSlots(team) { return Math.max(0, Number(team.team_size || 0) - lockedCount(team)) }
function paySlots(team) { return Math.max(0, Number(team.team_size || 0) - paidCount(team)) }
function progressWidth(team) {
  const size = Number(team.team_size || 0)
  return `${size ? Math.min(100, Math.round((paidCount(team) / size) * 100)) : 0}%`
}
function secondsLeft(team) {
  if (team.valid_end_time) {
    const parsed = Date.parse(String(team.valid_end_time).replace(' ', 'T'))
    if (!Number.isNaN(parsed)) return Math.max(0, Math.floor((parsed - nowTick.value) / 1000))
  }
  return Math.max(0, Number(team.countdown_seconds || 0) - Math.floor((Date.now() - nowTick.value) / 1000))
}
function countdownLabel(team) {
  const total = secondsLeft(team)
  const days = Math.floor(total / 86400)
  const hours = Math.floor((total % 86400) / 3600)
  const minutes = Math.floor((total % 3600) / 60)
  const seconds = total % 60
  if (days > 0) return `${days}天 ${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`
  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
}
function canJoinTeam(team) { return openSlots(team) > 0 && secondsLeft(team) > 0 }
function orderSecondsLeft(order) {
  if (!order?.expires_at) return 0
  const parsed = Date.parse(String(order.expires_at).replace(' ', 'T'))
  if (Number.isNaN(parsed)) return 0
  return Math.max(0, Math.floor((parsed - nowTick.value) / 1000))
}
function orderCountdownLabel(order) {
  const total = orderSecondsLeft(order)
  const minutes = Math.floor(total / 60)
  const seconds = total % 60
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
}
function canTestPay(order) { return !!order?.test_payment_enabled }
function openPayWindow(order) {
  if (!order?.pay_url) return
  if (order.pay_url.trim().startsWith('<form')) {
    const payWindow = window.open('', '_blank')
    if (payWindow) {
      payWindow.document.open()
      payWindow.document.write(order.pay_url)
      payWindow.document.close()
    }
    return
  }
  window.open(order.pay_url, '_blank')
}
async function buy(planId, teamId = '') {
  selectedPlan.value = planId
  message.value = ''
  const order = await membership.createOrder(planId, teamId)
  if (!order) return
  message.value = order.purchase_mode === 'group'
    ? 'Group order created. Use the sticky order bar to pay, cancel, or run local test payment.'
    : 'Order created. Use the sticky order bar to open payment or run local test payment.'
}
async function createTeam(planId) { await buy(planId, '__create__') }
async function refreshPayment() {
  if (!membership.pendingOrder?.order_id) return
  const status = await membership.confirmOrder(membership.pendingOrder.order_id)
  if (!status) return
  await membership.loadGroupTeams()
  if (status.active) {
    message.value = `${status.plan?.name || '会员'} 已开通。`
    setTimeout(() => router.replace(route.query.redirect || '/projects'), 650)
  } else if (status.message) message.value = status.message
}
async function cancelPendingOrder() {
  if (!membership.pendingOrder?.order_id) return
  const ok = await membership.cancelOrder(membership.pendingOrder.order_id)
  if (!ok) return
  await membership.loadGroupTeams()
  message.value = 'Order cancelled. Reserved group seat has been released.'
}
async function testPayPendingOrder() {
  if (!membership.pendingOrder?.order_id) return
  const status = await membership.testPayOrder(membership.pendingOrder.order_id)
  if (!status) return
  await membership.loadGroupTeams()
  if (status.active) {
    message.value = `${status.plan?.name || 'Membership'} is active.`
    setTimeout(() => router.replace(route.query.redirect || '/projects'), 650)
  } else if (status.message) message.value = status.message
}
function enterApp() { router.push(route.query.redirect || '/projects') }
</script>

<template>
  <main class="membership-page">
    <nav class="membership-nav">
      <button @click="router.push('/')">返回首页</button>
      <strong>小猫写作会员中心</strong>
      <button v-if="membership.isActive" class="enter" @click="enterApp">进入平台</button>
    </nav>

    <section class="hero">
      <p class="eyebrow">MEMBERSHIP</p>
      <h1>会员只区分 Token 额度，拼团价更低，成团后自动开通。</h1>
      <p>直接开通支付成功后立即生效；拼团先锁座，支付成功才计入成团人数，满员后自动升级会员。</p>
      <div v-if="membership.isActive" class="active-banner">当前套餐：{{ membership.currentPlan?.name }} · 剩余 {{ formatTokens(membership.remainingTokens) }}</div>
    </section>

    <section class="plans">
      <article v-for="plan in plans" :key="plan.id" class="plan-card" :class="{ featured: plan.id === 'plus', active: membership.currentPlan?.id === plan.id }">
        <div class="plan-head"><span>{{ plan.name }}</span><em v-if="plan.id === 'plus'">推荐</em></div>
        <div class="price-row">
          <div class="price">&yen;{{ plan.price }}<small>/月</small></div>
          <div class="group-price">拼团 &yen;{{ groupPrice(plan) }}</div>
        </div>
        <p class="quota">{{ formatTokens(plan.monthly_token_quota) }}</p>
        <ul><li v-for="feature in plan.features" :key="feature">{{ feature }}</li></ul>

        <div class="action-row">
          <button class="direct-btn" :disabled="membership.loading" @click="buy(plan.id)">&yen;{{ plan.price }} 直接开通</button>
          <button class="create-team-btn" :disabled="membership.loading" @click="createTeam(plan.id)">&yen;{{ groupPrice(plan) }} 开团</button>
        </div>

        <div class="group-panel">
          <div class="group-title-row"><h3>正在拼的团</h3><span>{{ teamsForPlan(plan.id).length }} 个团，每页 2 个</span></div>
          <div v-if="visibleTeams(plan.id).length" class="team-slider">
            <button class="slide-btn" :disabled="currentPage(plan.id) === 0" @click="moveTeams(plan.id, -1)">‹</button>
            <div class="team-page">
              <div v-for="team in visibleTeams(plan.id)" :key="team.team_id" class="team-card">
                <div class="team-head">
                  <strong>{{ team.hint }}</strong>
                  <span>{{ paidCount(team) }}/{{ team.team_size }} 已支付</span>
                </div>
                <div class="team-clock">
                  <span>剩余 {{ countdownLabel(team) }}</span>
                  <em>{{ lockedCount(team) }} 个名额已锁定，{{ openSlots(team) }} 个可加入</em>
                </div>
                <div class="team-progress"><i :style="{ width: progressWidth(team) }"></i></div>
                <div class="team-members">
                  <span v-for="member in team.members" :key="member.user_id || member.name" :title="member.email || member.name"><b>{{ member.avatar }}</b>{{ member.name }}</span>
                  <span v-for="slot in pendingLocks(team)" :key="`locked-${team.team_id}-${slot}`" class="locked-slot">待支付锁单</span>
                  <span v-for="slot in openSlots(team)" :key="`empty-${team.team_id}-${slot}`" class="empty-slot">可加入</span>
                </div>
                <button class="team-btn" :disabled="membership.loading || !canJoinTeam(team)" @click="buy(plan.id, team.team_id)">
                  &yen;{{ team.group_price || groupPrice(plan) }} 加入拼团
                  <small>还差 {{ paySlots(team) }} 人支付成团</small>
                </button>
              </div>
            </div>
            <button class="slide-btn" :disabled="currentPage(plan.id) >= pageCount(plan.id) - 1" @click="moveTeams(plan.id, 1)">›</button>
          </div>
          <div v-else class="empty-team">还没有正在拼的团，可以先开一个。</div>
          <div class="pager">{{ currentPage(plan.id) + 1 }} / {{ pageCount(plan.id) }}</div>
        </div>
      </article>
    </section>

    <section v-if="membership.pendingOrder" class="pending-order">
      <div class="pending-copy">
        <strong>Pending order: {{ membership.pendingOrder.order_id }}</strong>
        <p>Type: {{ membership.pendingOrder.purchase_mode === 'group' ? 'Group buy' : 'Direct' }} · Amount &yen;{{ membership.pendingOrder.amount }} · Left {{ orderCountdownLabel(membership.pendingOrder) }}</p>
      </div>
      <button class="cancel-order" :disabled="membership.loading" @click="cancelPendingOrder">Cancel order</button>
      <button v-if="membership.pendingOrder.pay_url" @click="openPayWindow(membership.pendingOrder)">Open payment page</button>
      <button class="primary" :disabled="membership.loading" @click="refreshPayment">Paid, refresh status</button>
      <button v-if="canTestPay(membership.pendingOrder)" class="test-pay" :disabled="membership.loading" @click="testPayPendingOrder">Test: mark paid</button>
    </section>

    <p v-if="message" class="message">{{ message }}</p>
    <p v-if="membership.error" class="error">{{ membership.error }}</p>
  </main>
</template>

<style scoped>
.membership-page { min-height: 100vh; padding: 24px clamp(18px, 5vw, 72px) 180px; color: #102033; background: radial-gradient(circle at 14% 8%, rgba(186, 230, 253, .76), transparent 28%), radial-gradient(circle at 90% 18%, rgba(219, 234, 254, .92), transparent 26%), linear-gradient(180deg, #f8fbff, #edf7ff 58%, #ffffff); font-family: "Noto Serif SC", "Microsoft YaHei", sans-serif; }
.membership-nav { height: 52px; display: flex; align-items: center; justify-content: space-between; }
.membership-nav button { border: 1px solid #bfdbfe; background: rgba(255,255,255,.86); color: #1d4ed8; border-radius: 999px; padding: 10px 16px; font-weight: 900; cursor: pointer; }
.membership-nav .enter { background: #2563eb; color: #fff; border-color: #2563eb; }
.hero { max-width: 980px; margin: 62px auto 32px; text-align: center; }
.eyebrow { color: #2563eb; font-weight: 950; letter-spacing: .16em; }
h1 { margin: 0; font-size: clamp(32px, 4.8vw, 56px); letter-spacing: -.04em; line-height: 1.12; }
.hero p { color: #52657d; font-size: 18px; line-height: 1.8; }
.active-banner, .message { display: inline-block; margin-top: 18px; padding: 12px 16px; border-radius: 999px; color: #1d4ed8; background: #dbeafe; font-weight: 900; }
.plans { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 20px; max-width: 1400px; margin: 0 auto; }
.plan-card { display: flex; flex-direction: column; gap: 14px; padding: 26px; border: 1px solid rgba(147,197,253,.62); border-radius: 30px; background: rgba(255,255,255,.92); box-shadow: 0 22px 58px rgba(37,99,235,.1); }
.plan-card.featured { border-color: #2563eb; transform: translateY(-8px); }
.plan-card.active { outline: 4px solid rgba(37,99,235,.12); }
.plan-head, .price-row, .team-head, .group-title-row, .action-row { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.plan-head { color: #1d4ed8; font-size: 26px; font-weight: 950; }
.plan-head em { font-style: normal; font-size: 12px; padding: 6px 10px; border-radius: 999px; background: #dbeafe; }
.price { font-size: 44px; font-weight: 950; }
.price small { color: #64748b; font-size: 15px; }
.group-price { padding: 8px 12px; border-radius: 999px; background: #ecfdf5; color: #047857; font-weight: 950; }
.quota, li, .pending-order p, .group-title-row span { color: #5d718b; line-height: 1.7; }
ul { min-height: 86px; padding-left: 18px; }
.action-row button, .team-btn, .pending-order button { width: 100%; border: 0; border-radius: 999px; padding: 13px 18px; color: #fff; font-weight: 950; cursor: pointer; }
.direct-btn { background: linear-gradient(135deg, #38bdf8, #2563eb); }
.create-team-btn { background: linear-gradient(135deg, #10b981, #0ea5e9); }
.group-panel { padding: 16px; border: 1px solid #bfdbfe; border-radius: 24px; background: linear-gradient(180deg, #f8fcff, #fff); }
.group-title-row { margin-bottom: 10px; }
.group-title-row h3 { margin: 0; color: #1e3a8a; }
.team-slider { display: grid; grid-template-columns: 40px minmax(0,1fr) 40px; gap: 10px; align-items: center; }
.slide-btn { width: 40px; height: 40px; border: 1px solid #bfdbfe; border-radius: 50%; background: white; color: #2563eb; font-size: 30px; cursor: pointer; box-shadow: 0 10px 24px rgba(37,99,235,.1); }
.slide-btn:disabled { opacity: .35; cursor: not-allowed; }
.team-page { display: grid; gap: 12px; }
.team-card { padding: 14px; border: 1px solid #bfdbfe; border-radius: 20px; background: #fff; }
.team-head { color: #0f2d6b; font-size: 15px; }
.team-head span { color: #2563eb; font-weight: 950; }
.team-clock { display: flex; justify-content: space-between; gap: 10px; margin-top: 10px; color: #64748b; font-size: 12px; }
.team-clock span { color: #0f766e; font-weight: 950; }
.team-clock em { font-style: normal; }
.team-progress { height: 9px; overflow: hidden; margin-top: 10px; border-radius: 999px; background: #e0f2fe; }
.team-progress i { display: block; height: 100%; border-radius: inherit; background: linear-gradient(90deg, #22c55e, #38bdf8, #2563eb); transition: width .35s ease; }
.team-members { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 12px; }
.team-members span { max-width: 130px; display: inline-flex; align-items: center; gap: 7px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; padding: 7px 10px; border-radius: 999px; background: #eef6ff; color: #334155; font-size: 13px; }
.team-members b { width: 24px; height: 24px; display: grid; place-items: center; border-radius: 50%; background: #bfdbfe; color: #1d4ed8; }
.team-members .empty-slot { color: #94a3b8; border: 1px dashed #bfdbfe; background: #fff; }
.team-members .locked-slot { color: #b45309; border: 1px solid #fde68a; background: #fffbeb; }
.team-btn { margin-top: 12px; display: grid; gap: 2px; background: linear-gradient(135deg, #22c55e, #0ea5e9, #2563eb); }
.team-btn small { color: rgba(255,255,255,.82); font-size: 11px; }
.empty-team { padding: 18px; border: 1px dashed #bfdbfe; border-radius: 18px; color: #64748b; text-align: center; }
.pager { margin-top: 10px; text-align: center; color: #1d4ed8; font-weight: 900; }
.pending-order { position: fixed; left: clamp(16px, 4vw, 56px); right: clamp(16px, 4vw, 56px); bottom: 22px; z-index: 30; display: grid; grid-template-columns: minmax(260px, 1fr) auto auto auto auto; gap: 14px; align-items: center; max-width: 1280px; margin: 0 auto; padding: 18px; border: 1px solid #93c5fd; border-radius: 24px; background: rgba(255,255,255,.96); box-shadow: 0 24px 70px rgba(37,99,235,.18); backdrop-filter: blur(18px); }
.pending-order button { width: auto; white-space: nowrap; background: #eff6ff; color: #2563eb; }
.pending-order .primary { color: #fff; background: linear-gradient(135deg, #22c55e, #2563eb); }
.pending-order .cancel-order { color: #b91c1c; background: #fff1f2; }
.pending-order .test-pay { color: #fff; background: linear-gradient(135deg, #f59e0b, #0ea5e9); }
.error { text-align: center; color: #dc2626; font-weight: 900; }
.message { display: block; width: fit-content; margin: 24px auto 0; }
button:disabled { opacity: .55; cursor: wait; }
@media (max-width: 1180px) { .plans { grid-template-columns: 1fr; } .plan-card.featured { transform: none; } }
@media (max-width: 720px) { .pending-order, .team-slider, .action-row { grid-template-columns: 1fr; display: grid; } .slide-btn { width: 100%; border-radius: 999px; } .pending-order button { width: 100%; } }
</style>
