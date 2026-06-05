"""User preference API for dynamic realms and system UI templates."""

from pathlib import Path
from fastapi import APIRouter, Depends

from dependencies import get_project_root
from services.user_preferences import UserPreference, load_user_preferences, save_user_preferences

router = APIRouter()


@router.get("")
async def get_preferences(root: Path = Depends(get_project_root)):
    return load_user_preferences(root)


@router.put("")
async def update_preferences(preference: UserPreference, root: Path = Depends(get_project_root)):
    saved = save_user_preferences(root, preference)
    return {"success": True, "preferences": saved}


@router.get("/prompt-context")
async def get_preferences_prompt_context(root: Path = Depends(get_project_root)):
    from services.user_preferences import build_preference_prompt_context

    return {"context": build_preference_prompt_context(root)}

