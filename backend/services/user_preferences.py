import json
from pathlib import Path
from typing import Any, Dict, List, Optional

from pydantic import BaseModel, ConfigDict, Field


DEFAULT_SYSTEM_UI_TEMPLATE = "[宿主：{name} | 等级：{level}]"


class Timeline(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    id: Optional[str] = None
    title: str = ""
    time: str = Field("", alias="Time")
    characters: List[str] = Field(default_factory=list, alias="Characters")
    location: str = Field("", alias="Location")
    event: str = Field("", alias="Event")


class RealmLevel(BaseModel):
    id: Optional[str] = None
    name: str
    features: str = ""
    order: int = 0


class UserPreference(BaseModel):
    realms: List[RealmLevel] = Field(default_factory=list)
    system_ui_template: str = DEFAULT_SYSTEM_UI_TEMPLATE


def _preferences_file(project_root: Path) -> Path:
    return project_root / ".webnovel" / "user_preferences.json"


def _levels_file(project_root: Path) -> Path:
    return project_root / "config" / "levels.json"


def _default_preferences() -> UserPreference:
    return UserPreference(
        realms=[
            RealmLevel(id="qi-refining", name="炼气期", features="引气入体，灵力初成", order=1),
            RealmLevel(id="foundation", name="筑基期", features="筑成道基，灵力稳定", order=2),
        ],
        system_ui_template=DEFAULT_SYSTEM_UI_TEMPLATE,
    )


def _normalize_realms(realms: List[Dict[str, Any]]) -> List[RealmLevel]:
    normalized: List[RealmLevel] = []
    for index, item in enumerate(realms or []):
        if not isinstance(item, dict):
            continue
        name = str(item.get("name", "")).strip()
        if not name:
            continue
        normalized.append(
            RealmLevel(
                id=str(item.get("id") or f"realm-{index + 1}"),
                name=name,
                features=str(item.get("features", "")).strip(),
                order=int(item.get("order") or index + 1),
            )
        )
    normalized.sort(key=lambda item: item.order)
    return normalized


def load_user_preferences(project_root: Path) -> UserPreference:
    pref_file = _preferences_file(project_root)
    if pref_file.exists():
        try:
            data = json.loads(pref_file.read_text(encoding="utf-8"))
            return UserPreference(
                realms=_normalize_realms(data.get("realms", [])),
                system_ui_template=str(data.get("system_ui_template") or DEFAULT_SYSTEM_UI_TEMPLATE),
            )
        except Exception:
            pass

    levels_file = _levels_file(project_root)
    if levels_file.exists():
        try:
            data = json.loads(levels_file.read_text(encoding="utf-8"))
            raw_levels = data.get("levels", data if isinstance(data, list) else [])
            return UserPreference(
                realms=_normalize_realms(raw_levels),
                system_ui_template=DEFAULT_SYSTEM_UI_TEMPLATE,
            )
        except Exception:
            pass

    return _default_preferences()


def save_user_preferences(project_root: Path, preference: UserPreference) -> UserPreference:
    normalized = UserPreference(
        realms=_normalize_realms([item.model_dump() for item in preference.realms]),
        system_ui_template=(preference.system_ui_template or DEFAULT_SYSTEM_UI_TEMPLATE).strip(),
    )
    pref_file = _preferences_file(project_root)
    pref_file.parent.mkdir(parents=True, exist_ok=True)
    pref_file.write_text(normalized.model_dump_json(indent=2), encoding="utf-8")

    levels_file = _levels_file(project_root)
    levels_file.parent.mkdir(parents=True, exist_ok=True)
    levels_file.write_text(
        json.dumps({"levels": [item.model_dump() for item in normalized.realms]}, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    return normalized


def build_preference_prompt_context(project_root: Path) -> str:
    preference = load_user_preferences(project_root)
    realm_lines = [
        f"{level.order}. {level.name}：{level.features or '（未填写特征）'}"
        for level in preference.realms
    ]
    realms_text = "\n".join(realm_lines) if realm_lines else "（未配置境界；不得临时硬编新境界）"
    return f"""【用户自定义境界表（最高优先级）】
{realms_text}

【用户自定义金手指系统 UI 模版（最高优先级）】
{preference.system_ui_template}

硬性规则：
1. 正文出现主角境界、突破、经验值或实力判断时，必须使用上方境界表，不得硬编码或自创境界。
2. 正文出现系统面板/弹窗/提示框时，必须套用上方 UI 模版；除替换花括号变量值外，固定文字、分隔符、括号必须 100% 保持一致。
3. 若本章不需要系统面板，不要额外生成面板。"""
