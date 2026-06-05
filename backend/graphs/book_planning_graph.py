"""LangGraph workflow for planning a long-form web novel from one human brief."""

from __future__ import annotations

import json
import re
from pathlib import Path
from typing import Any, Dict, List, TypedDict
from uuid import uuid4

from services.ai_service import get_ai_service
from services.skill_executor import SkillExecutor
from services.user_preferences import load_user_preferences


class BookPlanningState(TypedDict, total=False):
    project_root: str
    inspiration: str
    guidance: str
    title: str
    genre: str
    substyle: str
    target_words: int
    total_chapters: int
    total_volumes: int
    chapters_per_volume: int
    max_repair_attempts: int
    repair_attempts: int
    context: Dict[str, Any]
    rag_context: List[Dict[str, Any]]
    dossier: str
    review: Dict[str, Any]
    report_path: str
    volume_outline_paths: List[str]
    errors: List[str]
    events: List[Dict[str, str]]


def _event(state: BookPlanningState, stage: str, status: str, detail: str = "") -> None:
    state.setdefault("events", []).append({"stage": stage, "status": status, "detail": detail})


def _executor(state: BookPlanningState) -> SkillExecutor:
    return SkillExecutor(project_root=Path(state["project_root"]), ai_service=get_ai_service())


def _safe_json(text: str) -> Dict[str, Any]:
    cleaned = text.strip()
    fenced = re.search(r"```(?:json)?\s*(.*?)```", cleaned, re.DOTALL | re.IGNORECASE)
    if fenced:
        cleaned = fenced.group(1).strip()
    try:
        value = json.loads(cleaned)
        return value if isinstance(value, dict) else {"passed": False, "issues": [cleaned]}
    except json.JSONDecodeError:
        return {"passed": False, "issues": [cleaned or "审查模型未返回有效 JSON"]}


def _volume_sections(dossier: str) -> List[tuple[int, str]]:
    matches = list(re.finditer(r"(?m)^##\s*第\s*(\d+)\s*卷\b.*$", dossier))
    sections: List[tuple[int, str]] = []
    for index, match in enumerate(matches):
        end = matches[index + 1].start() if index + 1 < len(matches) else len(dossier)
        sections.append((int(match.group(1)), dossier[match.start():end].strip()))
    return sections


async def load_planning_context_node(state: BookPlanningState) -> BookPlanningState:
    _event(state, "load_planning_context", "running", "读取设定、用户偏好与规划参考")
    executor = _executor(state)
    root = Path(state["project_root"])
    preferences = load_user_preferences(root)
    state["context"] = {
        "world": executor._read_file(root / "设定集" / "世界观.md"),
        "power": executor._read_file(root / "设定集" / "力量体系.md"),
        "protagonist": executor._read_file(root / "设定集" / "主角卡.md"),
        "golden_finger": executor._read_file(root / "设定集" / "金手指设计.md"),
        "entities": executor._load_entity_libraries_summary(),
        "preferences": preferences.dict(),
    }
    _event(state, "load_planning_context", "completed")
    return state


async def retrieve_planning_rag_node(state: BookPlanningState) -> BookPlanningState:
    _event(state, "retrieve_planning_rag", "running", "检索可复用的历史设定与剧情约束")
    try:
        from data_modules.config import DataModulesConfig
        from data_modules.rag_adapter import RAGAdapter

        adapter = RAGAdapter(DataModulesConfig.from_project_root(Path(state["project_root"])))
        results = await adapter.hybrid_search(state["inspiration"][:800], rerank_top_n=8)
        state["rag_context"] = [
            {
                "chapter": item.chapter,
                "content": item.content[:700],
                "score": round(item.score, 4),
                "source": item.source,
            }
            for item in results
        ]
    except Exception as error:
        state.setdefault("errors", []).append(f"规划阶段 RAG 已降级：{error}")
        state["rag_context"] = []
    _event(state, "retrieve_planning_rag", "completed", f"{len(state.get('rag_context', []))} chunks")
    return state


