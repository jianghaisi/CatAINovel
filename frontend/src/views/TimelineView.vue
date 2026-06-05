<script setup>
import { computed, onMounted, ref } from 'vue'
import { timelineApi } from '../api'

const loading = ref(false)
const saving = ref(false)
const generating = ref(false)
const expanding = ref(false)
const message = ref('')
const events = ref([])
const selectedId = ref('')
const overwrite = ref(false)
const guidance = ref('')

const selectedEvent = computed(() => events.value.find(item => item.id === selectedId.value) || events.value[0] || null)

function toast(text) {
  message.value = text
  setTimeout(() => {
    if (message.value === text) message.value = ''
  }, 2800)
}

function normalizeEvents() {
  events.value = events.value.map((item, index) => ({
    ...item,
    order: index + 1,
    characters: Array.isArray(item.characters) ? item.characters : splitList(item.characters),
    foreshadowing: Array.isArray(item.foreshadowing) ? item.foreshadowing : splitList(item.foreshadowing),
    related_chapters: Array.isArray(item.related_chapters) ? item.related_chapters : []
  }))
}

function splitList(value) {
  if (Array.isArray(value)) return value
  return String(value || '')
    .split(/[,，、/|；;]/)
    .map(item => item.trim())
    .filter(Boolean)
}

function joinList(value) {
  return Array.isArray(value) ? value.join('、') : String(value || '')
}

function updateListField(event, key, value) {
  event[key] = splitList(value)
}

async function loadTimeline() {
  loading.value = true
  try {
    const { data } = await timelineApi.get()
    events.value = (data.events || []).sort((a, b) => (a.order || 0) - (b.order || 0))
    selectedId.value = events.value[0]?.id || ''
  } catch (error) {
    toast('加载事件时间线失败')
  } finally {
    loading.value = false
  }
}

async function saveTimeline() {
  saving.value = true
  try {
    normalizeEvents()
    await timelineApi.update(events.value)
    toast('事件时间线已保存')
  } catch (error) {
    toast('保存事件时间线失败')
  } finally {
    saving.value = false
  }
}

async function generateFromOutline() {
  generating.value = true
  try {
    const { data } = await timelineApi.generateFromOutline({
      overwrite: overwrite.value,
      guidance: guidance.value
    })
    events.value = data.events || []
    selectedId.value = events.value[0]?.id || ''
    toast('已从总纲生成事件时间线')
  } catch (error) {
    const detail = error?.response?.data?.detail
    toast(detail || '生成事件时间线失败')
  } finally {
    generating.value = false
  }
}

async function expandTimeline() {
  if (!events.value.length) {
    toast('请先从总纲提取基础时间线')
    return
  }
  expanding.value = true
  try {
    const { data } = await timelineApi.expand({
      event_ids: [],
      overwrite: false,
      guidance: guidance.value
    })
    events.value = data.events || []
    selectedId.value = selectedId.value || events.value[0]?.id || ''
    toast('已拓展前因后果、因果影响与伏笔')
  } catch (error) {
    const detail = error?.response?.data?.detail
    toast(detail || '拓展事件因果失败')
  } finally {
    expanding.value = false
  }
}

function addEvent() {
  const id = `event-${Date.now()}`
  events.value.push({
    id,
    order: events.value.length + 1,
    time: '',
    characters: [],
    location: '',
    event: '',
    cause: '',
    consequence: '',
    causal_effect: '',
    foreshadowing: [],
    related_chapters: [],
    source_outline_id: 'manual'
  })
  selectedId.value = id
}

function removeEvent(id) {
  const index = events.value.findIndex(item => item.id === id)
  if (index < 0) return
  events.value.splice(index, 1)
  normalizeEvents()
  selectedId.value = events.value[Math.min(index, events.value.length - 1)]?.id || ''
}

function moveEvent(index, direction) {
  const target = index + direction
  if (target < 0 || target >= events.value.length) return
  const copy = [...events.value]
  const [item] = copy.splice(index, 1)
  copy.splice(target, 0, item)
  events.value = copy
  normalizeEvents()
}

onMounted(loadTimeline)
</script>

