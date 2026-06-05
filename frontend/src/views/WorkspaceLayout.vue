<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { useProjectStore } from '../stores/project'
import { useAuthStore } from '../stores/auth'
import { useMembershipStore } from '../stores/membership'
import { aiApi } from '../api'

const route = useRoute()
const router = useRouter()
const projectStore = useProjectStore()
const authStore = useAuthStore()
const membershipStore = useMembershipStore()

const chatInput = ref('')
const chatLoading = ref(false)
const accountMenuOpen = ref(false)
const chatMessages = ref([
  { role: 'assistant', content: '喵，我在右侧陪你规划剧情、检查设定，或者帮你整理下一章思路。' }
])

const navItems = [
  { path: '/workspace/dashboard', icon: '⌂', label: '仪表盘', group: '资源管理器' },
  { path: '/workspace/project', icon: '▦', label: '项目管理', group: '资源管理器' },
  { path: '/workspace/pipeline', icon: '✦', label: '灵感成书', group: '创作' },
  { path: '/workspace/timeline', icon: '↳', label: '事件时间线', group: '创作' },
  { path: '/workspace/outline', icon: '☰', label: '大纲编辑', group: '创作' },
  { path: '/workspace/write', icon: '✎', label: '章节创作', group: '创作' },
  { path: '/workspace/prompts', icon: '⚙', label: '提示词配置', group: '配置' },
  { path: '/workspace/realms', icon: '⌁', label: '境界管理', group: '配置' },
  { path: '/workspace/system-ui', icon: '▣', label: '系统模板', group: '配置' },
  { path: '/workspace/rag', icon: '⌕', label: 'RAG 检索', group: '资料' },
  { path: '/workspace/characters', icon: '♙', label: '角色管理', group: '资料' },
  { path: '/workspace/relations', icon: '⌘', label: '关系图谱', group: '资料' },
  { path: '/workspace/admin', icon: '⚑', label: '超级管理员', group: '系统', adminOnly: true }
]

const visibleNavItems = computed(() => navItems.filter((item) => !item.adminOnly || authStore.user?.role === 'admin'))

const groupedNavItems = computed(() => {
  const groups = []
  for (const item of visibleNavItems.value) {
    let group = groups.find((entry) => entry.name === item.group)
    if (!group) {
      group = { name: item.group, items: [] }
      groups.push(group)
    }
    group.items.push(item)
  }
  return groups
})

const accountName = computed(() => authStore.user?.name || authStore.user?.email?.split('@')[0] || '小猫用户')
const accountEmail = computed(() => authStore.user?.email || authStore.user?.id || '未登录账号')
const accountInitial = computed(() => accountName.value.slice(0, 1).toUpperCase() || '喵')
const memberLabel = computed(() => membershipStore.isActive ? membershipStore.currentPlan?.name || '会员' : 'Free')

onMounted(() => {
  authStore.bootstrap()
  membershipStore.refresh()
  if (projectStore.projectRoot && !projectStore.title) {
    projectStore.fetchStatus()
  }
  window.addEventListener('click', closeAccountMenu)
})

onBeforeUnmount(() => {
  window.removeEventListener('click', closeAccountMenu)
})

function closeAccountMenu() {
  accountMenuOpen.value = false
}

function toggleAccountMenu() {
  accountMenuOpen.value = !accountMenuOpen.value
}

function formatTokens(value) {
  const tokens = Number(value || 0)
  if (tokens >= 10000) return `${(tokens / 10000).toFixed(tokens >= 100000 ? 0 : 1)}万`
  return `${tokens}`
}

async function checkin() {
  await membershipStore.checkin()
}

function goBackToProjects() {
  projectStore.clearProject()
  router.push('/projects')
}

function goProfile() {
  accountMenuOpen.value = false
  router.push('/projects')
}

function goMembership() {
  accountMenuOpen.value = false
  router.push('/membership')
}

function isActiveRoute(itemPath) {
  if (itemPath === '/workspace/dashboard') return route.path === '/workspace/dashboard'
  return route.path.startsWith(itemPath)
}

