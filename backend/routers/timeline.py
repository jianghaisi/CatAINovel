"""Editable event timeline API.

The timeline is generated in two phases:
1. Extract a clean basic timeline from the total outline: time, location,
   characters, event.
2. Optionally expand selected events with cause, consequence, causal impact and
   foreshadowing.
"""

import json
import re
from pathlib import Path
from typing import List, Optional
from uuid import uuid4

from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel

from contracts import TimelineEvent
from dependencies import get_project_root
from runtime import ProjectRuntimeStore

router = APIRouter()


class TimelineUpdate(BaseModel):
    events: List[TimelineEvent]


class GenerateTimelineRequest(BaseModel):
    overwrite: bool = False
    guidance: str = ""


class ExpandTimelineRequest(BaseModel):
    event_ids: Optional[List[str]] = None
    overwrite: bool = False
    guidance: str = ""


def _store(root: Path) -> ProjectRuntimeStore:
    return ProjectRuntimeStore(root)


def _read_total_outline(root: Path) -> str:
    outline_dirs = [root / "大纲", root / "澶х翰"]
    candidate_names = ["总纲.md", "鎬荤翰.md", "閹崵缈?md"]
    for outline_dir in outline_dirs:
        for filename in candidate_names:
            path = outline_dir / filename
            if path.exists():
                return path.read_text(encoding="utf-8")

    for outline_dir in outline_dirs:
        if not outline_dir.exists():
            continue
        for path in sorted(outline_dir.glob("*.md")):
            stem = path.stem
            if "总纲" in stem or "鎬荤翰" in stem:
                return path.read_text(encoding="utf-8")
        files = sorted(outline_dir.glob("*.md"))
        if files:
            return files[0].read_text(encoding="utf-8")
    return ""


def _extract_json_payload(raw: str):
    text = (raw or "").strip()
    if text.startswith("```"):
        text = re.sub(r"^```(?:json)?\s*", "", text, flags=re.IGNORECASE)
        text = re.sub(r"\s*```$", "", text)
    try:
        return json.loads(text)
    except Exception:
        pass

    object_start = text.find("{")
    object_end = text.rfind("}")
    if object_start >= 0 and object_end > object_start:
        try:
            return json.loads(text[object_start:object_end + 1])
        except Exception:
            pass

    array_start = text.find("[")
    array_end = text.rfind("]")
    if array_start >= 0 and array_end > array_start:
        try:
            return json.loads(text[array_start:array_end + 1])
        except Exception:
            pass
    return None


def _extract_event_items(raw: str) -> list:
    data = _extract_json_payload(raw)
    if isinstance(data, dict):
        data = data.get("events", [])
    return data if isinstance(data, list) else []


def _split_list(value) -> List[str]:
    if isinstance(value, list):
        return [str(item).strip() for item in value if str(item).strip()]
    return [part.strip() for part in re.split(r"[,，、|;；\n]+", str(value or "")) if part.strip()]


def _normalize_events(items: list, include_expansion: bool = True) -> List[TimelineEvent]:
    events: List[TimelineEvent] = []
    for index, item in enumerate(items):
        if not isinstance(item, dict):
            continue
        event_text = str(item.get("event", "")).strip()
        if not event_text:
            continue
        event_id = str(item.get("id") or f"event-{index + 1:04d}-{uuid4().hex[:6]}")
        events.append(
            TimelineEvent(
                id=event_id,
                order=int(item.get("order") or index + 1),
                time=str(item.get("time", "")).strip(),
                characters=_split_list(item.get("characters", [])),
                location=str(item.get("location", "")).strip(),
                event=event_text,
                cause=str(item.get("cause", "")).strip() if include_expansion else "",
                consequence=str(item.get("consequence", "")).strip() if include_expansion else "",
                causal_effect=str(item.get("causal_effect", "")).strip() if include_expansion else "",
                foreshadowing=_split_list(item.get("foreshadowing", [])) if include_expansion else [],
                related_chapters=[int(x) for x in item.get("related_chapters", []) if str(x).isdigit()],
                source_outline_id=str(item.get("source_outline_id", "total-outline")),
            )
        )
    events.sort(key=lambda item: item.order)
    return events


def _fallback_basic_events(outline: str) -> List[TimelineEvent]:
    lines = [line.strip(" #\t") for line in outline.splitlines() if line.strip()]
    candidates = []
    for line in lines:
        if len(line) < 8:
            continue
        if re.match(r"^(第[一二三四五六七八九十百千万\d]+[卷章幕节]|[一二三四五六七八九十\d]+[、.．])", line):
            candidates.append(line)
        elif any(key in line for key in ["发生", "遇到", "发现", "进入", "离开", "击败", "获得", "暴露", "决定"]):
            candidates.append(line)
    if not candidates:
        candidates = lines[:20]
    events = []
    for index, line in enumerate(candidates[:60]):
        events.append(
            TimelineEvent(
                id=f"event-{index + 1:04d}-{uuid4().hex[:6]}",
                order=index + 1,
                time=f"事件 {index + 1}",
                characters=[],
                location="",
                event=line[:500],
                source_outline_id="total-outline-fallback",
            )
        )
    return events


async def _chat_json(prompt: str, max_tokens: int = 6000) -> str:
    from services.ai_service import get_ai_service

    ai_service = get_ai_service()
    return await ai_service.chat(
        [{"role": "user", "content": prompt}],
        temperature=0.25,
        max_tokens=max_tokens,
        response_format="json",
    )


