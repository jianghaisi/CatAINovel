<script setup>
import { computed, ref } from 'vue'
import { pipelineApi } from '../api'

const inspiration = ref('')
const title = ref('')
const genre = ref('玄幻')
const substyle = ref('')
const targetWords = ref(2000000)
const chapterWordCount = ref(3500)
const chaptersPerVolume = ref(30)
const planVolumesNow = ref(1)
const maxPlanningRepairAttempts = ref(2)
const autoStartWriting = ref(false)
const loading = ref(false)
const result = ref(null)
const error = ref('')

const wordOptions = Array.from({ length: 7 }, (_, index) => 2000 + index * 500)
const estimatedChapters = computed(() => Math.ceil(Number(targetWords.value || 0) / Number(chapterWordCount.value || 3500)))
const estimatedVolumes = computed(() => Math.ceil(estimatedChapters.value / Number(chaptersPerVolume.value || 30)))

async function startPipeline() {
  error.value = ''
  result.value = null
  if (!inspiration.value.trim()) {
    error.value = '请先输入一段灵感。'
    return
  }
  loading.value = true
  try {
    const { data } = await pipelineApi.createFromInspiration({
      inspiration: inspiration.value,
      title: title.value,
      genre: genre.value,
      substyle: substyle.value,
      target_words: Number(targetWords.value),
      chapter_word_count: Number(chapterWordCount.value),
      chapters_per_volume: Number(chaptersPerVolume.value),
      plan_volumes_now: Number(planVolumesNow.value),
      max_planning_repair_attempts: Number(maxPlanningRepairAttempts.value),
      auto_start_writing: autoStartWriting.value
    })
    result.value = data
  } catch (e) {
    error.value = e?.response?.data?.detail || e.message || '启动流水线失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="pipeline-page">
    <header class="hero">
      <p class="eyebrow">Inspiration Pipeline</p>
      <h1>灵感一键成书</h1>
      <p>只需输入剧情灵感。规划 Agent 会一次性完成全书总纲与完整卷纲，并自动审查、校正，再生成首批章节大纲。</p>
    </header>

    <div class="panel">
      <label class="field full">
        <span>灵感</span>
        <textarea v-model="inspiration" rows="8" placeholder="例如：废柴少年捡到一个只会吐槽的修仙系统，却发现系统其实来自未来的自己……" />
      </label>

      <div class="grid">
        <label class="field">
          <span>书名</span>
          <input v-model="title" placeholder="可留空，后端使用临时书名" />
        </label>
        <label class="field">
          <span>题材</span>
          <input v-model="genre" placeholder="玄幻 / 都市 / 规则怪谈" />
        </label>
        <label class="field">
          <span>子风格</span>
          <input v-model="substyle" placeholder="可选" />
        </label>
        <label class="field">
          <span>目标总字数</span>
          <select v-model.number="targetWords">
            <option :value="1000000">100 万字</option>
            <option :value="2000000">200 万字</option>
            <option :value="3000000">300 万字</option>
          </select>
        </label>
        <label class="field">
          <span>单章目标字数</span>
          <select v-model.number="chapterWordCount">
            <option v-for="item in wordOptions" :key="item" :value="item">{{ item }} 字</option>
          </select>
        </label>
        <label class="field">
          <span>每卷章节数</span>
          <input v-model.number="chaptersPerVolume" type="number" min="10" max="80" />
        </label>
        <label class="field">
          <span>首批展开几卷章节大纲</span>
          <select v-model.number="planVolumesNow">
            <option :value="1">1 卷</option>
            <option :value="2">2 卷</option>
            <option :value="3">3 卷</option>
          </select>
        </label>
        <label class="field">
          <span>规划自动校正次数</span>
          <select v-model.number="maxPlanningRepairAttempts">
            <option :value="1">最多 1 次</option>
            <option :value="2">最多 2 次</option>
            <option :value="3">最多 3 次</option>
          </select>
        </label>
      </div>

      <div class="summary">
        <span>预计 {{ estimatedChapters }} 章</span>
        <span>约 {{ estimatedVolumes }} 卷</span>
        <label><input v-model="autoStartWriting" type="checkbox" /> 创建后先写第 1 章</label>
      </div>

      <button class="primary" :disabled="loading" @click="startPipeline">
        {{ loading ? '规划 Agent 正在生成并校正…' : '启动规划 Agent 并创建长篇任务' }}
      </button>

      <p v-if="error" class="error">{{ error }}</p>
      <div v-if="result" class="result">
        <h2>任务已创建</h2>
        <p>总章节：{{ result.total_chapters }}，总卷数：{{ result.total_volumes }}</p>
        <p>完整卷纲：{{ result.volume_outlines?.length || 0 }} 卷，自动校正：{{ result.outline?.repair_attempts || 0 }} 次</p>
        <p>写作 Job：{{ result.job?.id }}</p>
        <p>{{ result.next_step }}</p>
      </div>
    </div>
  </section>
</template>

<style scoped>
.pipeline-page {
  height: 100%;
  min-height: 100%;
  padding: 2rem;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-gutter: stable;
  color: #16324f;
}
.hero {
  margin-bottom: 1.25rem;
}
.eyebrow {
  margin: 0 0 .35rem;
  color: #3b82f6;
  font-weight: 700;
  letter-spacing: .08em;
  text-transform: uppercase;
}
h1 {
  margin: 0;
  font-size: 2rem;
}
.hero p {
  color: #5b708a;
}
.panel {
  display: grid;
  gap: 1rem;
  padding: 1.25rem;
  border: 1px solid rgba(147, 197, 253, .45);
  border-radius: 24px;
  background: rgba(255, 255, 255, .86);
  box-shadow: 0 20px 60px rgba(59, 130, 246, .10);
}
.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 1rem;
}
.field {
  display: grid;
  gap: .45rem;
  font-weight: 650;
}
.full {
  grid-column: 1 / -1;
}
input, select, textarea {
  width: 100%;
  border: 1px solid #bfdbfe;
  border-radius: 14px;
  padding: .75rem .85rem;
  color: #18324f;
  background: #f8fbff;
  outline: none;
}
textarea {
  resize: vertical;
}
input:focus, select:focus, textarea:focus {
  border-color: #60a5fa;
  box-shadow: 0 0 0 4px rgba(96, 165, 250, .16);
}
.summary {
  display: flex;
  flex-wrap: wrap;
  gap: .75rem;
  align-items: center;
  color: #49657f;
}
.summary span, .summary label {
  padding: .45rem .7rem;
  border-radius: 999px;
  background: #eff6ff;
}
.primary {
  justify-self: start;
  border: 0;
  border-radius: 999px;
  padding: .85rem 1.25rem;
  color: white;
  background: linear-gradient(135deg, #60a5fa, #2563eb);
  cursor: pointer;
}
.primary:disabled {
  opacity: .65;
  cursor: wait;
}
.error {
  color: #dc2626;
}
.result {
  padding: 1rem;
  border-radius: 18px;
  background: #f0f9ff;
}
@media (max-width: 960px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
