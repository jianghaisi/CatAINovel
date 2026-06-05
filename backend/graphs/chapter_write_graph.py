"""LangGraph-based chapter writing workflow.

This module is the migration seam from the old monolithic SkillExecutor flow to
an explicit graph. It keeps existing services alive, but makes the orchestration
visible as nodes: context -> RAG -> validation -> draft -> review -> repair/save.
"""

from __future__ import annotations

import json
import re
from pathlib import Path
from typing import Any, Dict, List, Optional, TypedDict
from uuid import uuid4

from contracts import ValidationCheck, ValidationReport, ValidationSeverity
from runtime import ProjectRuntimeStore
from services.ai_service import get_ai_service
from services.skill_executor import SkillExecutor
from services.user_preferences import load_user_preferences


class ChapterWriteState(TypedDict, total=False):
    project_root: str
    chapter: int
    word_count: int
    max_repair_attempts: int
    stage: str
    context_pack: Dict[str, Any]
    core_constraints: str
    rag_context: List[Dict[str, Any]]
    user_preferences: Dict[str, Any]
    draft: str
    review: Dict[str, Any]
    repair_attempts: int
    validation_report_id: str
    final_path: str
    errors: List[str]
    events: List[Dict[str, Any]]


def _event(state: ChapterWriteState, stage: str, status: str, detail: str = "") -> None:
    state.setdefault("events", []).append({"stage": stage, "status": status, "detail": detail})
    state["stage"] = stage


def _executor(state: ChapterWriteState) -> SkillExecutor:
    return SkillExecutor(project_root=Path(state["project_root"]), ai_service=get_ai_service())


def _chapter_title(context_pack: Dict[str, Any], chapter: int) -> str:
    outline = (context_pack.get("core", {}) or {}).get("chapter_outline", "") if context_pack else ""
    match = re.search(r"第\s*0*%s\s*章[：:\-\s]*(.+)" % chapter, outline)
    if match:
        return match.group(1).strip()[:80]
    first_line = next((line.strip("# 　\t") for line in outline.splitlines() if line.strip()), "")
    return first_line[:80] or f"第{chapter}章"


def _chapter_path(project_root: Path, chapter: int, title: str) -> Path:
    safe_title = re.sub(r'[\\/:*?"<>|]+', "", title).strip() or f"第{chapter}章"
    chapters_dir = project_root / "正文"
    chapters_dir.mkdir(parents=True, exist_ok=True)
    return chapters_dir / f"第{chapter}章 {safe_title}.md"


async def load_context_node(state: ChapterWriteState) -> ChapterWriteState:
    _event(state, "load_context", "running", "读取章节大纲、设定、角色状态和项目偏好")
    executor = _executor(state)
    context_pack = await executor._execute_context_agent(int(state["chapter"]))
    state["context_pack"] = context_pack
    state["core_constraints"] = executor._load_reference("webnovel-write", "core-constraints.md")
    preferences = load_user_preferences(Path(state["project_root"]))
    state["user_preferences"] = preferences.dict()
    _event(state, "load_context", "completed")
    return state


async def retrieve_rag_node(state: ChapterWriteState) -> ChapterWriteState:
    _event(state, "retrieve_rag", "running", "使用章节大纲检索历史正文与设定片段")
    root = Path(state["project_root"])
    query = ((state.get("context_pack", {}).get("core", {}) or {}).get("chapter_outline", "") or "")[:500]
    if not query.strip():
        state["rag_context"] = []
        _event(state, "retrieve_rag", "skipped", "当前章节没有可检索的大纲")
        return state

    try:
        from data_modules.config import DataModulesConfig
        from data_modules.rag_adapter import RAGAdapter

        adapter = RAGAdapter(DataModulesConfig.from_project_root(root))
        results = await adapter.hybrid_search(query, rerank_top_n=6)
        state["rag_context"] = [
            {
                "chapter": item.chapter,
                "scene_index": item.scene_index,
                "content": item.content[:800],
                "score": round(item.score, 4),
                "source": item.source,
            }
            for item in results
        ]
    except Exception as error:
        state.setdefault("errors", []).append(f"RAG skipped: {error}")
        state["rag_context"] = []
    _event(state, "retrieve_rag", "completed", f"{len(state.get('rag_context', []))} chunks")
    return state


async def validate_inputs_node(state: ChapterWriteState) -> ChapterWriteState:
    _event(state, "validate_inputs", "running", "校验大纲、境界配置和系统 UI 模版是否可用")
    checks: List[str] = []
    context_pack = state.get("context_pack", {})
    outline = (context_pack.get("core", {}) or {}).get("chapter_outline", "")
    if not outline.strip():
        checks.append("缺少当前章节大纲，建议先从事件时间线生成章节大纲")

    preferences = state.get("user_preferences", {})
    if not preferences.get("realms"):
        checks.append("未配置境界列表，Writer 只能使用默认力量体系")
    if not preferences.get("system_ui_template"):
        checks.append("未配置金手指系统 UI 模版")

    if checks:
        state.setdefault("errors", []).extend(checks)
        _event(state, "validate_inputs", "warning", "；".join(checks))
    else:
        _event(state, "validate_inputs", "completed")
    return state


async def draft_chapter_node(state: ChapterWriteState) -> ChapterWriteState:
    _event(state, "draft_chapter", "running", "调用 Writer Agent 生成正文草稿")
    executor = _executor(state)
    context_pack = state.get("context_pack", {})
    if state.get("rag_context"):
        context_pack.setdefault("rag", {})["related_scenes"] = state["rag_context"]
    draft = await executor._generate_chapter_content(
        int(state["chapter"]),
        context_pack,
        state.get("core_constraints", ""),
        int(state.get("word_count", 3500)),
    )
    state["draft"] = draft.strip()
    _event(state, "draft_chapter", "completed", f"{len(state['draft'])} chars")
    return state