async function sendChat() {
  const value = chatInput.value.trim()
  if (!value || chatLoading.value) return
  chatMessages.value.push({ role: 'user', content: value })
  chatInput.value = ''
  chatLoading.value = true
  try {
    const history = chatMessages.value.map((message) => ({
      role: message.role,
      content: message.content
    }))
    const { data } = await aiApi.chat(history, true)
    chatMessages.value.push({ role: 'assistant', content: data.message || '喵，模型没有返回内容。' })
  } catch (error) {
    chatMessages.value.push({
      role: 'assistant',
      content: error?.response?.data?.detail || error.message || '对话失败，请先检查 API 配置。'
    })
  } finally {
    chatLoading.value = false
  }
}

async function logout() {
  accountMenuOpen.value = false
  await authStore.logout()
  membershipStore.clearLocal()
  projectStore.clearProject()
  router.push('/login')
}

async function switchAccount() {
  await logout()
}
</script>

<template>
  <div class="ide-shell">
    <aside class="explorer-panel">
      <header class="explorer-header">
        <span>资源管理器</span>
        <button @click="goBackToProjects" title="返回项目列表">•••</button>
      </header>

      <section class="project-root">
        <div class="root-row">
          <span class="chevron">⌄</span>
          <strong :title="projectStore.title || '未命名项目'">{{ projectStore.title || '未命名项目' }}</strong>
        </div>
        <p>{{ projectStore.genre || '未设置题材' }} · {{ projectStore.totalChapters }} 章 · {{ (projectStore.totalWords / 10000).toFixed(1) }} 万字</p>
      </section>

      <nav class="tree-nav" aria-label="工作台菜单">
        <section v-for="group in groupedNavItems" :key="group.name" class="tree-group">
          <div class="group-title">⌄ {{ group.name }}</div>
          <RouterLink
            v-for="item in group.items"
            :key="item.path"
            :to="item.path"
            class="tree-item"
            :class="{ active: isActiveRoute(item.path) }"
          >
            <span class="tree-icon">{{ item.icon }}</span>
            <span>{{ item.label }}</span>
          </RouterLink>
        </section>
      </nav>

      <footer class="account-panel" @click.stop>
        <button class="account-summary" :title="`${accountName} / ${accountEmail}`" @click="toggleAccountMenu">
          <span class="summary-avatar">{{ accountInitial }}</span>
          <span class="summary-text">
            <strong>{{ accountName }}</strong>
            <small>{{ accountEmail }}</small>
          </span>
          <span class="summary-badge">{{ memberLabel }}</span>
          <span class="summary-caret">⌃</span>
        </button>

        <div class="quota-strip">
          <span>{{ memberLabel }}</span>
          <b>{{ formatTokens(membershipStore.remainingTokens) }} Token</b>
        </div>

        <div class="account-actions">
          <button :disabled="membershipStore.loading || membershipStore.checkedInToday" @click="checkin">
            {{ membershipStore.checkedInToday ? '已签到' : '签到' }}
          </button>
          <button @click="goMembership">会员</button>
        </div>

        <div v-if="accountMenuOpen" class="account-menu" role="menu">
          <div class="menu-profile">
            <span class="menu-avatar">{{ accountInitial }}</span>
            <div>
              <strong :title="accountName">{{ accountName }}</strong>
              <p :title="accountEmail">{{ accountEmail }}</p>
              <em>{{ memberLabel }} · {{ formatTokens(membershipStore.remainingTokens) }} Token</em>
            </div>
          </div>
          <button @click="goProfile">个人中心</button>
          <button @click="goMembership">会员中心</button>
          <button @click="switchAccount">切换账号</button>
          <button class="danger" @click="logout">退出登录</button>
        </div>
      </footer>
    </aside>

    <main class="editor-area">
      <div class="editor-content">
        <RouterView />
      </div>
    </main>

    <aside class="ai-panel">
      <header class="ai-header">
        <div>
          <strong>模型对话</strong>
          <p>剧情、设定、章纲助手</p>
        </div>
        <button title="新对话" @click="chatMessages = [{ role: 'assistant', content: '新对话已开始。把剧情问题交给我吧，喵。' }]">＋</button>
      </header>
      <div class="chat-list">
        <div v-for="(message, index) in chatMessages" :key="index" class="chat-bubble" :class="message.role">
          {{ message.content }}
        </div>
      </div>
      <footer class="chat-input">
        <textarea v-model="chatInput" placeholder="和模型讨论剧情走向…" rows="3" @keydown.enter.exact.prevent="sendChat"></textarea>
        <button :disabled="chatLoading" @click="sendChat">{{ chatLoading ? '思考中' : '发送' }}</button>
      </footer>
    </aside>
  </div>
