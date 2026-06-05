"""Persistent super-admin settings."""

from __future__ import annotations

import json
from pathlib import Path
from typing import Any


SETTINGS_FILE = Path.home() / ".webnovel" / "admin_settings.json"


DEFAULT_SETTINGS = {
    "linux_do_client_id": "",
    "linux_do_redirect_uri": "",
    "linux_do_auth_url": "https://connect.linux.do/oauth2/authorize",
    "login_mode": "email_password",
}


def load_admin_settings() -> dict[str, Any]:
    if not SETTINGS_FILE.exists():
        return dict(DEFAULT_SETTINGS)
    try:
        data = json.loads(SETTINGS_FILE.read_text(encoding="utf-8"))
        return {**DEFAULT_SETTINGS, **data}
    except Exception:
        return dict(DEFAULT_SETTINGS)


def save_admin_settings(settings: dict[str, Any]) -> dict[str, Any]:
    SETTINGS_FILE.parent.mkdir(parents=True, exist_ok=True)
    data = {**DEFAULT_SETTINGS, **settings}
    SETTINGS_FILE.write_text(json.dumps(data, ensure_ascii=False, indent=2), encoding="utf-8")
    return data
