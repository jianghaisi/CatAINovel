<script setup>
import { onMounted, ref } from 'vue'
import { preferencesApi } from '../api'

const loading = ref(false)
const saving = ref(false)
const message = ref('')
const realms = ref([])
const systemUiTemplate = ref('[宿主：{name} | 等级：{level}]')

function toast(text) {
  message.value = text
  setTimeout(() => {
    if (message.value === text) message.value = ''
  }, 2600)
}

function normalizeOrder() {
  realms.value = realms.value.map((item, index) => ({
    ...item,
    order: index + 1,
    id: item.id || `realm-${Date.now()}-${index}`
  }))
}

function addRealm() {
  realms.value.push({
    id: `realm-${Date.now()}`,
    name: '',
    features: '',
    order: realms.value.length + 1
  })
}

function removeRealm(index) {
  realms.value.splice(index, 1)
  normalizeOrder()
}

function moveRealm(index, direction) {
  const target = index + direction
  if (target < 0 || target >= realms.value.length) return
  const copy = [...realms.value]
  const [item] = copy.splice(index, 1)
  copy.splice(target, 0, item)
  realms.value = copy
  normalizeOrder()
}

async function loadPreferences() {
  loading.value = true
  try {
    const { data } = await preferencesApi.get()
    realms.value = [...(data.realms || [])].sort((a, b) => (a.order || 0) - (b.order || 0))
    systemUiTemplate.value = data.system_ui_template || systemUiTemplate.value
  } catch (error) {
    toast('加载境界配置失败')
  } finally {
    loading.value = false
  }
}

async function savePreferences() {
  normalizeOrder()
  const validRealms = realms.value.filter(item => item.name.trim())
  if (!validRealms.length) {
    toast('至少保留一个境界')
    return
  }
  saving.value = true
  try {
    await preferencesApi.update({
      realms: validRealms,
      system_ui_template: systemUiTemplate.value
    })
    realms.value = validRealms
    toast('境界配置已保存')
  } catch (error) {
    toast('保存境界配置失败')
  } finally {
    saving.value = false
  }
}

onMounted(loadPreferences)
</script>

<template>
  <div class="config-page">
    <section class="hero-card">
      <p class="kicker">Realm Config</p>
      <h1>境界管理</h1>
      <p>这里定义 Writer Agent 可使用的境界顺序。正文生成前会校验主角当前境界是否存在于这张表里，避免“凭空升级”。</p>
    </section>

    <section class="panel">
      <div class="panel-head">
        <div>
          <h2>自定义境界列表</h2>
          <p>可添加“炼气期”“筑基期”等，并通过上下按钮调整先后顺序。</p>
        </div>
        <button class="btn ghost" @click="addRealm">添加境界</button>
      </div>

      <div v-if="loading" class="empty">正在加载配置...</div>
      <div v-else class="realm-list">
        <article v-for="(realm, index) in realms" :key="realm.id || index" class="realm-row">
          <div class="order-badge">{{ index + 1 }}</div>
          <input v-model="realm.name" placeholder="境界名称，如：炼气期" />
          <input v-model="realm.features" placeholder="特征/升级条件，如：引气入体，灵力初成" />
          <div class="row-actions">
            <button class="mini" :disabled="index === 0" @click="moveRealm(index, -1)">上移</button>
            <button class="mini" :disabled="index === realms.length - 1" @click="moveRealm(index, 1)">下移</button>
            <button class="mini danger" @click="removeRealm(index)">删除</button>
          </div>
        </article>
      </div>

      <div class="actions">
        <button class="btn solid" :disabled="saving" @click="savePreferences">
          {{ saving ? '保存中...' : '保存境界配置' }}
        </button>
      </div>
    </section>

    <div v-if="message" class="toast">{{ message }}</div>
  </div>
</template>

<style scoped>
.config-page { height: 100%; min-height: 0; padding: 2rem; overflow-y: auto; background: radial-gradient(circle at top left, rgba(96,165,250,.16), transparent 30%), linear-gradient(180deg, #eef7ff, #ffffff); box-sizing: border-box; }
.hero-card, .panel { max-width: 1100px; margin: 0 auto 1.25rem; background: rgba(255,255,255,.88); border: 1px solid rgba(37,99,235,.13); border-radius: 24px; box-shadow: 0 18px 42px rgba(37,99,235,.08); backdrop-filter: blur(10px); }
.hero-card { padding: 1.75rem 2rem; }
.kicker { margin: 0 0 .4rem; color: #2563eb; letter-spacing: .16em; text-transform: uppercase; font-size: .78rem; font-weight: 800; }
h1, h2 { margin: 0; color: #0f172a; }
.hero-card p, .panel p { color: #64748b; line-height: 1.7; }
.panel { display: flex; flex-direction: column; max-height: calc(100vh - 280px); min-height: 360px; padding: 1.25rem; }
.panel-head { display: flex; justify-content: space-between; gap: 1rem; align-items: center; margin-bottom: 1rem; }
.realm-list { display: grid; gap: .75rem; overflow-y: auto; padding-right: .35rem; min-height: 0; }
.realm-list::-webkit-scrollbar { width: 8px; }
.realm-list::-webkit-scrollbar-thumb { background: #bfdbfe; border-radius: 999px; }
.realm-row { display: grid; grid-template-columns: 48px minmax(150px, 1fr) minmax(220px, 2fr) auto; gap: .75rem; align-items: center; padding: .85rem; border-radius: 18px; background: rgba(248,251,255,.96); border: 1px solid rgba(37,99,235,.12); }
.order-badge { display: grid; place-items: center; height: 38px; border-radius: 12px; background: #dbeafe; color: #1d4ed8; font-weight: 800; }
input { width: 100%; box-sizing: border-box; border: 1px solid rgba(37,99,235,.16); border-radius: 12px; padding: .72rem .8rem; background: #fff; color: #0f172a; outline: none; }
input:focus { border-color: rgba(37,99,235,.5); box-shadow: 0 0 0 4px rgba(37,99,235,.09); }
.row-actions { display: flex; gap: .45rem; }
.btn, .mini { border: none; border-radius: 999px; font-weight: 700; cursor: pointer; }
.btn { padding: .75rem 1.1rem; }
.btn.solid { color: #fff; background: linear-gradient(135deg, #60a5fa, #2563eb); box-shadow: 0 12px 24px rgba(37,99,235,.2); }
.btn.ghost, .mini { color: #1e40af; background: #fff; border: 1px solid rgba(37,99,235,.16); }
.mini { padding: .45rem .65rem; white-space: nowrap; }
.mini.danger { color: #a33a2a; }
button:disabled { opacity: .45; cursor: not-allowed; }
.actions { position: sticky; bottom: -1.25rem; display: flex; justify-content: flex-end; margin: 1rem -1.25rem -1.25rem; padding: 1rem 1.25rem; background: rgba(255,255,255,.88); border-top: 1px solid rgba(37,99,235,.12); backdrop-filter: blur(10px); border-radius: 0 0 24px 24px; }
.empty { padding: 2rem; color: #64748b; }
.toast { position: fixed; right: 1.5rem; bottom: 1.5rem; padding: .85rem 1rem; border-radius: 14px; background: rgba(15,23,42,.94); color: #fff; }
@media (max-width: 900px) { .realm-row { grid-template-columns: 40px 1fr; } .row-actions { grid-column: 2; } }
</style>
