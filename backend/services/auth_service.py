"""Authentication service with MySQL/Redis-ready storage and local fallback."""

from __future__ import annotations

import hashlib
import json
import os
import secrets
import smtplib
import httpx
import shutil
from dataclasses import asdict, dataclass
from datetime import datetime, timedelta
from email.message import EmailMessage
from pathlib import Path
from typing import Any, Optional
from urllib.parse import urlencode

from services.admin_settings import load_admin_settings
from services.projects_manager import GLOBAL_CONFIG_DIR
from services import projects_manager
from services.user_workspace import get_user_dir


AUTH_DIR = GLOBAL_CONFIG_DIR / "auth"
DEFAULT_SUPER_ADMIN_EMAIL = "whilejack0@gmail.com"
DEFAULT_OWNER_PROJECT_NAME = "亲亲就能强，从幼儿园开始无敌"
DEFAULT_OWNER_PROJECT_PATH = Path(__file__).resolve().parents[1] / "data" / DEFAULT_OWNER_PROJECT_NAME
USERS_FILE = AUTH_DIR / "users.json"
CODES_FILE = AUTH_DIR / "email_codes.json"
SESSIONS_FILE = AUTH_DIR / "sessions.json"


@dataclass
class AuthUser:
    id: str
    email: str
    name: str
    provider: str = "email"
    role: str = "user"
    status: str = "active"
    created_at: str = ""
    data_dir: str = ""

    def public_dict(self) -> dict[str, Any]:
        default_project = ensure_default_project_for_user(self)
        data = asdict(self)
        data["role"] = "admin" if _is_super_admin_email(self.email) else self.role
        data["data_dir"] = str(get_user_dir(self.id))
        data["default_project"] = default_project
        return data


def _now() -> datetime:
    return datetime.utcnow()


def _ensure_dir() -> None:
    AUTH_DIR.mkdir(parents=True, exist_ok=True)


def _read_json(path: Path, default: Any) -> Any:
    _ensure_dir()
    if not path.exists():
        return default
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except Exception:
        return default


def _write_json(path: Path, data: Any) -> None:
    _ensure_dir()
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2), encoding="utf-8")


def _hash_token(value: str) -> str:
    return hashlib.sha256(value.encode("utf-8")).hexdigest()


def _normalize_email(email: str) -> str:
    return (email or "").strip().lower()


def _dev_show_code_enabled() -> bool:
    return os.getenv("AUTH_DEV_SHOW_CODE", "").strip().lower() in {"1", "true", "yes", "on"}


def _smtp_configured() -> bool:
    return bool(os.getenv("SMTP_HOST", "").strip())


def _redis_client():
    redis_url = os.getenv("REDIS_URL", "").strip()
    if not redis_url:
        return None
    try:
        import redis
        return redis.Redis.from_url(redis_url, decode_responses=True)
    except Exception as error:
        print(f"[AUTH] Redis unavailable, falling back to files: {error}")
        return None


def _mysql_enabled() -> bool:
    return os.getenv("AUTH_STORAGE", "").strip().lower() == "mysql"


def _mysql_connect():
    try:
        import pymysql
        return pymysql.connect(
            host=os.getenv("MYSQL_HOST", "mysql"),
            port=int(os.getenv("MYSQL_PORT", "3306")),
            user=os.getenv("MYSQL_USER", "novel"),
            password=os.getenv("MYSQL_PASSWORD", "novel"),
            database=os.getenv("MYSQL_DATABASE", "novel_agent"),
            charset="utf8mb4",
            cursorclass=pymysql.cursors.DictCursor,
            autocommit=True,
        )
    except Exception as error:
        print(f"[AUTH] MySQL unavailable, falling back to files: {error}")
        return None


