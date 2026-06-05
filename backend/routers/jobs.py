"""Durable writing job API."""

from pathlib import Path
from typing import Optional

from fastapi import APIRouter, Depends, Header, HTTPException
from pydantic import BaseModel

from dependencies import get_project_root
from job_runner import WritingJobRunner
from services.membership_service import require_active_membership

router = APIRouter()


class WriteRangeRequest(BaseModel):
    chapter_start: int
    chapter_end: int
    auto_repair: bool = True
    stop_on_blocker: bool = True
    max_repair_attempts: int = 2
    max_resume_attempts: int = 3


class RunJobRequest(BaseModel):
    word_count: int = 3500


def get_job_runner(root: Path) -> WritingJobRunner:
    return WritingJobRunner(root)


@router.get("")
async def list_jobs(root: Path = Depends(get_project_root)):
    return {"jobs": get_job_runner(root).list_jobs()}


@router.post("/write-range")
async def create_write_range_job(
    request: WriteRangeRequest,
    root: Path = Depends(get_project_root),
    authorization: str | None = Header(None),
):
    require_active_membership(authorization)
    try:
        job = get_job_runner(root).create_write_range_job(
            chapter_start=request.chapter_start,
            chapter_end=request.chapter_end,
            auto_repair=request.auto_repair,
            stop_on_blocker=request.stop_on_blocker,
            max_repair_attempts=request.max_repair_attempts,
            max_resume_attempts=request.max_resume_attempts,
        )
        return {"success": True, "job": job}
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))


@router.get("/{job_id}")
async def get_job(job_id: str, root: Path = Depends(get_project_root)):
    try:
        return {"job": get_job_runner(root).get_job(job_id)}
    except ValueError as error:
        raise HTTPException(status_code=404, detail=str(error))


@router.post("/{job_id}/pause")
async def pause_job(job_id: str, root: Path = Depends(get_project_root)):
    try:
        return {"success": True, "job": get_job_runner(root).pause_job(job_id)}
    except ValueError as error:
        raise HTTPException(status_code=404, detail=str(error))


@router.post("/{job_id}/resume")
async def resume_job(job_id: str, root: Path = Depends(get_project_root)):
    try:
        return {"success": True, "job": get_job_runner(root).resume_job(job_id)}
    except ValueError as error:
        raise HTTPException(status_code=404, detail=str(error))


@router.post("/{job_id}/run-next")
async def run_next_chapter(
    job_id: str,
    request: RunJobRequest,
    root: Path = Depends(get_project_root),
    authorization: str | None = Header(None),
):
    require_active_membership(authorization, estimated_tokens=max(8_000, request.word_count * 3))
    try:
        job = await get_job_runner(root).run_next_chapter(job_id, word_count=request.word_count)
        return {"success": True, "job": job}
    except ValueError as error:
        raise HTTPException(status_code=404, detail=str(error))
    except Exception as error:
        raise HTTPException(status_code=500, detail=str(error))


@router.post("/{job_id}/run")
async def run_job_until_done(
    job_id: str,
    request: RunJobRequest,
    root: Path = Depends(get_project_root),
    authorization: str | None = Header(None),
):
    require_active_membership(authorization, estimated_tokens=100_000)
    try:
        job = await get_job_runner(root).run_until_blocked_or_done(job_id, word_count=request.word_count)
        return {"success": True, "job": job}
    except ValueError as error:
        raise HTTPException(status_code=404, detail=str(error))
    except Exception as error:
        raise HTTPException(status_code=500, detail=str(error))
