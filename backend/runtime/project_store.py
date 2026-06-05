"""Runtime project store used by the writing job runner.

This is intentionally small and file-backed. It acts as the local facade that can
later be replaced by a real MCP server without changing the job runner contract.
"""

import json
from pathlib import Path
from typing import Any, Dict, List, Optional

from contracts import ChapterJob, TimelineEvent, ValidationReport, WriteRangeJob


class ProjectRuntimeStore:
    def __init__(self, project_root: Path):
        self.project_root = project_root
        self.runtime_dir = project_root / ".webnovel" / "runtime"
        self.jobs_dir = self.runtime_dir / "jobs"
        self.drafts_dir = self.runtime_dir / "drafts"
        self.reports_dir = self.runtime_dir / "validation_reports"
        self.timeline_file = self.runtime_dir / "timeline.json"

    def ensure_dirs(self) -> None:
        self.jobs_dir.mkdir(parents=True, exist_ok=True)
        self.drafts_dir.mkdir(parents=True, exist_ok=True)
        self.reports_dir.mkdir(parents=True, exist_ok=True)

    def _read_json(self, path: Path, default: Any) -> Any:
        if not path.exists():
            return default
        try:
            return json.loads(path.read_text(encoding="utf-8"))
        except Exception:
            return default

    def _write_json(self, path: Path, data: Any) -> None:
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(json.dumps(data, ensure_ascii=False, indent=2, default=str), encoding="utf-8")

    def save_write_range_job(self, job: WriteRangeJob) -> None:
        self.ensure_dirs()
        self._write_json(self.jobs_dir / f"{job.id}.json", job.dict())

    def get_write_range_job(self, job_id: str) -> Optional[WriteRangeJob]:
        data = self._read_json(self.jobs_dir / f"{job_id}.json", None)
        return WriteRangeJob.parse_obj(data) if data else None

    def list_write_range_jobs(self) -> List[WriteRangeJob]:
        self.ensure_dirs()
        jobs: List[WriteRangeJob] = []
        for path in sorted(self.jobs_dir.glob("*.json")):
            data = self._read_json(path, None)
            if data:
                jobs.append(WriteRangeJob.parse_obj(data))
        return jobs

    def save_chapter_draft(self, chapter: int, content: str) -> Path:
        self.ensure_dirs()
        path = self.drafts_dir / f"chapter-{chapter}.md"
        path.write_text(content, encoding="utf-8")
        return path

    def read_chapter_draft(self, chapter: int) -> str:
        path = self.drafts_dir / f"chapter-{chapter}.md"
        return path.read_text(encoding="utf-8") if path.exists() else ""

    def save_validation_report(self, report: ValidationReport) -> None:
        self.ensure_dirs()
        self._write_json(self.reports_dir / f"{report.id}.json", report.dict())

    def get_validation_report(self, report_id: str) -> Optional[ValidationReport]:
        data = self._read_json(self.reports_dir / f"{report_id}.json", None)
        return ValidationReport.parse_obj(data) if data else None

    def save_timeline(self, events: List[TimelineEvent]) -> None:
        self.ensure_dirs()
        self._write_json(self.timeline_file, [event.dict() for event in events])

    def load_timeline(self) -> List[TimelineEvent]:
        data = self._read_json(self.timeline_file, [])
        return [TimelineEvent.parse_obj(item) for item in data if isinstance(item, dict)]

    def get_timeline_event(self, event_id: str) -> Optional[TimelineEvent]:
        for event in self.load_timeline():
            if event.id == event_id:
                return event
        return None

    def upsert_timeline_event(self, event: TimelineEvent) -> TimelineEvent:
        events = self.load_timeline()
        replaced = False
        for index, item in enumerate(events):
            if item.id == event.id:
                events[index] = event
                replaced = True
                break
        if not replaced:
            events.append(event)
        events.sort(key=lambda item: item.order or 0)
        self.save_timeline(events)
        return event

    def delete_timeline_event(self, event_id: str) -> bool:
        events = self.load_timeline()
        remaining = [event for event in events if event.id != event_id]
        if len(remaining) == len(events):
            return False
        self.save_timeline(remaining)
        return True

    def mark_chapter_job(self, job: WriteRangeJob, chapter_job: ChapterJob) -> WriteRangeJob:
        job.chapters = [
            chapter_job if item.id == chapter_job.id else item
            for item in job.chapters
        ]
        self.save_write_range_job(job)
        return job