def _ensure_mysql_schema(conn) -> None:
    with conn.cursor() as cursor:
        cursor.execute(
            """
            CREATE TABLE IF NOT EXISTS users (
                id VARCHAR(64) PRIMARY KEY,
                email VARCHAR(255) UNIQUE NOT NULL,
                password_hash VARCHAR(255),
                name VARCHAR(128) NOT NULL,
                provider VARCHAR(32) NOT NULL DEFAULT 'email',
                role VARCHAR(32) NOT NULL DEFAULT 'user',
                status VARCHAR(32) NOT NULL DEFAULT 'active',
                created_at DATETIME NOT NULL,
                updated_at DATETIME NULL
            ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
            """
        )


def _user_from_record(record: dict[str, Any]) -> AuthUser:
    email = record.get("email", "")
    role = "admin" if _is_super_admin_email(email) else record.get("role", "user")
    return AuthUser(
        id=record.get("id", ""),
        email=email,
        name=record.get("name") or email.split("@")[0],
        provider=record.get("provider", "email"),
        role=role,
        status=record.get("status", "active"),
        created_at=str(record.get("created_at") or ""),
        data_dir=str(get_user_dir(record.get("id", ""))),
    )


def _read_users() -> dict[str, Any]:
    return _read_json(USERS_FILE, {})


def _write_users(users: dict[str, Any]) -> None:
    _write_json(USERS_FILE, users)


def _get_user_record(email: str) -> Optional[dict[str, Any]]:
    email = _normalize_email(email)
    if _mysql_enabled():
        conn = _mysql_connect()
        if conn:
            _ensure_mysql_schema(conn)
            with conn.cursor() as cursor:
                cursor.execute("SELECT * FROM users WHERE email=%s", (email,))
                return cursor.fetchone()
    return _read_users().get(email)


def _get_user_record_by_id(user_id: str) -> Optional[dict[str, Any]]:
    if _mysql_enabled():
        conn = _mysql_connect()
        if conn:
            _ensure_mysql_schema(conn)
            with conn.cursor() as cursor:
                cursor.execute("SELECT * FROM users WHERE id=%s", (user_id,))
                return cursor.fetchone()
    for record in _read_users().values():
        if record.get("id") == user_id:
            return record
    return None


def get_user_display_by_id(user_id: str) -> dict[str, str] | None:
    record = _get_user_record_by_id(user_id)
    if not record:
        return None
    email = str(record.get("email") or "").strip().lower()
    name = str(record.get("name") or "").strip() or (email.split("@")[0] if email else "")
    return {
        "user_id": str(record.get("id") or user_id),
        "email": email,
        "name": name or user_id,
        "avatar": (name or email or user_id or "U")[:1].upper(),
    }


def _is_super_admin_email(email: str) -> bool:
    configured = os.getenv("SUPER_ADMIN_EMAIL", DEFAULT_SUPER_ADMIN_EMAIL).strip().lower()
    return _normalize_email(email) == configured


def _first_user_role(email: str = "") -> str:
    if _is_super_admin_email(email):
        return "admin"
    if _mysql_enabled():
        conn = _mysql_connect()
        if conn:
            _ensure_mysql_schema(conn)
            with conn.cursor() as cursor:
                cursor.execute("SELECT COUNT(*) AS count FROM users")
                return "admin" if int(cursor.fetchone()["count"]) == 0 else "user"
    return "admin" if not _read_users() else "user"


def ensure_default_project_for_user(user: AuthUser | dict[str, Any] | None) -> dict[str, Any] | None:
    if not user:
        return None
    email = getattr(user, "email", "") if hasattr(user, "email") else str(user.get("email", ""))
    if not _is_super_admin_email(email):
        # New ordinary accounts start empty; they create or import projects manually.
        return None
    user_id = getattr(user, "id", "") if hasattr(user, "id") else str(user.get("id", ""))
    if not user_id:
        return None
    target = get_user_dir(user_id) / "projects" / DEFAULT_OWNER_PROJECT_NAME
    if DEFAULT_OWNER_PROJECT_PATH.exists() and not target.exists():
        shutil.copytree(DEFAULT_OWNER_PROJECT_PATH, target)
    if not target.exists():
        return None
    result = projects_manager.import_project(str(target), user_id=user_id)
    if result.get("success"):
        return result.get("project")
    return {
        "id": f"default-{user_id}",
        "name": DEFAULT_OWNER_PROJECT_NAME,
        "path": str(target),
        "genre": "",
        "created_at": "",
        "last_opened": "",
        "exists": True,
    }