</template>

<style scoped>
.ide-shell {
  width: 100%;
  height: 100vh;
  min-height: 0;
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr) 360px;
  background: #f8fafc;
  color: #1e293b;
  overflow: hidden;
}

.explorer-panel {
  position: relative;
  min-height: 0;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, .9);
  border-right: 1px solid #dbe3ee;
  overflow: visible;
  z-index: 3;
}

.ai-panel {
  min-height: 0;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, .9);
  border-left: 1px solid #dbe3ee;
}

.explorer-header,
.ai-header {
  height: 48px;
  flex: 0 0 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 14px;
  border-bottom: 1px solid #e2e8f0;
}

.explorer-header {
  font-size: 13px;
  font-weight: 900;
  color: #475569;
}

.explorer-header button,
.ai-header button {
  border: 0;
  background: transparent;
  cursor: pointer;
  color: #64748b;
}

.project-root {
  flex: 0 0 auto;
  padding: 12px 14px;
  border-bottom: 1px solid #e2e8f0;
}

.root-row {
  display: flex;
  gap: 6px;
  align-items: center;
  min-width: 0;
}

.root-row strong {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.project-root p {
  margin: 6px 0 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 12px;
  color: #64748b;
}

.tree-nav {
  flex: 1 1 auto;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 8px;
  scrollbar-gutter: stable;
}

.tree-nav::-webkit-scrollbar,
.chat-list::-webkit-scrollbar {
  width: 10px;
}

.tree-nav::-webkit-scrollbar-thumb,
.chat-list::-webkit-scrollbar-thumb {
  border: 3px solid transparent;
  border-radius: 999px;
  background: #bfdbfe;
  background-clip: padding-box;
}

.tree-group {
  margin-bottom: 10px;
}

.group-title {
  padding: 7px 8px;
  font-size: 12px;
  font-weight: 900;
  color: #64748b;
}

.tree-item {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 10px 7px 22px;
  border-radius: 8px;
  color: #334155;
  text-decoration: none;
  font-size: 13px;
}

.tree-item span:last-child {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tree-item:hover,
.tree-item.active {
  background: #eaf4ff;
  color: #1d4ed8;
}

.tree-icon {
  width: 18px;
  flex: 0 0 18px;
  text-align: center;
  color: #64748b;
}

.account-panel {
  position: relative;
  flex: 0 0 auto;
  display: grid;
  gap: 8px;
  padding: 10px;
  border-top: 1px solid #dbeafe;
  background: linear-gradient(180deg, #f8fbff, #eef6ff);
  box-shadow: 0 -10px 24px rgba(37, 99, 235, .06);
}

.account-summary {
  width: 100%;
  min-height: 58px;
  display: grid;
  grid-template-columns: 34px minmax(0, 1fr) auto 12px;
  align-items: center;
  gap: 9px;
  padding: 8px;
  border: 1px solid #dbeafe;
  border-radius: 16px;
  background: rgba(255, 255, 255, .82);
  color: #0f172a;
  cursor: pointer;
  text-align: left;
}

.summary-avatar,
.menu-avatar {
  width: 34px;
  height: 34px;
  display: grid;
  place-items: center;
  border-radius: 12px;
  background: linear-gradient(135deg, #93c5fd, #2563eb);
  color: white;
  font-weight: 950;
}

.summary-text {
  min-width: 0;
  display: grid;
  gap: 2px;
  line-height: 1.2;
}

.summary-text strong,
.summary-text small,
.menu-profile strong,
.menu-profile p {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.summary-text strong {
  font-size: 13px;
  font-weight: 950;
}

.summary-text small {
  font-size: 11px;
  color: #64748b;
}

.summary-badge {
  max-width: 54px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 4px 7px;
  border-radius: 999px;
  background: #dbeafe;
  color: #1d4ed8;
  font-size: 11px;
  font-weight: 950;
}

.summary-caret {
  color: #94a3b8;
  font-size: 12px;
}

.quota-strip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  min-width: 0;
  padding: 7px 9px;
  border: 1px solid #bfdbfe;
  border-radius: 14px;
  background: #fff;
  color: #1d4ed8;
  font-size: 12px;
  line-height: 1.3;
}

.quota-strip span,
.quota-strip b {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.quota-strip span {
  font-weight: 950;
}

.quota-strip b {
  color: #0f172a;
}

.account-actions {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px;
}

.account-panel button {
  font: inherit;
}

.account-actions button,
.account-menu button {
  border: 1px solid #bfdbfe;
  border-radius: 999px;
  background: white;
  color: #2563eb;
  padding: 7px 10px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 850;
}

.account-actions button:disabled {
  opacity: .55;
  cursor: not-allowed;
}

.account-menu {
  position: absolute;
  left: 10px;
  right: 10px;
  bottom: calc(100% + 10px);
  z-index: 20;
  display: grid;
  gap: 8px;
  padding: 10px;
  border: 1px solid #bfdbfe;
  border-radius: 20px;
  background: rgba(255, 255, 255, .98);
  box-shadow: 0 22px 60px rgba(15, 23, 42, .16);
}

.menu-profile {
  min-width: 0;
  display: grid;
  grid-template-columns: 38px minmax(0, 1fr);
  gap: 10px;
  align-items: center;
  padding: 8px;
  border-radius: 16px;
  background: #eff6ff;
}

.menu-profile p {
  margin: 2px 0;
  color: #64748b;
  font-size: 12px;
}

.menu-profile em {
  display: block;
  color: #1d4ed8;
  font-size: 12px;
  font-style: normal;
  font-weight: 900;
}

.account-menu button {
  width: 100%;
  text-align: left;
  border-radius: 12px;
  color: #334155;
}

.account-menu button:hover {
  background: #eff6ff;
  color: #1d4ed8;
}

.account-menu .danger {
  color: #dc2626;
  border-color: #fecaca;
}

.editor-area {
  height: 100vh;
  min-width: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #fff;
}

.editor-content {
  flex: 1;
  min-height: 0;
  overflow: auto;
  scrollbar-gutter: stable both-edges;
  background: #fff;
  overscroll-behavior: contain;
}

.editor-content::-webkit-scrollbar {
  width: 12px;
  height: 12px;
}

.editor-content::-webkit-scrollbar-thumb {
  border: 3px solid transparent;
  border-radius: 999px;
  background: #93c5fd;
  background-clip: padding-box;
}

.editor-content::-webkit-scrollbar-track {
  background: #eff6ff;
}

.editor-content :deep(> *) {
  min-width: 980px;
}

.ai-header p {
  margin: 2px 0 0;
  font-size: 12px;
  color: #64748b;
}

.chat-list {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.chat-bubble {
  padding: 10px 12px;
  border-radius: 14px;
  font-size: 13px;
  line-height: 1.55;
}

.chat-bubble.assistant {
  background: #eef6ff;
  color: #1e3a8a;
  align-self: flex-start;
}

.chat-bubble.user {
  background: #2563eb;
  color: white;
  align-self: flex-end;
}

.chat-input {
  flex: 0 0 auto;
  padding: 12px;
  border-top: 1px solid #e2e8f0;
  background: #f8fbff;
}

.chat-input textarea {
  width: 100%;
  resize: none;
  border: 1px solid #bfdbfe;
  border-radius: 12px;
  padding: 10px;
  outline: none;
}

.chat-input button {
  width: 100%;
  margin-top: 8px;
  border: 0;
  border-radius: 999px;
  padding: 9px;
  background: #2563eb;
  color: white;
  font-weight: 900;
  cursor: pointer;
}

@media (max-width: 1180px) {
  .ide-shell {
    grid-template-columns: 260px minmax(0, 1fr);
  }

  .ai-panel {
    display: none;
  }
}

@media (max-height: 680px) {
  .account-panel {
    gap: 6px;
    padding: 8px;
  }

  .account-summary {
    min-height: 52px;
  }

  .quota-strip {
    padding-block: 6px;
  }
}
</style>
