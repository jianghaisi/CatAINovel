"""LangGraph workflow API."""

from pathlib import Path

from fastapi import APIRouter, Depends, Header, HTTPException
from pydantic import BaseModel

from dependencies import get_project_root
from graphs import run_chapter_write_graph
from services.membership_service import require_active_membership

router = APIRouter()


class LangGraphWriteChapterRequest(BaseModel):
    chapter: int
    word_count: int = 3500
    max_repair_attempts: int = 2


@router.post("/write-chapter")
async def write_chapter_with_langgraph(
    request: LangGraphWriteChapterRequest,
    root: Path = Depends(get_project_root),
    authorization: str | None = Header(None),
):
    if request.chapter <= 0:
        raise HTTPException(status_code=400, detail="chapter must be greater than 0")
    if request.word_count < 500:
        raise HTTPException(status_code=400, detail="word_count is too small")
    require_active_membership(authorization, estimated_tokens=max(8_000, request.word_count * 3))
    try:
        state = await run_chapter_write_graph(
            project_root=root,
            chapter=request.chapter,
            word_count=request.word_count,
            max_repair_attempts=request.max_repair_attempts,
        )
        return {
            "success": True,
            "chapter": request.chapter,
            "final_path": state.get("final_path", ""),
            "validation_report_id": state.get("validation_report_id", ""),
            "events": state.get("events", []),
            "errors": state.get("errors", []),
        }
    except Exception as error:
        raise HTTPException(status_code=500, detail=str(error))
