"""Long-form inspiration-to-novel pipeline API."""

import math
from pathlib import Path
from typing import Optional

from fastapi import APIRouter, Depends, Header, HTTPException
from pydantic import BaseModel, Field

from dependencies import get_project_root
from graphs import run_book_planning_graph
from job_runner import WritingJobRunner
from routers.ai import get_skill_executor
from services.genre_catalog import canonical_genre_id, canonical_substyle_id
from services.membership_service import require_active_membership


router = APIRouter()


class InspirationPipelineRequest(BaseModel):
    inspiration: str = Field(..., min_length=2)
    title: str = ""
    genre: str = "玄幻"
    substyle: str = ""
    protagonist_name: str = ""
    golden_finger_name: str = ""
    golden_finger_type: str = ""
    target_words: int = 2_000_000
    chapter_word_count: int = 3500
    chapters_per_volume: int = 30
    plan_volumes_now: int = 1
    max_planning_repair_attempts: int = Field(2, ge=0, le=3)
    auto_start_writing: bool = False


def _validate_word_count(value: int) -> int:
    if value < 2000 or value > 5000 or value % 500 != 0:
        raise HTTPException(status_code=400, detail="单章目标字数必须是 2000-5000 之间、每 500 递增的数值")
    return value


def _pipeline_guidance(request: InspirationPipelineRequest, total_chapters: int, total_volumes: int) -> str:
    return f"""用户灵感：
{request.inspiration}

长篇目标：
- 目标总字数：{request.target_words}
- 单章目标字数：{request.chapter_word_count}
- 预计总章节：{total_chapters}
- 预计总卷数：{total_volumes}

工作流要求：
1. 按“灵感 -> 全书总纲与完整卷纲 -> 章节大纲 -> 正文”的顺序推进。
2. 本次不要生成事件时间线；时间线只作为可选人工编辑材料，不作为自动生成正文的前置依赖。
3. Planner Agent 必须一次性给出全书主线、完整卷纲、阶段目标、主要矛盾、角色成长线、伏笔回收表。
4. 章节大纲必须严格服务总纲，每章写清目标、冲突、转折、收束钩子，不要跳过因果。
5. 正文写作必须遵守 AGENTS.md 中的境界系统、金手指 UI 模版、RAG 一致性要求。
"""


@router.post("/from-inspiration")
async def create_from_inspiration(
    request: InspirationPipelineRequest,
    root: Path = Depends(get_project_root),
    authorization: str | None = Header(None),
):
    """Create a durable pipeline from one inspiration paragraph.

    The endpoint initializes project context, runs a reviewed LangGraph planner
    for the total outline and all volume outlines, expands the first N volumes
    into chapter outlines, then creates a durable write-range job.
    Long 2M/3M-word writing continues through /api/jobs/{job_id}/run.
    """
    if request.target_words < 100_000:
        raise HTTPException(status_code=400, detail="长篇目标字数建议不少于 10 万字")
    _validate_word_count(request.chapter_word_count)
    if request.chapters_per_volume <= 0:
        raise HTTPException(status_code=400, detail="每卷章节数必须大于 0")

    total_chapters = max(1, math.ceil(request.target_words / request.chapter_word_count))
    total_volumes = max(1, math.ceil(total_chapters / request.chapters_per_volume))
    plan_volumes_now = max(1, min(request.plan_volumes_now, total_volumes, 3))
    require_active_membership(authorization, estimated_tokens=120_000)

    executor = get_skill_executor(root)
    title = request.title.strip() or "灵感新书"
    genre = canonical_genre_id(request.genre)
    substyle = canonical_substyle_id(genre, request.substyle)
    guidance = _pipeline_guidance(request, total_chapters, total_volumes)

    try:
        init_result = await executor.execute_init(
            title=title,
            genre=genre,
            substyle=substyle,
            protagonist_name=request.protagonist_name,
            golden_finger_name=request.golden_finger_name,
            golden_finger_type=request.golden_finger_type,
            additional_info=guidance,
            target_words=request.target_words,
            mode="deep",
            skip_legacy_outline=True,
        )

        def update_state(state):
            project_info = state.setdefault("project_info", {})
            project_info["title"] = title
            project_info["genre"] = genre
            project_info["substyle"] = substyle
            project_info["target_words"] = request.target_words
            project_info["chapter_word_count"] = request.chapter_word_count
            project_info["target_chapters"] = total_chapters
            project_info["total_volumes"] = total_volumes
            project_info["pipeline_mode"] = "inspiration_to_novel"
            project_info["skip_timeline"] = True
            state["chapter_word_count"] = request.chapter_word_count
            state["inspiration"] = request.inspiration

        executor._update_state(update_state)

        planning = await run_book_planning_graph(
            project_root=root,
            inspiration=request.inspiration,
            guidance=guidance,
            title=title,
            genre=genre,
            substyle=substyle,
            target_words=request.target_words,
            total_chapters=total_chapters,
            total_volumes=total_volumes,
            chapters_per_volume=request.chapters_per_volume,
            max_repair_attempts=request.max_planning_repair_attempts,
        )
        planned_volumes = []
        for volume in range(1, plan_volumes_now + 1):
            remaining = total_chapters - ((volume - 1) * request.chapters_per_volume)
            chapters_count = min(request.chapters_per_volume, remaining)
            planned = await executor.execute_plan(volume=volume, chapters_count=chapters_count)
            planned_volumes.append(
                {
                    "volume": volume,
                    "success": bool(planned.get("success")),
                    "path": planned.get("path", ""),
                }
            )

        job_runner = WritingJobRunner(root)
        job = job_runner.create_write_range_job(1, total_chapters)
        if request.auto_start_writing:
            job = await job_runner.run_next_chapter(job.id, word_count=request.chapter_word_count)

        return {
            "success": True,
            "initialized": bool(init_result.get("success")),
            "outline": {
                "success": True,
                "path": str(root / "大纲" / "总纲.md"),
                "review": planning.get("review", {}),
                "repair_attempts": planning.get("repair_attempts", 0),
                "report_path": planning.get("report_path", ""),
            },
            "volume_outlines": planning.get("volume_outline_paths", []),
            "planning_events": planning.get("events", []),
            "planning_errors": planning.get("errors", []),
            "planned_volumes": planned_volumes,
            "job": job,
            "total_chapters": total_chapters,
            "total_volumes": total_volumes,
            "chapter_word_count": request.chapter_word_count,
            "next_step": f"调用 /api/jobs/{job.id}/run 持续生成正文；中断后可继续调用同一 job。",
        }
    except HTTPException:
        raise
    except Exception as error:
        raise HTTPException(status_code=500, detail=str(error))