def _save_user_record(record: dict[str, Any]) -> None:
    if _mysql_enabled():
        conn = _mysql_connect()
        if conn:
            _ensure_mysql_schema(conn)
            with conn.cursor() as cursor:
                cursor.execute(
                    """
                    INSERT INTO users (id, email, password_hash, name, provider, role, status, created_at, updated_at)
                    VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)
                    ON DUPLICATE KEY UPDATE
                        password_hash=VALUES(password_hash),
                        name=VALUES(name),
                        provider=VALUES(provider),
                        role=VALUES(role),
                        status=VALUES(status),
                        updated_at=VALUES(updated_at)
                    """,
                    (
                        record["id"], record["email"], record.get("password_hash"), record["name"],
                        record.get("provider", "email"), record.get("role", "user"),
                        record.get("status", "active"), record.get("created_at"), record.get("updated_at"),
                    ),
                )
            return
    users = _read_users()
    users[record["email"]] = record
    _write_users(users)


def _hash_password(password: str) -> str:
    try:
        import bcrypt
        return bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")
    except Exception:
        salt = secrets.token_hex(16)
        digest = hashlib.pbkdf2_hmac("sha256", password.encode("utf-8"), salt.encode("utf-8"), 200_000).hex()
        return f"pbkdf2${salt}${digest}"


def _verify_password(password: str, password_hash: str) -> bool:
    if password_hash.startswith("$2"):
        import bcrypt
        return bool(bcrypt.checkpw(password.encode("utf-8"), password_hash.encode("utf-8")))
    if password_hash.startswith("pbkdf2$"):
        _, salt, digest = password_hash.split("$", 2)
        actual = hashlib.pbkdf2_hmac("sha256", password.encode("utf-8"), salt.encode("utf-8"), 200_000).hex()
        return secrets.compare_digest(actual, digest)
    return False


def _send_email_code(email: str, code: str) -> None:
    host = os.getenv("SMTP_HOST", "").strip()
    port = int(os.getenv("SMTP_PORT", "587"))
    username = os.getenv("SMTP_USERNAME", "").strip()
    password = os.getenv("SMTP_PASSWORD", "").strip()
    sender = os.getenv("SMTP_FROM", username or "no-reply@localhost").strip()

    if not host:
        print(f"[AUTH] Email verification code for {email}: {code}")
        return

    message = EmailMessage()
    message["Subject"] = "小猫写作验证码"
    message["From"] = sender
    message["To"] = email
    message.set_content(f"你的验证码是：{code}\n\n验证码 5 分钟内有效。")

    with smtplib.SMTP(host, port, timeout=20) as smtp:
        smtp.starttls()
        if username:
            smtp.login(username, password)
        smtp.send_message(message)


def send_verification_code(email: str, purpose: str = "login") -> dict[str, Any]:
    email = _normalize_email(email)
    if "@" not in email:
        raise ValueError("请输入有效邮箱")
    code = f"{secrets.randbelow(1_000_000):06d}"
    redis_client = _redis_client()
    payload = {
        "code_hash": _hash_token(code),
        "purpose": purpose,
        "expires_at": (_now() + timedelta(minutes=5)).isoformat(),
        "created_at": _now().isoformat(),
    }
    if redis_client:
        redis_client.setex(f"email_code:{email}", 300, json.dumps(payload, ensure_ascii=False))
    else:
        codes = _read_json(CODES_FILE, {})
        codes[email] = payload
        _write_json(CODES_FILE, codes)
    _send_email_code(email, code)
    result: dict[str, Any] = {
        "email": email,
        "expires_in_seconds": 300,
        "smtp_configured": _smtp_configured(),
        "dev_code_enabled": _dev_show_code_enabled(),
    }
    if result["dev_code_enabled"]:
        result["dev_code"] = code
    return result