<template>
  <div class="timeline-page">
    <section class="timeline-hero">
      <div>
        <p class="eyebrow">Outline → Timeline → Chapters</p>
        <h1>事件时间线</h1>
        <p class="hero-copy">
          现在分两步走：先从总纲提取基础时间线，只得到时间、地点、人物、事件；确认顺序没问题后，再选择拓展前因后果、因果影响和伏笔。这样章节大纲会更稳，不会一开始就被 AI 扩写带偏。
        </p>
      </div>
      <div class="hero-card">
        <span class="hero-number">{{ events.length }}</span>
        <span>个事件节点</span>
      </div>
    </section>

    <section class="toolbar">
      <div class="workflow-steps">
        <div class="step-chip active"><strong>1</strong><span>从总纲提取：时间 / 地点 / 人物 / 事件</span></div>
        <div class="step-chip"><strong>2</strong><span>可选拓展：前因后果 / 因果影响 / 伏笔</span></div>
      </div>
      <div class="generate-box">
        <input v-model="guidance" placeholder="可选：提取或拓展时的额外要求，例如：减少跳跃、强化伏笔" />
        <label class="check">
          <input v-model="overwrite" type="checkbox" />
          覆盖现有时间线
        </label>
      </div>
      <div class="toolbar-actions">
        <button class="btn ghost" @click="addEvent">新增事件</button>
        <button class="btn ghost" :disabled="generating" @click="generateFromOutline">
          {{ generating ? '提取中...' : '1. 提取基础时间线' }}
        </button>
        <button class="btn ghost" :disabled="expanding || !events.length" @click="expandTimeline">
          {{ expanding ? '拓展中...' : '2. 拓展因果伏笔' }}
        </button>
        <button class="btn solid" :disabled="saving" @click="saveTimeline">
          {{ saving ? '保存中...' : '保存时间线' }}
        </button>
      </div>
    </section>

    <div v-if="loading" class="empty-card">正在加载时间线...</div>

    <section v-else class="timeline-shell">
      <aside class="event-list">
        <article
          v-for="(item, index) in events"
          :key="item.id"
          class="event-pill"
          :class="{ active: selectedEvent?.id === item.id }"
          @click="selectedId = item.id"
        >
          <div class="event-order">{{ index + 1 }}</div>
          <div class="event-summary">
            <strong>{{ item.time || '未填写时间' }}</strong>
            <span>{{ item.event || '未填写事件内容' }}</span>
          </div>
          <div class="event-move">
            <button :disabled="index === 0" @click.stop="moveEvent(index, -1)">↑</button>
            <button :disabled="index === events.length - 1" @click.stop="moveEvent(index, 1)">↓</button>
          </div>
        </article>
      </aside>

      <main v-if="selectedEvent" class="editor-panel">
        <div class="editor-head">
          <div>
            <p class="eyebrow">Event {{ selectedEvent.order }}</p>
            <h2>{{ selectedEvent.event || '新的事件节点' }}</h2>
          </div>
          <button class="danger" @click="removeEvent(selectedEvent.id)">删除事件</button>
        </div>

        <div class="form-grid">
          <label>
            <span>时间</span>
            <input v-model="selectedEvent.time" placeholder="如：第一卷第三天傍晚 / 主角入学当天" />
          </label>
          <label>
            <span>地点</span>
            <input v-model="selectedEvent.location" placeholder="如：红星幼儿园操场" />
          </label>
          <label>
            <span>人物</span>
            <input :value="joinList(selectedEvent.characters)" placeholder="用顿号或逗号分隔" @input="updateListField(selectedEvent, 'characters', $event.target.value)" />
          </label>
          <label>
            <span>关联章节</span>
            <input
              :value="joinList(selectedEvent.related_chapters)"
              placeholder="如：12、13、14"
              @input="selectedEvent.related_chapters = splitList($event.target.value).map(Number).filter(Boolean)"
            />
          </label>
        </div>

        <label class="wide">
          <span>事件</span>
          <textarea v-model="selectedEvent.event" placeholder="发生了什么？结果是什么？" />
        </label>

        <div class="form-grid two">
          <label>
            <span>前因</span>
            <textarea v-model="selectedEvent.cause" placeholder="这个事件为什么会发生？由哪个前置事件推动？" />
          </label>
          <label>
            <span>后果</span>
            <textarea v-model="selectedEvent.consequence" placeholder="这个事件造成什么变化？会如何影响后续？" />
          </label>
        </div>

        <label class="wide">
          <span>因果影响</span>
          <textarea v-model="selectedEvent.causal_effect" placeholder="对主线、人物关系、地图切换、实力成长的长期影响" />
        </label>

        <label class="wide">
          <span>伏笔</span>
          <input :value="joinList(selectedEvent.foreshadowing)" placeholder="用顿号或逗号分隔，例如：镜子异常、老师身份、隐藏任务" @input="updateListField(selectedEvent, 'foreshadowing', $event.target.value)" />
        </label>
      </main>

      <main v-else class="editor-panel empty-state">
        <h2>还没有事件节点</h2>
        <p>可以先点击“从总纲生成”，也可以手动新增事件。时间线一旦稳定，章节大纲就会有清晰的因果轨道。</p>
      </main>
    </section>

    <div v-if="message" class="toast">{{ message }}</div>
  </div>