def _planning_prompt(state: BookPlanningState) -> str:
    context = state.get("context", {})
    preferences = context.get("preferences", {})
    rag_text = "\n\n".join(item["content"] for item in state.get("rag_context", [])) or "（暂无可用历史片段）"
    return f"""你是长篇网络小说的首席规划 Agent。人类只提供创意素材，你负责一次性完成可执行的全书总纲和完整卷纲。

【人类输入的剧情灵感】
{state["inspiration"]}

【补充约束】
{state.get("guidance", "")}

【项目目标】
- 书名：《{state["title"]}》
- 题材：{state["genre"]} / {state.get("substyle", "")}
- 目标字数：{state["target_words"]}
- 预计章节：{state["total_chapters"]}
- 完整卷数：{state["total_volumes"]}
- 每卷约 {state["chapters_per_volume"]} 章

【已有设定】
世界观：{context.get("world", "")[:2400]}
力量体系：{context.get("power", "")[:1800]}
主角卡：{context.get("protagonist", "")[:1800]}
金手指：{context.get("golden_finger", "")[:1800]}
实体库：{context.get("entities", "")[:1800]}
用户境界配置：{json.dumps(preferences.get("realms", []), ensure_ascii=False)[:1400]}
系统 UI 模版：{preferences.get("system_ui_template", "")[:800]}

【RAG 检索到的历史约束】
{rag_text[:5000]}

【规划规则】
1. 不要向人类追问细节；在不违背灵感的前提下主动补齐世界观、人物动机、矛盾和结局。
2. 输出完整全书结构，不要只规划开头。每一卷都必须服务主线，并自然承接上一卷。
3. 人物目标、性格、关系和能力变化必须有因果；境界升级必须有铺垫、收益、代价或限制。
4. 符合网络小说节奏：开篇钩子、阶段爽点、危机升级、卷末高潮、地图或矛盾升级、伏笔回收。
5. 为后续章纲生成留下明确锚点，但此处不要展开逐章大纲。
6. 必须严格输出 Markdown。卷纲必须恰好包含第1卷至第{state["total_volumes"]}卷，不得缺卷或跳号。

【输出结构】
# 《{state["title"]}》全书规划
## 一、核心卖点
## 二、世界观与规则
## 三、主要人物弧光
## 四、全书主线与结局方向
## 五、伏笔回收总表
## 六、完整分卷规划
## 第1卷 《卷名》（约1-{state["chapters_per_volume"]}章）
- 核心目标：
- 起因与承接：
- 主要冲突：
- 人物变化：
- 境界与能力变化：
- 阶段爽点：
- 关键伏笔：
- 卷末高潮：
- 对下一卷的影响：

继续按照同一结构输出直到第{state["total_volumes"]}卷。"""


async def plan_book_node(state: BookPlanningState) -> BookPlanningState:
    _event(state, "plan_book", "running", "Planner Agent 一次性生成总纲与完整卷纲")
    max_tokens = max(12000, min(50000, 4500 + state["total_volumes"] * 900))
    state["dossier"] = (
        await get_ai_service().chat(
            [{"role": "user", "content": _planning_prompt(state)}],
            temperature=0.72,
            max_tokens=max_tokens,
        )
    ).strip()
    _event(state, "plan_book", "completed", f"{len(state['dossier'])} chars")
    return state


async def review_book_node(state: BookPlanningState) -> BookPlanningState:
    _event(state, "review_book", "running", "Reviewer Agent 检查逻辑、人物与网文节奏")
    dossier = state.get("dossier", "")
    prompt = f"""你是长篇网络小说的规划审查 Agent。请严格审查总纲和卷纲。

检查：
1. 是否包含第1卷至第{state["total_volumes"]}卷，且卷数完整、顺序连续。
2. 全书主线、因果链、伏笔埋设与回收是否闭合。
3. 人物目标、性格、关系和能力变化是否一致，是否有无解释跳变。
4. 是否具有网络小说需要的开篇钩子、阶段爽点、矛盾升级和卷末高潮。
5. 是否存在剧情重复、后期失控、战力膨胀或结局与前文脱节。

仅输出 JSON：
{{"passed": true或false, "score": 0到100, "issues": ["具体问题"], "repair_instructions": ["可执行修复要求"]}}

【待审查规划】
{dossier[:60000]}"""
    state["review"] = _safe_json(
        await get_ai_service().chat(
            [{"role": "user", "content": prompt}],
            temperature=0.15,
            max_tokens=5000,
            response_format="json_object",
        )
    )
    sections = _volume_sections(dossier)
    volume_numbers = [volume for volume, _ in sections]
    expected_numbers = list(range(1, state["total_volumes"] + 1))
    if volume_numbers != expected_numbers:
        state["review"]["passed"] = False
        state["review"].setdefault("issues", []).append(
            f"卷纲编号不完整：期望 {expected_numbers}，实际 {volume_numbers}"
        )
    _event(state, "review_book", "completed", f"score={state['review'].get('score', 'unknown')}")
    return state


def should_repair_book(state: BookPlanningState) -> str:
    passed = state.get("review", {}).get("passed") is True
    attempts = int(state.get("repair_attempts", 0))
    return "save" if passed or attempts >= int(state.get("max_repair_attempts", 2)) else "repair"


