"""Structured contracts for the next-generation novel writing pipeline."""

from datetime import datetime
from enum import Enum
from typing import Any, Dict, List, Optional

from pydantic import BaseModel, Field


class TimelineEvent(BaseModel):
    id: str
    order: int = 0
    time: str
    characters: List[str] = Field(default_factory=list)
    location: str
    event: str
    cause: str = ""
    consequence: str = ""
    causal_effect: str = ""
    foreshadowing: List[str] = Field(default_factory=list)
    related_chapters: List[int] = Field(default_factory=list)
    source_outline_id: str = ""


class RealmLevel(BaseModel):
    id: str
    name: str
    order: int
    features: str = ""
    min_exp: Optional[int] = None
    max_exp: Optional[int] = None


class SystemUIContract(BaseModel):
    template: str = "[宿主：{name} | 等级：{level}]"
    variables: List[str] = Field(default_factory=lambda: ["name", "level"])


class ProtagonistState(BaseModel):
    name: str = ""
    realm: str = ""
    exp: Optional[int] = None
    power_summary: str = ""
    last_updated_chapter: int = 0


class ChapterOutline(BaseModel):
    chapter: int
    title: str = ""
    timeline_event_ids: List[str] = Field(default_factory=list)
    goal: str = ""
    conflict: str = ""
    turning_point: str = ""
    ending_hook: str = ""
    forbidden_next_events: List[str] = Field(default_factory=list)


class ChapterContext(BaseModel):
    chapter: int
    outline: ChapterOutline
    timeline_events: List[TimelineEvent] = Field(default_factory=list)
    previous_continuity: str = ""
    next_chapter_boundary: Dict[str, Any] = Field(default_factory=dict)
    active_characters: List[Dict[str, Any]] = Field(default_factory=list)
    protagonist_state: ProtagonistState = Field(default_factory=ProtagonistState)
    realm_levels: List[RealmLevel] = Field(default_factory=list)
    system_ui_contract: SystemUIContract = Field(default_factory=SystemUIContract)
    related_rag_scenes: List[Dict[str, Any]] = Field(default_factory=list)


class ChapterJobStatus(str, Enum):
    pending = "pending"
    building_context = "building_context"
    writing = "writing"
    draft_saved = "draft_saved"
    validating = "validating"
    repairing = "repairing"
    final_saved = "final_saved"
    state_updated = "state_updated"
    completed = "completed"
    failed = "failed"
    paused = "paused"


class ChapterJob(BaseModel):
    id: str
    chapter: int
    status: ChapterJobStatus = ChapterJobStatus.pending
    stage: str = "pending"
    attempts: int = 0
    draft_path: str = ""
    final_path: str = ""
    validation_report_id: str = ""
    error: str = ""
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: datetime = Field(default_factory=datetime.utcnow)


class WriteRangeJob(BaseModel):
    id: str
    chapter_start: int
    chapter_end: int
    status: ChapterJobStatus = ChapterJobStatus.pending
    current_chapter: int
    auto_repair: bool = True
    stop_on_blocker: bool = True
    max_repair_attempts: int = 2
    max_resume_attempts: int = 3
    chapters: List[ChapterJob] = Field(default_factory=list)
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: datetime = Field(default_factory=datetime.utcnow)


class ValidationSeverity(str, Enum):
    info = "info"
    warning = "warning"
    blocker = "blocker"


class ValidationCheck(BaseModel):
    type: str
    severity: ValidationSeverity
    message: str
    repairable: bool = False
    evidence: Dict[str, Any] = Field(default_factory=dict)


class ValidationReport(BaseModel):
    id: str
    chapter: int
    passed: bool
    checks: List[ValidationCheck] = Field(default_factory=list)
    created_at: datetime = Field(default_factory=datetime.utcnow)