@router.get("")
async def get_timeline(root: Path = Depends(get_project_root)):
    events = _store(root).load_timeline()
    return {"events": events, "total": len(events)}


@router.put("")
async def update_timeline(payload: TimelineUpdate, root: Path = Depends(get_project_root)):
    events = sorted(payload.events, key=lambda item: item.order)
    _store(root).save_timeline(events)
    return {"success": True, "events": events, "total": len(events)}


@router.post("")
async def create_timeline_event(event: TimelineEvent, root: Path = Depends(get_project_root)):
    saved = _store(root).upsert_timeline_event(event)
    return {"success": True, "event": saved}


@router.delete("/{event_id}")
async def delete_timeline_event(event_id: str, root: Path = Depends(get_project_root)):
    deleted = _store(root).delete_timeline_event(event_id)
    if not deleted:
        raise HTTPException(status_code=404, detail="timeline event not found")
    return {"success": True}


@router.post("/generate-from-outline")
async def generate_timeline_from_outline(
    request: GenerateTimelineRequest,
    root: Path = Depends(get_project_root),
):
    store = _store(root)
    if store.load_timeline() and not request.overwrite:
        raise HTTPException(status_code=409, detail="时间线已存在；如需替换，请勾选覆盖现有时间线")

    outline = _read_total_outline(root)
    if not outline.strip():
        raise HTTPException(status_code=400, detail="未找到总纲文件，请先生成或保存总纲")

    prompt = f"""你是小说总纲编排 Agent。请只从【全局总纲】中提取“基础事件时间线”。

这一步只做结构提取，不要扩写，不要补脑，不要加入前因后果。

每个事件只输出以下字段：
- order: 顺序，从 1 开始
- time: 故事内时间或相对顺序；不确定时写“事件 N”
- characters: 参与人物数组；不确定时为空数组
- location: 地点；不确定时为空字符串
- event: 事件本身，一句话到三句话，必须能支撑后续章节大纲

要求：
1. 事件顺序必须和总纲逻辑一致。
2. 每个事件只覆盖一个清晰剧情节点，不要把多个大转折塞进一条。
3. 只输出 JSON 对象：{{"events":[...]}}。

额外要求：{request.guidance or "无"}

【全局总纲】
{outline[:18000]}
"""
    try:
        raw = await _chat_json(prompt, max_tokens=6000)
        events = _normalize_events(_extract_event_items(raw), include_expansion=False)
    except Exception:
        events = []

    if not events:
        events = _fallback_basic_events(outline)
    if not events:
        raise HTTPException(status_code=500, detail="无法从总纲提取基础时间线")

    store.save_timeline(events)
    return {"success": True, "events": events, "total": len(events), "mode": "basic"}


@router.post("/expand")
async def expand_timeline_events(
    request: ExpandTimelineRequest,
    root: Path = Depends(get_project_root),
):
    store = _store(root)
    events = store.load_timeline()
    if not events:
        raise HTTPException(status_code=400, detail="请先从总纲提取基础事件时间线")

    selected_ids = set(request.event_ids or [])
    selected = [event for event in events if not selected_ids or event.id in selected_ids]
    if not selected:
        raise HTTPException(status_code=404, detail="未找到要拓展的事件")

    payload = [
        {
            "id": event.id,
            "order": event.order,
            "time": event.time,
            "characters": event.characters,
            "location": event.location,
            "event": event.event,
            "cause": event.cause,
            "consequence": event.consequence,
            "causal_effect": event.causal_effect,
            "foreshadowing": event.foreshadowing,
        }
        for event in selected
    ]
    prompt = f"""你是小说剧情逻辑校准 Agent。请基于已有“基础事件时间线”，为指定事件补全前因后果。

不要修改 id、order、time、characters、location、event。
只补全或改进以下字段：
- cause: 事件前因，说明它由什么设定、人物动机或前序事件推动
- consequence: 直接后果，说明事件发生后立刻改变了什么
- causal_effect: 长期因果影响，说明它会如何影响主线、人物关系、地图切换、实力成长或章节大纲
- foreshadowing: 伏笔数组，没有则空数组

要求：
1. 因果必须自洽，不得引入与基础事件冲突的新事实。
2. 如果 overwrite=false，已有非空字段尽量保留，只做补强。
3. 只输出 JSON 对象：{{"events":[...]}}。

overwrite={str(request.overwrite).lower()}
额外要求：{request.guidance or "无"}

【待拓展事件】
{json.dumps(payload, ensure_ascii=False, indent=2)}
"""
    try:
        raw = await _chat_json(prompt, max_tokens=7000)
        expanded = _normalize_events(_extract_event_items(raw), include_expansion=True)
    except Exception as error:
        raise HTTPException(status_code=500, detail=f"拓展时间线失败：{error}")

    expanded_by_id = {event.id: event for event in expanded}
    merged: List[TimelineEvent] = []
    for event in events:
        patch = expanded_by_id.get(event.id)
        if not patch:
            merged.append(event)
            continue
        merged.append(
            event.copy(
                update={
                    "cause": patch.cause if request.overwrite or not event.cause else event.cause,
                    "consequence": patch.consequence if request.overwrite or not event.consequence else event.consequence,
                    "causal_effect": patch.causal_effect if request.overwrite or not event.causal_effect else event.causal_effect,
                    "foreshadowing": patch.foreshadowing if request.overwrite or not event.foreshadowing else event.foreshadowing,
                }
            )
        )

    merged.sort(key=lambda item: item.order)
    store.save_timeline(merged)
    return {"success": True, "events": merged, "total": len(merged), "mode": "expanded"}
