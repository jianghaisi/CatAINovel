"""Super admin APIs."""

import os

from fastapi import APIRouter, Header, HTTPException
from pydantic import BaseModel

from services.admin_settings import load_admin_settings, save_admin_settings
from services.auth_service import get_user_by_token, list_users
from services.membership_service import list_plans, update_plan_configs
from services.user_workspace import summarize_user_folder


router = APIRouter()


class AdminSettings(BaseModel):
    linux_do_client_id: str = ""
    linux_do_redirect_uri: str = ""
    linux_do_auth_url: str = ""
    login_mode: str = "email_password"


class MembershipPlansUpdate(BaseModel):
    plans: list[dict]


def _bearer_token(authorization: str | None) -> str:
    if not authorization:
        return ""
    return authorization.removeprefix("Bearer ").strip()


def require_admin(authorization: str | None) -> dict:
    user = get_user_by_token(_bearer_token(authorization))
    if not user:
        raise HTTPException(status_code=401, detail="未登录")
    super_admin_email = os.getenv("SUPER_ADMIN_EMAIL", "whilejack0@gmail.com").strip().lower()
    is_env_admin = bool(super_admin_email and user.email.lower() == super_admin_email)
    if user.role != "admin" and not is_env_admin:
        raise HTTPException(status_code=403, detail="需要超级管理员权限")
    return user.public_dict()


@router.get("/overview")
async def overview(authorization: str | None = Header(None)):
    require_admin(authorization)
    users = list_users()
    return {
        "users_count": len(users),
        "super_admin_email": os.getenv("SUPER_ADMIN_EMAIL", "whilejack0@gmail.com").strip(),
        "admin_hint": "第一个注册用户会自动成为超级管理员",
        "features": ["用户列表", "登录设置", "用户小说数据目录"],
        "settings": load_admin_settings(),
    }


@router.get("/users")
async def users(authorization: str | None = Header(None)):
    require_admin(authorization)
    return {"users": list_users()}


@router.get("/users/{user_id}/files")
async def user_files(user_id: str, authorization: str | None = Header(None)):
    require_admin(authorization)
    return summarize_user_folder(user_id)


@router.get("/settings")
async def get_settings(authorization: str | None = Header(None)):
    require_admin(authorization)
    return load_admin_settings()


@router.put("/settings")
async def update_settings(settings: AdminSettings, authorization: str | None = Header(None)):
    require_admin(authorization)
    saved = save_admin_settings(settings.model_dump())
    return {"success": True, "settings": saved}


@router.get("/membership/plans")
async def get_membership_plans(authorization: str | None = Header(None)):
    require_admin(authorization)
    return {"plans": list_plans(include_free=True)}


@router.put("/membership/plans")
async def update_membership_plans(request: MembershipPlansUpdate, authorization: str | None = Header(None)):
    require_admin(authorization)
    plans = update_plan_configs(request.plans)
    return {"success": True, "plans": list(plans.values())}
