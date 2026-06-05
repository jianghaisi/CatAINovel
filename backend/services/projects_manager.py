 
# projects_manager.py - 多项目管理服务
import json
import uuid
from pathlib import Path
from datetime import datetime
from typing import Dict, List, Optional, Any
from services.genre_catalog import canonical_genre_id, canonical_substyle_id
from services.project_prompt_store import ensure_project_prompts
from services.user_preferences import load_user_preferences, save_user_preferences

# 全局配置目录
GLOBAL_CONFIG_DIR = Path.home() / ".webnovel"
PROJECTS_FILE = GLOBAL_CONFIG_DIR / "projects.json"


def _projects_file_for_user(user_id: str | None = None) -> Path:
    if not user_id:
        return PROJECTS_FILE
    from services.user_workspace import get_user_dir
    return get_user_dir(user_id) / "projects.json"

def _ensure_config_dir():
    """确保全局配置目录存在"""
    GLOBAL_CONFIG_DIR.mkdir(parents=True, exist_ok=True)

def _load_projects_data(user_id: str | None = None) -> Dict[str, Any]:
    """加载项目列表数据"""
    _ensure_config_dir()
    data = {"current_project": None, "projects": []}
    
    projects_file = _projects_file_for_user(user_id)
    if projects_file.exists():
        try:
            data = json.loads(projects_file.read_text(encoding="utf-8"))
        except Exception:
            pass
    
    # Empty project lists stay empty. Users create or import projects manually.

    return data

def find_project_by_path(path: Path, user_id: str | None = None) -> Optional[Dict[str, Any]]:
    """根据路径回溯查找所属项目"""
    data = _load_projects_data(user_id)
    path = path.expanduser().resolve()
    
    # 尝试匹配路径或其父目录
    best_match = None
    max_len = -1
    
    for p in data.get("projects", []):
        p_path = Path(p["path"]).expanduser().resolve()
        try:
            if path == p_path or p_path in path.parents:
                # 选取最长匹配路径（最具体的子项目）
                if len(str(p_path)) > max_len:
                    max_len = len(str(p_path))
                    best_match = p
        except ValueError:
            continue
            
    return best_match

def _save_projects_data(data: Dict[str, Any], user_id: str | None = None):
    """保存项目列表数据"""
    _ensure_config_dir()
    projects_file = _projects_file_for_user(user_id)
    projects_file.parent.mkdir(parents=True, exist_ok=True)
    projects_file.write_text(json.dumps(data, ensure_ascii=False, indent=2), encoding="utf-8")

def list_projects(user_id: str | None = None) -> List[Dict[str, Any]]:
    """获取所有项目列表（并同步最新标题、章节数、字数）"""
    data = _load_projects_data(user_id)
    projects = data.get("projects", [])
    updated = False

    # 检查项目路径是否存在，并同步标题
    for p in projects:
        p_path = Path(p["path"])
        p["exists"] = p_path.exists()

        if p["exists"]:
            # 尝试读取最新的 state.json
            state_file = p_path / ".webnovel" / "state.json"
            if state_file.exists():
                try:
                    state = json.loads(state_file.read_text(encoding="utf-8"))
                    # 获取最新标题
                    new_title = ""
                    new_genre = ""
                    if "project_info" in state:
                        new_title = state["project_info"].get("title", "")
                        new_genre = state["project_info"].get("genre", "")
                    else:
                        new_title = state.get("title", "")
                        new_genre = state.get("genre", "")

                    # 如果标题不一致，更新缓存
                    if new_title and new_title != p["name"]:
                        p["name"] = new_title
                        updated = True
                    if new_genre and new_genre != p.get("genre"):
                        p["genre"] = new_genre
                        updated = True
                except Exception:
                    pass

            # 统计章节数和总字数
            chapters_dir = p_path / "正文"
            if chapters_dir.exists():
                try:
                    chapter_files = list(chapters_dir.glob("第*章*.md"))
                    p["total_chapters"] = len(chapter_files)
                    total_words = 0
                    for f in chapter_files:
                        try:
                            total_words += len(f.read_text(encoding="utf-8"))
                        except Exception:
                            pass
                    p["total_words"] = total_words
                except Exception:
                    p["total_chapters"] = 0
                    p["total_words"] = 0
            else:
                p["total_chapters"] = 0
                p["total_words"] = 0
        else:
            p["total_chapters"] = 0
            p["total_words"] = 0

    # 如果有更新，保存回 projects.json
    if updated:
        _save_projects_data(data, user_id)

    return projects

def get_current_project(user_id: str | None = None) -> Optional[Dict[str, Any]]:
    """获取当前项目"""
    data = _load_projects_data(user_id)
    current_path = data.get("current_project")
    if not current_path:
        return None
    for p in data.get("projects", []):
        if p["path"] == current_path:
            p["exists"] = Path(p["path"]).exists()
            return p
    return None

def set_current_project(path: Path, user_id: str | None = None):
    """设置当前项目路径"""
    data = _load_projects_data(user_id)
    abs_path = str(path.expanduser().resolve())
    
    # 确保保存为绝对路径
    data["current_project"] = abs_path
    
    # 更新最后打开时间
    for p in data["projects"]:
        if str(Path(p["path"]).expanduser().resolve()) == abs_path:
            p["last_opened"] = datetime.now().strftime("%Y-%m-%d")
            break
            
    _save_projects_data(data, user_id)