async def review_chapter_node(state: ChapterWriteState) -> ChapterWriteState:
    _event(state, "review_chapter", "running", "审查正文是否脱纲、截断或违反设定")
    ai_service = get_ai_service()
    context_pack = state.get("context_pack", {})
    outline = (context_pack.get("core", {}) or {}).get("chapter_outline", "")
    try:
        review = await ai_service.review_chapter(state.get("draft", ""), chapter_outline=outline)
    except Exception as error:
        review = {"passed": False, "raw_review": str(error), "issues": [str(error)]}
    state["review"] = review
    _event(state, "review_chapter", "completed")
    return state


def should_repair(state: ChapterWriteState) -> str:
    review_text = json.dumps(state.get("review", {}), ensure_ascii=False)
    attempts = int(state.get("repair_attempts", 0))
    max_attempts = int(state.get("max_repair_attempts", 2))
    blocker = bool(re.search(r"严重|高危|必须修改|需修改|截断|未完成|脱纲|不一致", review_text))
    if blocker and attempts < max_attempts:
        return "repair"
    return "save"


async def repair_chapter_node(state: ChapterWriteState) -> ChapterWriteState:
    attempts = int(state.get("repair_attempts", 0)) + 1
    state["repair_attempts"] = attempts
    _event(state, "repair_chapter", "running", f"第 {attempts} 次自动修复")

    ai_service = get_ai_service()
    review_text = json.dumps(state.get("review", {}), ensure_ascii=False)
    prompt = f"""请根据审查意见修复小说章节正文。

要求：
1. 保留原章节结构和主要剧情。
2. 修复脱纲、截断、设定不一致、系统 UI 模版不匹配等问题。
3. 只输出修复后的正文，不要解释。

【审查意见】
{review_text[:4000]}

【原正文】
{state.get("draft", "")}
"""
    fixed = await ai_service.chat([{"role": "user", "content": prompt}], temperature=0.45, max_tokens=9000)
    state["draft"] = fixed.strip()
    _event(state, "repair_chapter", "completed", f"{len(state['draft'])} chars")
    return state


async def save_chapter_node(state: ChapterWriteState) -> ChapterWriteState:
    _event(state, "save_chapter", "running", "保存最终正文与校验报告")
    root = Path(state["project_root"])
    chapter = int(state["chapter"])
    title = _chapter_title(state.get("context_pack", {}), chapter)
    path = _chapter_path(root, chapter, title)
    content = state.get("draft", "").strip()
    if not content.startswith("#"):
        content = f"# 第{chapter}章 {title}\n\n{content}"
    path.write_text(content, encoding="utf-8")

    review_text = json.dumps(state.get("review", {}), ensure_ascii=False)
    passed = not bool(re.search(r"严重|高危|必须修改|需修改|截断|未完成|脱纲|不一致", review_text))
    checks = [
        ValidationCheck(
            type="langgraph_review",
            severity=ValidationSeverity.info if passed else ValidationSeverity.warning,
            message="LangGraph 写作图完成审查" if passed else "LangGraph 写作图完成，但仍建议人工复核审查意见",
            repairable=not passed,
            evidence={"review": state.get("review", {})},
        )
    ]
    report = ValidationReport(id=f"report-{uuid4().hex[:10]}", chapter=chapter, passed=passed, checks=checks)
    ProjectRuntimeStore(root).save_validation_report(report)

    state["final_path"] = str(path)
    state["validation_report_id"] = report.id
    _event(state, "save_chapter", "completed", str(path))
    return state


def build_chapter_write_graph():
    try:
        from langgraph.graph import END, StateGraph
    except ImportError:
        return None

    graph = StateGraph(ChapterWriteState)
    graph.add_node("load_context", load_context_node)
    graph.add_node("retrieve_rag", retrieve_rag_node)
    graph.add_node("validate_inputs", validate_inputs_node)
    graph.add_node("draft_chapter", draft_chapter_node)
    graph.add_node("review_chapter", review_chapter_node)
    graph.add_node("repair_chapter", repair_chapter_node)
    graph.add_node("save_chapter", save_chapter_node)

    graph.set_entry_point("load_context")
    graph.add_edge("load_context", "retrieve_rag")
    graph.add_edge("retrieve_rag", "validate_inputs")
    graph.add_edge("validate_inputs", "draft_chapter")
    graph.add_edge("draft_chapter", "review_chapter")
    graph.add_conditional_edges("review_chapter", should_repair, {"repair": "repair_chapter", "save": "save_chapter"})
    graph.add_edge("repair_chapter", "review_chapter")
    graph.add_edge("save_chapter", END)
    return graph.compile()


async def _run_fallback(state: ChapterWriteState) -> ChapterWriteState:
    for node in [
        load_context_node,
        retrieve_rag_node,
        validate_inputs_node,
        draft_chapter_node,
        review_chapter_node,
    ]:
        state = await node(state)
    while should_repair(state) == "repair":
        state = await repair_chapter_node(state)
        state = await review_chapter_node(state)
    return await save_chapter_node(state)


async def run_chapter_write_graph(
    project_root: Path,
    chapter: int,
    word_count: int = 3500,
    max_repair_attempts: int = 2,
) -> ChapterWriteState:
    state: ChapterWriteState = {
        "project_root": str(project_root),
        "chapter": chapter,
        "word_count": word_count,
        "max_repair_attempts": max_repair_attempts,
        "repair_attempts": 0,
        "errors": [],
        "events": [],
    }
    graph = build_chapter_write_graph()
    if graph is None:
        state.setdefault("errors", []).append("langgraph is not installed; used sequential fallback runner")
        return await _run_fallback(state)
    return await graph.ainvoke(state)
