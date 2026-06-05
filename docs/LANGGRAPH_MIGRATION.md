# LangGraph Migration

这个目录是从原小说 Agent 项目复制出的 LangGraph 改造版。改造原则不是重写前后端，而是把最复杂、最容易失控的“正文生成流程”先从 `SkillExecutor` 的长函数迁移到显式图。

## 当前状态

- 前端仍然使用 Vue 3 + Vite + Pinia。
- 后端仍然使用 FastAPI。
- AI 调用仍然复用 `services.ai_service.AIService`。
- RAG 仍然复用现有 `data_modules.rag_adapter.RAGAdapter`。
- 新增 LangGraph 工作流位于 `backend/graphs/chapter_write_graph.py`。
- 新增 API：`POST /api/graph/write-chapter`。

## 为什么先改正文生成

正文生成是最典型的状态机：

```text
load_context
  -> retrieve_rag
  -> validate_inputs
  -> draft_chapter
  -> review_chapter
  -> repair_chapter? -> review_chapter
  -> save_chapter
```

这比原来把所有步骤压在一个长方法里更适合 LangGraph：

- 每个节点职责单一，可单独测试。
- 条件分支显式，审查失败后进入修复循环。
- 状态集中在 `ChapterWriteState` 中，方便持久化和恢复。
- 前端以后可以直接展示图节点进度。

## 新增文件

- `backend/graphs/chapter_write_graph.py`：单章写作图。
- `backend/graphs/__init__.py`：图导出入口。
- `backend/routers/graph.py`：LangGraph API。

## API 示例

```http
POST /api/graph/write-chapter
Content-Type: application/json

{
  "chapter": 1,
  "word_count": 3500,
  "max_repair_attempts": 2
}
```

返回：

```json
{
  "success": true,
  "chapter": 1,
  "final_path": ".../正文/第1章 xxx.md",
  "validation_report_id": "report-...",
  "events": [
    {"stage": "load_context", "status": "completed"},
    {"stage": "retrieve_rag", "status": "completed"},
    {"stage": "draft_chapter", "status": "completed"}
  ],
  "errors": []
}
```

## 兼容策略

如果运行环境暂时没有安装 `langgraph`，`run_chapter_write_graph` 会使用顺序 fallback runner，保证代码仍然可运行；安装 `langgraph>=0.2.60` 后会自动使用真正的 `StateGraph`。

## 下一步迁移建议

1. 把 `execute_plan_stream` 拆成 `timeline_to_chapter_outline_graph`。
2. 把连续写多章交给 Writing Job Runner，Job Runner 每章调用 LangGraph。
3. 给 LangGraph 配 checkpoint saver，把每个节点状态落盘。
4. 前端把“AI 写作中”的 loading 改成图节点进度条。
