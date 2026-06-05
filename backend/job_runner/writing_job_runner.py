"""Durable writing job runner.

This module owns long-running chapter generation state. It is not a third-party
Harness framework; it is the project's lightweight job orchestration layer for
multi-chapter writing.
"""

from datetime import datetime
from pathlib import Path
from typing import List
from uuid import uuid4

from contracts import ChapterJob, ChapterJobStatus, WriteRangeJob
from graphs import run_chapter_write_graph
from runtime import ProjectRuntimeStore


class WritingJobRunner:
    def __init__(self, project_root: Path):
        self.project_root = project_root
        self.store = ProjectRuntimeStore(project_root)

    def create_write_range_job(
        self,
        chapter_start: int,
        chapter_end: int,
        *,
        auto_repair: bool = True,
        stop_on_blocker: bool = True,
        max_repair_attempts: int = 2,
        max_resume_attempts: int = 3,
    ) -> WriteRangeJob:
        if chapter_start <= 0:
            raise ValueError("chapter_start must be greater than 0")
        if chapter_end < chapter_start:
            raise ValueError("chapter_end must be greater than or equal to chapter_start")

        job_id = f"write-range-{datetime.utcnow().strftime('%Y%m%d%H%M%S')}-{uuid4().hex[:8]}"
        chapter_jobs: List[ChapterJob] = [
            ChapterJob(
                id=f"{job_id}-chapter-{chapter}",
                chapter=chapter,
                status=ChapterJobStatus.pending,
                stage="pending",
            )
            for chapter in range(chapter_start, chapter_end + 1)
        ]
        job = WriteRangeJob(
            id=job_id,
            chapter_start=chapter_start,
            chapter_end=chapter_end,
            current_chapter=chapter_start,
            auto_repair=auto_repair,
            stop_on_blocker=stop_on_blocker,
            max_repair_attempts=max_repair_attempts,
            max_resume_attempts=max_resume_attempts,
            chapters=chapter_jobs,
        )
        self.store.save_write_range_job(job)
        return job

    def get_job(self, job_id: str) -> WriteRangeJob:
        job = self.store.get_write_range_job(job_id)
        if not job:
            raise ValueError(f"job not found: {job_id}")
        return job

    def list_jobs(self) -> List[WriteRangeJob]:
        return self.store.list_write_range_jobs()

    def pause_job(self, job_id: str) -> WriteRangeJob:
        job = self.get_job(job_id)
        job.status = ChapterJobStatus.paused
        job.updated_at = datetime.utcnow()
        self.store.save_write_range_job(job)
        return job

    def resume_job(self, job_id: str) -> WriteRangeJob:
        job = self.get_job(job_id)
        if job.status == ChapterJobStatus.completed:
            return job
        job.status = ChapterJobStatus.pending
        job.updated_at = datetime.utcnow()
        self.store.save_write_range_job(job)
        return job

    def _replace_chapter_job(self, job: WriteRangeJob, chapter_job: ChapterJob) -> WriteRangeJob:
        job.chapters = [
            chapter_job if item.id == chapter_job.id else item
            for item in job.chapters
        ]
        job.current_chapter = chapter_job.chapter
        job.updated_at = datetime.utcnow()
        self.store.save_write_range_job(job)
        return job

    def _next_pending_chapter_job(self, job: WriteRangeJob) -> ChapterJob | None:
        for chapter_job in sorted(job.chapters, key=lambda item: item.chapter):
            if chapter_job.status in {ChapterJobStatus.pending, ChapterJobStatus.failed}:
                return chapter_job
        return None

    async def run_next_chapter(self, job_id: str, *, word_count: int = 3500) -> WriteRangeJob:
        job = self.get_job(job_id)
        if job.status == ChapterJobStatus.paused:
            return job
        if job.status == ChapterJobStatus.completed:
            return job

        chapter_job = self._next_pending_chapter_job(job)
        if not chapter_job:
            job.status = ChapterJobStatus.completed
            job.updated_at = datetime.utcnow()
            self.store.save_write_range_job(job)
            return job

        job.status = ChapterJobStatus.writing
        chapter_job.status = ChapterJobStatus.building_context
        chapter_job.stage = "langgraph_start"
        chapter_job.attempts += 1
        chapter_job.error = ""
        chapter_job.updated_at = datetime.utcnow()
        self._replace_chapter_job(job, chapter_job)

        try:
            state = await run_chapter_write_graph(
                project_root=self.project_root,
                chapter=chapter_job.chapter,
                word_count=word_count,
                max_repair_attempts=job.max_repair_attempts if job.auto_repair else 0,
            )
            chapter_job.status = ChapterJobStatus.completed
            chapter_job.stage = "completed"
            chapter_job.final_path = str(state.get("final_path", ""))
            chapter_job.validation_report_id = str(state.get("validation_report_id", ""))
            chapter_job.error = "; ".join(state.get("errors", []))
            chapter_job.updated_at = datetime.utcnow()
            self._replace_chapter_job(job, chapter_job)
        except Exception as error:
            chapter_job.status = ChapterJobStatus.failed
            chapter_job.stage = "failed"
            chapter_job.error = str(error)
            chapter_job.updated_at = datetime.utcnow()
            self._replace_chapter_job(job, chapter_job)
            if job.stop_on_blocker:
                job.status = ChapterJobStatus.paused
                job.updated_at = datetime.utcnow()
                self.store.save_write_range_job(job)
                return job

        remaining = self._next_pending_chapter_job(job)
        job.status = ChapterJobStatus.pending if remaining else ChapterJobStatus.completed
        if remaining:
            job.current_chapter = remaining.chapter
        job.updated_at = datetime.utcnow()
        self.store.save_write_range_job(job)
        return job

    async def run_until_blocked_or_done(self, job_id: str, *, word_count: int = 3500) -> WriteRangeJob:
        job = self.resume_job(job_id)
        while job.status not in {ChapterJobStatus.completed, ChapterJobStatus.paused}:
            before_chapter = job.current_chapter
            job = await self.run_next_chapter(job.id, word_count=word_count)
            if job.status == ChapterJobStatus.paused:
                break
            if job.status == ChapterJobStatus.completed:
                break
            if job.current_chapter == before_chapter and self._next_pending_chapter_job(job) is None:
                job.status = ChapterJobStatus.completed
                job.updated_at = datetime.utcnow()
                self.store.save_write_range_job(job)
                break
        return job
