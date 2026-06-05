# Writing Job Runner 是什么

这里不再使用 “Harness” 这个说法，避免让人误以为项目接入了某个官方 Harness 框架。

本项目里的准确命名是：

> **Writing Job Runner：写作任务调度器 / 章节级长任务执行器**

它是项目自定义的一层轻量任务控制逻辑，负责把“一次写一章”的能力扩展成“连续、可恢复地写很多章”。

## 它负责什么

Writing Job Runner 负责：

- 创建连续写作任务。
- 把任务拆成多个章节 Job。
- 记录当前写到第几章。
- 调用 LangGraph 执行单章写作流程。
- 成功后推进到下一章。
- 失败后记录错误，并按配置暂停或继续。
- 支持暂停、恢复、查询任务状态。

## 它不是什么

它不是：

- LangGraph 官方组件。
- OpenAI Evals Harness。
- 第三方任务队列框架。
- Celery / Redis Queue 这种生产级分布式队列。

它只是一个项目内自定义的轻量长任务调度器。

## 和 LangGraph 的区别

```text
Writing Job Runner
  负责：第几章该写、任务是否完成、失败后怎么办

LangGraph
  负责：单章内部怎么执行，例如上下文加载、RAG、写作、审查、修复、保存
```

一句话：

> LangGraph 管“单章内部流程”，Writing Job Runner 管“多章连续执行”。

## 代码位置

- `backend/job_runner/writing_job_runner.py`：新的主实现。
- `backend/routers/jobs.py`：任务 API。
- `backend/graphs/chapter_write_graph.py`：单章 LangGraph 写作流程。
- `backend/runtime/project_store.py`：任务状态文件存储。
- `backend/harness/`：旧命名兼容层，不建议面试时使用这个说法。

## 面试时可以这样说

> 项目里实现了一个自定义的 Writing Job Runner，也就是章节级长任务调度器。它不是官方 Harness 框架，而是我为了长篇小说连续生成设计的一层任务控制逻辑。LangGraph 负责单章内部的生成、审查和修复流程；Writing Job Runner 负责多章节任务的创建、状态持久化、失败记录、暂停恢复和继续执行。
