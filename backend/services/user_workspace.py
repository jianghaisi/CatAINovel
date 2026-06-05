"""Per-user workspace helpers."""

from __future__ import annotations

from pathlib import Path

GLOBAL_CONFIG_DIR = Path.home() / ".webnovel"


def get_users_root() -> Path:
    root = GLOBAL_CONFIG_DIR / "users"
    root.mkdir(parents=True, exist_ok=True)
    return root


def get_user_dir(user_id: str) -> Path:
    safe_id = "".join(ch for ch in user_id if ch.isalnum() or ch in {"-", "_"}) or "unknown"
    path = get_users_root() / safe_id
    (path / "projects").mkdir(parents=True, exist_ok=True)
    return path


def summarize_user_folder(user_id: str) -> dict:
    user_dir = get_user_dir(user_id)
    projects_dir = user_dir / "projects"
    projects = []
    if projects_dir.exists():
        for project in sorted(projects_dir.iterdir()):
            if project.is_dir():
                chapters_dir = project / "正文"
                chapters = list(chapters_dir.glob("*.md")) if chapters_dir.exists() else []
                projects.append({
                    "name": project.name,
                    "path": str(project),
                    "chapters": len(chapters),
                    "size_bytes": sum(file.stat().st_size for file in project.rglob("*") if file.is_file()),
                })
    return {
        "user_id": user_id,
        "data_dir": str(user_dir),
        "projects": projects,
    }