</template>

<style scoped>
.timeline-page {
  height: auto;
  min-height: 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  padding: 2rem;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-gutter: stable;
  color: #17324d;
  background:
    radial-gradient(circle at 8% 8%, rgba(125, 211, 252, 0.32), transparent 28%),
    radial-gradient(circle at 92% 12%, rgba(191, 219, 254, 0.55), transparent 24%),
    linear-gradient(180deg, #eff8ff 0%, #f8fbff 48%, #ffffff 100%);
}

.timeline-hero,
.toolbar,
.timeline-shell,
.empty-card {
  max-width: 1280px;
  margin: 0 auto 1.1rem;
}

.timeline-hero {
  display: flex;
  justify-content: space-between;
  gap: 1.5rem;
  padding: 1.6rem 1.8rem;
  border: 1px solid rgba(96, 165, 250, 0.2);
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 24px 60px rgba(59, 130, 246, 0.12);
  backdrop-filter: blur(16px);
}

.eyebrow {
  margin: 0 0 .45rem;
  color: #3b82f6;
  font-size: .78rem;
  font-weight: 800;
  letter-spacing: .16em;
  text-transform: uppercase;
}

h1, h2 {
  margin: 0;
  color: #0f2f4d;
}

h1 {
  font-size: 2.15rem;
}

.hero-copy {
  max-width: 780px;
  margin: .8rem 0 0;
  color: #526f8c;
  line-height: 1.75;
}

.hero-card {
  min-width: 142px;
  display: grid;
  place-items: center;
  padding: 1rem;
  border-radius: 22px;
  color: #1d4ed8;
  background: linear-gradient(135deg, #dbeafe, #f0f9ff);
}

.hero-number {
  font-size: 2.25rem;
  font-weight: 900;
}

.toolbar {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 1rem;
  padding: 1rem;
  border-radius: 22px;
  border: 1px solid rgba(147, 197, 253, 0.28);
  background: rgba(255, 255, 255, 0.74);
}

.workflow-steps {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: .75rem;
}

.step-chip {
  display: flex;
  align-items: center;
  gap: .7rem;
  padding: .75rem .9rem;
  border: 1px solid rgba(96, 165, 250, .22);
  border-radius: 16px;
  background: rgba(248, 251, 255, .9);
  color: #526f8c;
  font-weight: 700;
}

.step-chip strong {
  display: grid;
  place-items: center;
  width: 28px;
  height: 28px;
  border-radius: 999px;
  background: #dbeafe;
  color: #1d4ed8;
}

.step-chip.active {
  color: #1d4ed8;
  background: #eff6ff;
}

.generate-box {
  flex: 1;
  display: flex;
  gap: .8rem;
  align-items: center;
}

input,
textarea {
  width: 100%;
  border: 1px solid rgba(96, 165, 250, 0.25);
  border-radius: 14px;
  padding: .75rem .85rem;
  color: #17324d;
  background: rgba(255, 255, 255, 0.92);
  outline: none;
}

textarea {
  min-height: 112px;
  resize: vertical;
  line-height: 1.7;
}

input:focus,
textarea:focus {
  border-color: #60a5fa;
  box-shadow: 0 0 0 4px rgba(96, 165, 250, 0.16);
}

.check {
  display: flex;
  align-items: center;
  gap: .45rem;
  white-space: nowrap;
  color: #526f8c;
  font-size: .9rem;
}

.check input {
  width: auto;
}

.toolbar-actions {
  display: flex;
  gap: .7rem;
  flex-wrap: wrap;
}

.btn,
.danger,
.event-move button {
  border: none;
  border-radius: 999px;
  font-weight: 800;
  cursor: pointer;
  transition: all .18s ease;
}

.btn {
  padding: .72rem 1.05rem;
}

.btn.solid {
  color: white;
  background: linear-gradient(135deg, #38bdf8, #2563eb);
  box-shadow: 0 14px 26px rgba(37, 99, 235, 0.2);
}

.btn.ghost {
  color: #2563eb;
  background: #ffffff;
  border: 1px solid rgba(96, 165, 250, 0.24);
}

button:disabled {
  opacity: .45;
  cursor: not-allowed;
}

.timeline-shell {
  display: grid;
  grid-template-columns: minmax(320px, 420px) 1fr;
  gap: 1rem;
  align-items: start;
  flex: 0 0 auto;
  min-height: 680px;
  overflow: visible;
}

.event-list,
.editor-panel,
.empty-card {
  border: 1px solid rgba(147, 197, 253, 0.24);
  border-radius: 26px;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: 0 20px 48px rgba(59, 130, 246, 0.1);
}

.event-list {
  padding: .9rem;
  max-height: 680px;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
}

.event-list::-webkit-scrollbar,
.editor-panel::-webkit-scrollbar {
  width: 12px;
}

.event-list::-webkit-scrollbar-thumb,
.editor-panel::-webkit-scrollbar-thumb {
  background: #bfdbfe;
  border: 2px solid transparent;
  border-radius: 999px;
  background-clip: padding-box;
}

.event-pill {
  display: grid;
  grid-template-columns: 42px 1fr auto;
  gap: .75rem;
  align-items: center;
  padding: .78rem;
  border-radius: 18px;
  cursor: pointer;
  border: 1px solid transparent;
}

.event-pill:hover,
.event-pill.active {
  background: #eff6ff;
  border-color: rgba(96, 165, 250, 0.28);
}

.event-order {
  display: grid;
  place-items: center;
  width: 36px;
  height: 36px;
  border-radius: 13px;
  color: #1d4ed8;
  background: #dbeafe;
  font-weight: 900;
}

.event-summary {
  min-width: 0;
  display: grid;
  gap: .25rem;
}

.event-summary strong {
  color: #17324d;
  font-size: .92rem;
}

.event-summary span {
  overflow: hidden;
  color: #64748b;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: .85rem;
}

.event-move {
  display: flex;
  gap: .25rem;
}

.event-move button {
  width: 28px;
  height: 28px;
  color: #2563eb;
  background: white;
  border: 1px solid rgba(96, 165, 250, 0.22);
}

.editor-panel {
  padding: 1.25rem;
  max-height: 680px;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
}

.editor-head {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}

.danger {
  align-self: start;
  padding: .55rem .82rem;
  color: #dc2626;
  background: #fff1f2;
  border: 1px solid #fecdd3;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: .9rem;
}

.form-grid.two {
  margin-top: .9rem;
}

label {
  display: grid;
  gap: .42rem;
  color: #41627f;
  font-weight: 700;
}

label span {
  font-size: .86rem;
}

.wide {
  margin-top: .9rem;
}

.empty-state,
.empty-card {
  padding: 2rem;
  color: #526f8c;
}

.toast {
  position: fixed;
  right: 1.5rem;
  bottom: 1.5rem;
  padding: .85rem 1.05rem;
  border-radius: 16px;
  color: white;
  background: rgba(15, 47, 77, 0.92);
  box-shadow: 0 18px 38px rgba(15, 47, 77, 0.22);
}

@media (max-width: 1080px) {
  .timeline-hero,
  .toolbar {
    flex-direction: column;
  }

  .timeline-shell {
    grid-template-columns: 1fr;
    max-height: none;
    overflow: visible;
  }

  .event-list {
    max-height: 360px;
  }
}
</style>