def verify_email_code(email: str, code: str) -> bool:
    email = _normalize_email(email)
    redis_client = _redis_client()
    item = None
    if redis_client:
        raw = redis_client.get(f"email_code:{email}")
        item = json.loads(raw) if raw else None
    if not item:
        item = _read_json(CODES_FILE, {}).get(email)
    if not item:
        return False
    try:
        if datetime.fromisoformat(item["expires_at"]) < _now():
            return False
    except Exception:
        return False
    return secrets.compare_digest(item.get("code_hash", ""), _hash_token((code or "").strip()))


def upsert_user(email: str, name: Optional[str] = None, provider: str = "email", password: str = "") -> AuthUser:
    email = _normalize_email(email)
    existing = _get_user_record(email)
    now = _now().strftime("%Y-%m-%d %H:%M:%S")
    if existing:
        record = dict(existing)
        if _is_super_admin_email(email) and record.get("role") != "admin":
            record["role"] = "admin"
        record["name"] = name or record.get("name") or email.split("@")[0]
        record["provider"] = provider or record.get("provider", "email")
        record["updated_at"] = now
        if password:
            record["password_hash"] = _hash_password(password)
    else:
        record = {
            "id": f"user-{secrets.token_hex(8)}",
            "email": email,
            "password_hash": _hash_password(password) if password else "",
            "name": name or email.split("@")[0],
            "provider": provider,
            "role": _first_user_role(email),
            "status": "active",
            "created_at": now,
            "updated_at": now,
        }
    _save_user_record(record)
    user = _user_from_record(record)
    return user


def register_with_password(email: str, password: str, code: str, name: str = "") -> AuthUser:
    if len(password or "") < 8:
        raise ValueError("密码至少需要 8 位")
    if _get_user_record(email):
        raise ValueError("该邮箱已注册，请直接登录")
    if not verify_email_code(email, code):
        raise ValueError("验证码无效或已过期")
    return upsert_user(email, name=name, provider="email", password=password)


def login_with_password(email: str, password: str) -> AuthUser:
    record = _get_user_record(email)
    if not record or not record.get("password_hash"):
        raise ValueError("邮箱或密码错误")
    if record.get("status", "active") != "active":
        raise ValueError("账号已被禁用")
    if not _verify_password(password, record.get("password_hash", "")):
        raise ValueError("邮箱或密码错误")
    return _user_from_record(record)


def create_session(user: AuthUser) -> str:
    token = secrets.token_urlsafe(32)
    payload = {
        "user_id": user.id,
        "created_at": _now().isoformat(),
        "expires_at": (_now() + timedelta(days=30)).isoformat(),
    }
    redis_client = _redis_client()
    if redis_client:
        redis_client.setex(f"session:{_hash_token(token)}", 30 * 24 * 3600, json.dumps(payload))
    else:
        sessions = _read_json(SESSIONS_FILE, {})
        sessions[_hash_token(token)] = {"user": user.public_dict(), **payload}
        _write_json(SESSIONS_FILE, sessions)
    return token


def get_user_by_token(token: str) -> Optional[AuthUser]:
    if not token:
        return None
    token_hash = _hash_token(token)
    redis_client = _redis_client()
    if redis_client:
        raw = redis_client.get(f"session:{token_hash}")
        if not raw:
            return None
        item = json.loads(raw)
        record = _get_user_record_by_id(item.get("user_id", ""))
        return _user_from_record(record) if record else None

    item = _read_json(SESSIONS_FILE, {}).get(token_hash)
    if not item:
        return None
    try:
        if datetime.fromisoformat(item["expires_at"]) < _now():
            return None
    except Exception:
        return None
    user = item.get("user", {})
    return _user_from_record(user)


