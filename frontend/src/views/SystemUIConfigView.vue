<script setup>
import { computed, onMounted, ref } from 'vue'
import { preferencesApi } from '../api'

const loading = ref(false)
const saving = ref(false)
const message = ref('')
const realms = ref([])
const systemUiTemplate = ref('[宿主：{name} | 等级：{level}]')

const preview = computed(() => {
  return systemUiTemplate.value
    .replaceAll('{name}', '林凡')
    .replaceAll('{level}', realms.value[0]?.name || '炼气期')
    .replaceAll('{exp}', '120/300')
    .replaceAll('{task}', '完成今日修炼')
})

function toast(text) {
  message.value = text
  setTimeout(() => {
    if (message.value === text) message.value = ''
  }, 2600)
}

async function loadPreferences() {
  loading.value = true
  try {
    const { data } = await preferencesApi.get()
    realms.value = data.realms || []
    systemUiTemplate.value = data.system_ui_template || systemUiTemplate.value
  } catch (error) {
    toast('加载系统模版失败')
  } finally {
    loading.value = false
  }
}

async function saveTemplate() {
  if (!systemUiTemplate.value.trim()) {
    toast('系统模版不能为空')
    return
  }
  saving.value = true
  try {
    await preferencesApi.update({
      realms: realms.value,
      system_ui_template: systemUiTemplate.value.trim()
    })
    toast('系统模版已保存')
  } catch (error) {
    const detail = error?.response?.data?.detail
    toast(detail || '保存系统模版失败')
  } finally {
    saving.value = false
  }
}

onMounted(loadPreferences)
</script>

<template>
  <div class="config-page">
    <section class="hero-card">
      <p class="kicker">System UI Template</p>
      <h1>系统模版</h1>
      <p>定义金手指/系统流弹窗的固定文本格式。Writer Agent 会把它注入 Prompt，并在正文生成后检查系统面板是否完全匹配。</p>
    </section>

    <section class="panel">
      <div class="panel-head">
        <div>
          <h2>模版字符串</h2>
          <p>支持花括号变量，例如 <code>{name}</code>、<code>{level}</code>、<code>{exp}</code>。固定字符会被严格锁定。</p>
        </div>
      </div>

      <div v-if="loading" class="empty">正在加载配置...</div>
      <template v-else>
        <textarea
          v-model="systemUiTemplate"
          class="template-editor"
          spellcheck="false"
          placeholder="[宿主：{name} | 等级：{level}]"
        />

        <div class="preview-card">
          <div class="preview-title">预览</div>
          <pre>{{ preview }}</pre>
        </div>

        <div class="hint-card">
          <strong>校验规则</strong>
          <span>正文里凡是类似系统面板的行，都必须保持同样的括号、分隔符和固定文字；只允许变量值变化。</span>
        </div>

        <div class="actions">
          <button class="btn solid" :disabled="saving" @click="saveTemplate">
            {{ saving ? '保存中...' : '保存系统模版' }}
          </button>
        </div>
      </template>
    </section>

    <div v-if="message" class="toast">{{ message }}</div>
  </div>
</template>

<style scoped>
.config-page { height: 100%; min-height: 0; box-sizing: border-box; padding: 2rem; overflow-y: auto; background: radial-gradient(circle at top left, rgba(96,165,250,.16), transparent 30%), linear-gradient(180deg, #eef7ff, #ffffff); }
.hero-card, .panel { max-width: 1040px; margin: 0 auto 1.25rem; background: rgba(255,255,255,.88); border: 1px solid rgba(37,99,235,.13); border-radius: 24px; box-shadow: 0 18px 42px rgba(37,99,235,.08); backdrop-filter: blur(10px); }
.hero-card { padding: 1.75rem 2rem; }
.kicker { margin: 0 0 .4rem; color: #2563eb; letter-spacing: .16em; text-transform: uppercase; font-size: .78rem; font-weight: 800; }
h1, h2 { margin: 0; color: #0f172a; }
.hero-card p, .panel p { color: #64748b; line-height: 1.7; }
.panel { display: flex; flex-direction: column; max-height: calc(100vh - 260px); min-height: 0; padding: 1.25rem; overflow-y: auto; }
.panel::-webkit-scrollbar, .template-editor::-webkit-scrollbar { width: 8px; }
.panel::-webkit-scrollbar-thumb, .template-editor::-webkit-scrollbar-thumb { background: #bfdbfe; border-radius: 999px; }
.panel-head { display: flex; justify-content: space-between; gap: 1rem; align-items: center; margin-bottom: 1rem; }
code { padding: .12rem .35rem; border-radius: 6px; background: #dbeafe; color: #1d4ed8; }
.template-editor { width: 100%; min-height: 220px; box-sizing: border-box; border: 1px solid rgba(37,99,235,.16); border-radius: 18px; padding: 1rem; resize: vertical; background: #fff; color: #0f172a; line-height: 1.7; font-family: "SFMono-Regular", Consolas, monospace; font-size: .95rem; outline: none; }
.template-editor:focus { border-color: rgba(37,99,235,.48); box-shadow: 0 0 0 4px rgba(37,99,235,.1); }
.preview-card, .hint-card { margin-top: 1rem; padding: 1rem; border-radius: 18px; border: 1px solid rgba(37,99,235,.12); background: rgba(248,251,255,.96); }
.preview-title { font-weight: 800; color: #1d4ed8; margin-bottom: .5rem; }
pre { margin: 0; white-space: pre-wrap; color: #0f172a; font-family: "SFMono-Regular", Consolas, monospace; }
.hint-card { display: grid; gap: .35rem; color: #64748b; }
.btn { border: none; border-radius: 999px; padding: .75rem 1.1rem; font-weight: 700; cursor: pointer; }
.btn.solid { color: #fff; background: linear-gradient(135deg, #60a5fa, #2563eb); box-shadow: 0 12px 24px rgba(37,99,235,.2); }
.btn:disabled { opacity: .45; cursor: not-allowed; }
.actions { position: sticky; bottom: -1.25rem; display: flex; justify-content: flex-end; margin: 1rem -1.25rem -1.25rem; padding: 1rem 1.25rem; background: rgba(255,255,255,.88); border-top: 1px solid rgba(37,99,235,.12); backdrop-filter: blur(10px); border-radius: 0 0 24px 24px; }
.empty { padding: 2rem; color: #64748b; }
.toast { position: fixed; right: 1.5rem; bottom: 1.5rem; padding: .85rem 1rem; border-radius: 14px; background: rgba(15,23,42,.94); color: #fff; }
</style>