async def repair_book_node(state: BookPlanningState) -> BookPlanningState:
    attempts = int(state.get("repair_attempts", 0)) + 1
    state["repair_attempts"] = attempts
    _event(state, "repair_book", "running", f"Repair Agent 第 {attempts} 次校正")
    prompt = f"""你是长篇网络小说的规划修复 Agent。请根据审查意见完整重写规划，不要解释。

必须保留人类灵感的核心卖点，并修复逻辑断裂、人物跑偏、能力跳变、重复剧情、卷纲缺失和伏笔未回收。
必须输出第1卷至第{state["total_volumes"]}卷，卷纲编号完整连续。

【审查意见】
{json.dumps(state.get("review", {}), ensure_ascii=False)[:8000]}

【原规划】
{state.get("dossier", "")[:60000]}"""
    state["dossier"] = (
        await get_ai_service().chat(
            [{"role": "user", "content": prompt}],
            temperature=0.45,
            max_tokens=max(12000, min(50000, 4500 + state["total_volumes"] * 900)),
        )
    ).strip()
    _event(state, "repair_book", "completed", f"{len(state['dossier'])} chars")
    return state


async def save_book_plan_node(state: BookPlanningState) -> BookPlanningState:
    _event(state, "save_book_plan", "running", "保存经审查的总纲、卷纲与审查报告")
    root = Path(state["project_root"])
    outline_dir = root / "大纲"
    volume_dir = outline_dir / "全书卷纲"
    runtime_dir = root / ".webnovel" / "runtime" / "planning_reports"
    outline_dir.mkdir(parents=True, exist_ok=True)
    volume_dir.mkdir(parents=True, exist_ok=True)
    runtime_dir.mkdir(parents=True, exist_ok=True)

    dossier = state.get("dossier", "").strip()
    (outline_dir / "总纲.md").write_text(dossier, encoding="utf-8")
    paths: List[str] = []
    for volume, section in _volume_sections(dossier):
        path = volume_dir / f"第{volume}卷-卷纲.md"
        path.write_text(section + "\n", encoding="utf-8")
        paths.append(str(path))

    report_path = runtime_dir / f"plan-{uuid4().hex[:10]}.json"
    report_path.write_text(
        json.dumps(
            {
                "passed": state.get("review", {}).get("passed") is True,
                "review": state.get("review", {}),
                "repair_attempts": state.get("repair_attempts", 0),
                "total_volumes": state["total_volumes"],
                "saved_volume_outlines": len(paths),
            },
            ensure_ascii=False,
            indent=2,
        ),
        encoding="utf-8",
    )
    state["volume_outline_paths"] = paths
    state["report_path"] = str(report_path)
    _executor(state)._clear_outline_invalidation_state()
    _event(state, "save_book_plan", "completed", f"{len(paths)} volume outlines")
    return state


def build_book_planning_graph():
    try:
        from langgraph.graph import END, StateGraph
    except ImportError:
        return None

    graph = StateGraph(BookPlanningState)
    graph.add_node("load_planning_context", load_planning_context_node)
    graph.add_node("retrieve_planning_rag", retrieve_planning_rag_node)
    graph.add_node("plan_book", plan_book_node)
    graph.add_node("review_book", review_book_node)
    graph.add_node("repair_book", repair_book_node)
    graph.add_node("save_book_plan", save_book_plan_node)
    graph.set_entry_point("load_planning_context")
    graph.add_edge("load_planning_context", "retrieve_planning_rag")
    graph.add_edge("retrieve_planning_rag", "plan_book")
    graph.add_edge("plan_book", "review_book")
    graph.add_conditional_edges("review_book", should_repair_book, {"repair": "repair_book", "save": "save_book_plan"})
    graph.add_edge("repair_book", "review_book")
    graph.add_edge("save_book_plan", END)
    return graph.compile()


async def _run_fallback(state: BookPlanningState) -> BookPlanningState:
    for node in [load_planning_context_node, retrieve_planning_rag_node, plan_book_node, review_book_node]:
        state = await node(state)
    while should_repair_book(state) == "repair":
        state = await repair_book_node(state)
        state = await review_book_node(state)
    return await save_book_plan_node(state)


async def run_book_planning_graph(
    project_root: Path,
    inspiration: str,
    guidance: str,
    title: str,
    genre: str,
    substyle: str,
    target_words: int,
    total_chapters: int,
    total_volumes: int,
    chapters_per_volume: int,
    max_repair_attempts: int = 2,
) -> BookPlanningState:
    state: BookPlanningState = {
        "project_root": str(project_root),
        "inspiration": inspiration,
        "guidance": guidance,
        "title": title,
        "genre": genre,
        "substyle": substyle,
        "target_words": target_words,
        "total_chapters": total_chapters,
        "total_volumes": total_volumes,
        "chapters_per_volume": chapters_per_volume,
        "max_repair_attempts": max_repair_attempts,
        "repair_attempts": 0,
        "errors": [],
        "events": [],
    }
    graph = build_book_planning_graph()
    if graph is None:
        state["errors"].append("langgraph is not installed; used sequential planning fallback")
        return await _run_fallback(state)
    return await graph.ainvoke(state)
