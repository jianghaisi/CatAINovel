# Migration Roadmap

这份路线图用于把当前项目从“直接调用 SkillExecutor 写正文”迁移到“Skill + MCP + Writing Job Runner”的可恢复写作系统。

## Phase 0：冻结旧链路边界

目标：不再继续扩大旧 `SkillExecutor` 的职责。

动作：

- 保留现有 `ai/write-stream` 和 `chapters/write`，只修 bug，不加新能力。
- 新能力全部放入 `runtime`、`contracts`、`harness`、`mcp` 目录。
- 明确旧链路是兼容层，新链路是主线。

产物：

```text
backend/
  contracts/
  job_runner/
  mcp_servers/
  runtime/
```

## Phase 1：建立结构化 Contracts

目标：先把“数据契约”定下来，再接 AI。

新增模型：

- `TimelineEvent`
- `RealmLevel`
- `SystemUIContract`
- `ProtagonistState`
- `ChapterContext`
- `ChapterJob`
- `ValidationReport`

验收标准：

- 前端配置能保存到 `.webnovel/runtime/preferences.json` 或 SQLite。
- 章节上下文能被构造成一个稳定 JSON。
- 不调用 AI 也能运行境界和系统 UI 校验。

## Phase 2：实现本地 MCP Facade

目标：先用普通 Python 模块模拟 MCP 工具，降低迁移风险。

接口：

- `get_project_contracts()`
- `get_chapter_context(chapter)`
- `save_chapter_draft(chapter, content)`
- `save_validation_report(chapter, report)`
- `save_chapter_final(chapter, content)`
- `update_story_state(chapter, content)`

验收标准：

- Writing Job Runner 只通过这些接口读写项目数据。
- Writer 不直接读散落的 Markdown 文件。

## Phase 3：拆分 Skills

目标：将大而全的写作逻辑拆成可替换 Skill。

优先级：

1. `novel-writer`
2. `novel-realm-validator`
3. `novel-system-ui-validator`
4. `novel-resume`
5. `novel-repair`
6. `novel-continuity-checker`
7. `novel-outline-planner`
8. `novel-chapter-planner`

验收标准：

- 每个 Skill 的输入输出都是 JSON 契约。
- Validator 可单独运行。
- Repair 只接收报告和草稿，不重新构造上下文。

## Phase 4：实现 Writing Job Runner

目标：支持持续生成、断点恢复和失败重试。

核心类：

```text
WritingJobRunner
  create_write_range_job()
  run_next_chapter()
  run_chapter_stage()
  pause_job()
  resume_job()
  retry_failed_stage()
```

章节阶段：

1. `building_context`
2. `writing`
3. `draft_saved`
4. `validating_realm`
5. `validating_system_ui`
6. `validating_continuity`
7. `repairing`
8. `final_saved`
9. `state_updated`
10. `completed`

验收标准：

- 杀掉后端进程后重启，任务能从上一 checkpoint 继续。
- 单章失败不会影响已完成章节。
- 每章都有校验报告。

## Phase 5：前端控制台

目标：前端从编辑器升级为项目控制台。

页面：

- `RealmConfigView`：境界表。
- `SystemUIConfigView`：系统 UI 模版。
- `TimelineView`：事件时间线。
- `WritingJobView`：连续生成任务。
- `ValidationReportView`：校验失败定位和修复建议。

验收标准：

- 用户能启动“从第 X 章生成到第 Y 章”。
- 可暂停/恢复/重试。
- 校验失败能看到原因和对应正文片段。

## Phase 6：正式 MCP Server 化

目标：把本地 MCP Facade 升级为标准 MCP server。

收益：

- Codex、其他 Agent、命令行工具都能通过统一协议读写项目。
- 后续可增加外部工具：知识库、向量检索、数据库、排版发布器。

## 风险与规避

### 风险：过早重写全部旧代码

规避：旁路新链路，旧写作功能继续可用。

### 风险：AI 校验不稳定

规避：境界和系统 UI 用确定性校验，AI 只做复杂语义辅助。

### 风险：连续生成污染状态

规避：草稿先入 `.webnovel/runtime/drafts`，所有 blocker 通过后才写正文和更新状态。

### 风险：Prompt 上下文失控

规避：统一 `ChapterContext`，每个字段有预算和来源说明。

## 推荐下一步

先实现 Phase 1 和 Phase 2。也就是：

1. 新建 `backend/contracts` 数据契约。
2. 新建 `backend/runtime/project_store.py`。
3. 新建 `backend/job_runner/writing_job_runner.py` 的空状态机。
4. 新增 `POST /api/jobs/write-range`，先能创建任务和持久化，不急着真正调用 AI。
