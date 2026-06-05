"""Authentication API for 小猫写作."""

import os
from urllib.parse import urlencode

from fastapi import APIRouter, Header, HTTPException, Query
from fastapi.responses import RedirectResponse
from pydantic import BaseModel

from services.auth_service import (
    create_session,
    delete_session,
    get_user_by_token,
    linux_do_oauth_url,
    login_with_linux_do_code,
    login_with_password,
    register_with_password,
    send_verification_code,
    upsert_user,
    verify_email_code,
)


router = APIRouter()


class EmailCodeRequest(BaseModel):
    email: str
    purpose: str = "login"


class EmailLoginRequest(BaseModel):
    email: str
    code: str
    name: str = ""


class PasswordRegisterRequest(BaseModel):
    email: str
    code: str
    password: str
    name: str = ""


class PasswordLoginRequest(BaseModel):
    email: str
    password: str


def _bearer_token(authorization: str | None) -> str:
    if not authorization:
        return ""
    prefix = "Bearer "
    return authorization[len(prefix):].strip() if authorization.startswith(prefix) else authorization.strip()


@router.post("/email/code")
async def request_email_code(request: EmailCodeRequest):
    try:
        result = send_verification_code(request.email, request.purpose)
        message = "验证码已发送。"
        if not result.get("smtp_configured"):
            message = "验证码已生成。未配置 SMTP 时，请查看后端日志。"
        payload = {"success": True, "message": message, **result}
        if not result.get("dev_code_enabled"):
            payload.pop("dev_code", None)
        return payload
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))
    except Exception as error:
        raise HTTPException(status_code=500, detail=f"发送验证码失败：{error}")


@router.post("/email/login")
async def email_login(request: EmailLoginRequest):
    if not verify_email_code(request.email, request.code):
        raise HTTPException(status_code=400, detail="验证码无效或已过期")
    user = upsert_user(request.email, request.name, provider="email")
    token = create_session(user)
    return {"success": True, "token": token, "user": user.public_dict()}


@router.post("/email/register")
async def email_register(request: PasswordRegisterRequest):
    try:
        user = register_with_password(request.email, request.password, request.code, request.name)
        token = create_session(user)
        return {"success": True, "token": token, "user": user.public_dict()}
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))


@router.post("/password/login")
async def password_login(request: PasswordLoginRequest):
    try:
        user = login_with_password(request.email, request.password)
        token = create_session(user)
        return {"success": True, "token": token, "user": user.public_dict()}
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))


@router.get("/me")
async def me(authorization: str | None = Header(None)):
    user = get_user_by_token(_bearer_token(authorization))
    if not user:
        raise HTTPException(status_code=401, detail="未登录")
    return {"user": user.public_dict()}


@router.post("/logout")
async def logout(authorization: str | None = Header(None)):
    delete_session(_bearer_token(authorization))
    return {"success": True}


@router.get("/linux-do/url")
async def linux_do_url():
    try:
        return {"url": linux_do_oauth_url()}
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))


@router.get("/linux-do/callback")
async def linux_do_callback(code: str = Query(...), state: str = ""):
    try:
        token, user = await login_with_linux_do_code(code)
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))
    except Exception as error:
        raise HTTPException(status_code=500, detail=f"Linux.do 登录失败：{error}")

    frontend_url = os.getenv("FRONTEND_URL", "").strip()
    if frontend_url:
        redirect_base = f"{frontend_url.rstrip('/')}/login"
        return RedirectResponse(
            f"{redirect_base}?{urlencode({'auth_token': token})}",
            status_code=302,
        )
    return {"success": True, "token": token, "user": user.public_dict()}