def delete_session(token: str) -> None:
    redis_client = _redis_client()
    if redis_client:
        redis_client.delete(f"session:{_hash_token(token or '')}")
        return
    sessions = _read_json(SESSIONS_FILE, {})
    sessions.pop(_hash_token(token or ""), None)
    _write_json(SESSIONS_FILE, sessions)


def list_users() -> list[dict[str, Any]]:
    if _mysql_enabled():
        conn = _mysql_connect()
        if conn:
            _ensure_mysql_schema(conn)
            with conn.cursor() as cursor:
                cursor.execute("SELECT * FROM users ORDER BY created_at DESC")
                return [_user_from_record(row).public_dict() for row in cursor.fetchall()]
    return [_user_from_record(row).public_dict() for row in _read_users().values()]


def linux_do_oauth_url() -> str:
    admin_settings = load_admin_settings()
    client_id = os.getenv("LINUX_DO_CLIENT_ID", "").strip() or admin_settings.get("linux_do_client_id", "").strip()
    redirect_uri = os.getenv("LINUX_DO_REDIRECT_URI", "").strip() or admin_settings.get("linux_do_redirect_uri", "").strip()
    auth_url = (
        os.getenv("LINUX_DO_AUTH_URL", "").strip()
        or admin_settings.get("linux_do_auth_url", "").strip()
        or "https://connect.linux.do/oauth2/authorize"
    )
    if not client_id or not redirect_uri:
        raise ValueError("Linux.do 登录未配置：需要 LINUX_DO_CLIENT_ID 和 LINUX_DO_REDIRECT_URI")
    params = {
        "response_type": "code",
        "client_id": client_id,
        "redirect_uri": redirect_uri,
        "scope": "openid email profile",
        "state": secrets.token_urlsafe(16),
    }
    return f"{auth_url}?{urlencode(params)}"


async def login_with_linux_do_code(code: str) -> tuple[str, AuthUser]:
    client_id = os.getenv("LINUX_DO_CLIENT_ID", "").strip()
    client_secret = os.getenv("LINUX_DO_CLIENT_SECRET", "").strip()
    redirect_uri = os.getenv("LINUX_DO_REDIRECT_URI", "").strip()
    token_url = os.getenv("LINUX_DO_TOKEN_URL", "").strip()
    userinfo_url = os.getenv("LINUX_DO_USERINFO_URL", "").strip()
    if not all([client_id, client_secret, redirect_uri, token_url, userinfo_url]):
        raise ValueError("Linux.do OAuth 未配置完整：需要 CLIENT_ID、CLIENT_SECRET、REDIRECT_URI、TOKEN_URL、USERINFO_URL")

    async with httpx.AsyncClient(timeout=20, follow_redirects=True) as client:
        token_response = await client.post(
            token_url,
            data={
                "grant_type": "authorization_code",
                "code": code,
                "redirect_uri": redirect_uri,
                "client_id": client_id,
                "client_secret": client_secret,
            },
            headers={"Accept": "application/json"},
        )
        token_response.raise_for_status()
        token_data = token_response.json()
        access_token = token_data.get("access_token")
        if not access_token:
            raise ValueError("Linux.do OAuth 未返回 access_token")

        user_response = await client.get(
            userinfo_url,
            headers={"Authorization": f"Bearer {access_token}", "Accept": "application/json"},
        )
        user_response.raise_for_status()
        profile = user_response.json()

    email = (profile.get("email") or "").strip().lower()
    external_id = str(profile.get("sub") or profile.get("id") or profile.get("username") or "").strip()
    if not email:
        if not external_id:
            raise ValueError("Linux.do 用户信息缺少 email 和 id")
        email = f"linuxdo-{external_id}@users.local"
    name = profile.get("name") or profile.get("username") or email.split("@")[0]
    user = upsert_user(email, name=name, provider="linux.do")
    return create_session(user), user
