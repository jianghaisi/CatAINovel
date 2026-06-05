# 已改名：Writing Job Runner

这个文件只保留旧链接兼容。

新的准确说法是：

```text
Writing Job Runner
```

中文可以叫：

```text
写作任务调度器 / 章节级长任务执行器
```

请优先阅读：

```text
docs/JOB_RUNNER_EXPLAINED.md
```

面试时不要说“我用了 Harness 框架”，应该说：

> 我实现了一个自定义的 Writing Job Runner，用来管理多章节长任务。LangGraph 负责单章内部流程，Writing Job Runner 负责多章调度、状态持久化、失败暂停和恢复。