def get_current_project_path(user_id: str | None = None) -> Optional[Path]:
    """获取当前项目路径"""
    project = get_current_project(user_id)
    if project and project.get("exists"):
        return Path(project["path"])
    return None

def _resolve_user_project_path(path: str, user_id: str | None = None) -> Path:
    raw_path = Path(path or "").expanduser()
    if user_id and not raw_path.is_absolute():
        from services.user_workspace import get_user_dir
        raw_path = get_user_dir(user_id) / "projects" / raw_path.name
    return raw_path.resolve()


def _resolve_user_project_path(path: str, user_id: str | None = None) -> Path:
    raw_path = Path(path or "").expanduser()
    if user_id and not raw_path.is_absolute():
        from services.user_workspace import get_user_dir
        raw_path = get_user_dir(user_id) / "projects" / raw_path.name
    return raw_path.resolve()


def create_project(name: str, path: str, genre: str = "??", substyle: str = "", user_id: str | None = None) -> Dict[str, Any]:
    project_path = _resolve_user_project_path(path or name, user_id)
    genre = canonical_genre_id(genre) or "??"
    substyle = canonical_substyle_id(genre, substyle)
    data = _load_projects_data(user_id)
    for p in data["projects"]:
        if Path(p["path"]).expanduser().resolve() == project_path:
            return {"error": "???????", "project": p}
    project_path.mkdir(parents=True, exist_ok=True)
    for dirname in ["??", "??", "???", ".webnovel"]:
        (project_path / dirname).mkdir(exist_ok=True)
    state = {"title": name, "genre": genre, "substyle": substyle, "created_at": datetime.now().isoformat(), "initialized": False}
    (project_path / ".webnovel" / "state.json").write_text(json.dumps(state, ensure_ascii=False, indent=2), encoding="utf-8")
    ensure_project_prompts(project_path, genre, substyle)
    save_user_preferences(project_path, load_user_preferences(project_path))
    project = {"id": str(uuid.uuid4()), "name": name, "path": str(project_path), "genre": genre, "substyle": substyle, "created_at": datetime.now().strftime("%Y-%m-%d"), "last_opened": datetime.now().strftime("%Y-%m-%d")}
    data["projects"].append(project)
    data["current_project"] = project["path"]
    _save_projects_data(data, user_id)
    return {"success": True, "project": project}


def switch_project(project_id: str, user_id: str | None = None) -> Dict[str, Any]:
    data = _load_projects_data(user_id)
    requested_path = None
    try:
        requested_path = Path(project_id).expanduser().resolve() if project_id else None
    except Exception:
        requested_path = None
    for p in data["projects"]:
        project_path = Path(p["path"]).expanduser().resolve()
        if p["id"] == project_id or (requested_path and project_path == requested_path):
            if not project_path.exists():
                return {"error": "???????", "path": p["path"]}
            data["current_project"] = p["path"]
            p["last_opened"] = datetime.now().strftime("%Y-%m-%d")
            _save_projects_data(data, user_id)
            return {"success": True, "project": p}
    return {"error": "?????"}


def import_project(path: str, user_id: str | None = None) -> Dict[str, Any]:
    project_path = Path(path).expanduser().resolve()
    if not project_path.exists():
        return {"error": "?????"}
    state_file = project_path / ".webnovel" / "state.json"
    name = project_path.name
    genre = "??"
    substyle = ""
    if state_file.exists():
        try:
            state = json.loads(state_file.read_text(encoding="utf-8"))
            info = state.get("project_info") or {}
            name = info.get("title") or state.get("title") or name
            genre = info.get("genre") or state.get("genre") or genre
            substyle = info.get("substyle") or state.get("substyle") or ""
            ensure_project_prompts(project_path, genre, substyle)
        except Exception:
            pass
    data = _load_projects_data(user_id)
    for p in data["projects"]:
        if Path(p["path"]).expanduser().resolve() == project_path:
            data["current_project"] = p["path"]
            _save_projects_data(data, user_id)
            return {"success": True, "project": p, "already_exists": True}
    project = {"id": str(uuid.uuid4()), "name": name, "path": str(project_path), "genre": genre, "substyle": substyle, "created_at": datetime.now().strftime("%Y-%m-%d"), "last_opened": datetime.now().strftime("%Y-%m-%d")}
    data["projects"].append(project)
    data["current_project"] = project["path"]
    _save_projects_data(data, user_id)
    return {"success": True, "project": project}


def delete_project(project_id: str, delete_files: bool = False, user_id: str | None = None) -> Dict[str, Any]:
    data = _load_projects_data(user_id)
    for i, p in enumerate(data["projects"]):
        if p["id"] == project_id:
            removed = data["projects"].pop(i)
            if data.get("current_project") == removed["path"]:
                data["current_project"] = data["projects"][0]["path"] if data["projects"] else None
            _save_projects_data(data, user_id)
            if delete_files:
                import shutil
                try:
                    shutil.rmtree(removed["path"])
                except Exception as e:
                    return {"success": True, "warning": f"??????: {e}"}
            return {"success": True}
    return {"error": "?????"}
