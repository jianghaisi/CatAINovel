from pathlib import Path
from typing import Optional
from fastapi import Header, Query, HTTPException
from urllib.parse import unquote

from services.auth_service import get_user_by_token
from services.user_workspace import get_user_dir


def _token_from_header(authorization: str | None) -> str:
    if not authorization:
        return ""
    return authorization.removeprefix("Bearer ").strip()


def _path_inside(child: Path, parent: Path) -> bool:
    child = child.resolve()
    parent = parent.resolve()
    return child == parent or parent in child.parents


def _authorized_project_path(raw_path: str, authorization: str | None) -> Path:
    user = get_user_by_token(_token_from_header(authorization))
    if not user:
        raise HTTPException(status_code=401, detail="????")
    path = Path(unquote(raw_path)).expanduser().resolve()
    user_projects_dir = get_user_dir(user.id) / "projects"
    if not path.exists():
        raise HTTPException(status_code=404, detail=f"????????{path}")
    if not _path_inside(path, user_projects_dir):
        raise HTTPException(status_code=403, detail="?????????????")
    return path


def get_project_root(
    project_root: Optional[str] = Query(None),
    x_project_root: Optional[str] = Header(None, alias="X-Project-Root"),
    authorization: Optional[str] = Header(None),
) -> Path:
    """Resolve current project path and enforce per-user project isolation."""
    if project_root:
        return _authorized_project_path(project_root, authorization)

    if x_project_root and x_project_root.lower() not in {"null", "undefined", "none"}:
        return _authorized_project_path(x_project_root, authorization)

    user = get_user_by_token(_token_from_header(authorization))
    if user:
        default_project = user.public_dict().get("default_project") or {}
        default_path = default_project.get("path")
        if default_path:
            return _authorized_project_path(default_path, authorization)

    raise HTTPException(status_code=400, detail="???????????????????????????")
