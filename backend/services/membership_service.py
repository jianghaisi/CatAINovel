"""Token-only membership, quota, group-buy orders, and payment integration."""

from __future__ import annotations

import json
import os
import re
import secrets
from copy import deepcopy
from datetime import datetime, timedelta
from pathlib import Path
from typing import Any

import httpx
from fastapi import Header, HTTPException

from services.auth_service import DEFAULT_SUPER_ADMIN_EMAIL, get_user_by_token, get_user_display_by_id, list_users
from services.projects_manager import GLOBAL_CONFIG_DIR

MEMBERSHIP_DIR = GLOBAL_CONFIG_DIR / "membership"
ORDERS_FILE = MEMBERSHIP_DIR / "orders.json"
MEMBERS_FILE = MEMBERSHIP_DIR / "members.json"
PLANS_FILE = MEMBERSHIP_DIR / "plans.json"
QUOTA_FILE = MEMBERSHIP_DIR / "quota.json"
GROUP_TEAMS_FILE = MEMBERSHIP_DIR / "group_teams.json"
ORDER_PAY_TTL_SECONDS = int(os.getenv("MEMBERSHIP_ORDER_PAY_TTL_SECONDS", "1800"))

CREATE_TEAM_SENTINELS = {"__create__", "create", "new", "open"}
PAID_STATUSES = {"PAY_SUCCESS", "DEAL_DONE", "TRADE_SUCCESS", "TRADE_FINISHED", "SUCCESS"}
LEGACY_BAD_DISCOUNT_RATES = {
    "pro": {0.1},
    "plus": {0.82},
    "max": {0.85},
}
MODEL_FEATURE_KEYWORDS = ("妯″瀷", "濡€崇€?, "model", "Model")

DEFAULT_FREE_PLAN: dict[str, Any] = {
    "id": "free",
    "name": "Free",
    "price": 0,
    "currency": "CNY",
    "period_days": 0,
    "monthly_token_quota": 0,
    "initial_tokens": 80_000,
    "daily_token_limit": 20_000,
    "checkin_reward_tokens": 10_000,
    "features": ["鍏嶈垂鍒涗綔璐︽埛", "姣忔棩鏈夐檺 Token", "绛惧埌澧炲姞浣欓"],
}

DEFAULT_PLAN_CONFIGS: dict[str, dict[str, Any]] = {
    "pro": {
        "id": "pro",
        "name": "Pro",
        "price": 19,
        "currency": "CNY",
        "period_days": 30,
        "monthly_token_quota": 1_000_000,
        "product_id": "member-pro",
        "market_type": 1,
        "activity_id": 100123,
        "team_size": 3,
        "group_strategy_type": "discount",
        "group_discount_rate": 0.8,
        "group_fixed_price": 15,
        "group_reduction_amount": 0,
        "group_threshold_amount": 0,
        "features": ["鏃ュ父绔犺妭鐢熸垚", "鍩虹 RAG 妫€绱?, "100 涓?Token / 鏈?],
    },
    "plus": {
        "id": "plus",
        "name": "Plus",
        "price": 49,
        "currency": "CNY",
        "period_days": 30,
        "monthly_token_quota": 5_000_000,
        "product_id": "member-plus",
        "market_type": 1,
        "activity_id": 100123,
        "team_size": 3,
        "group_strategy_type": "discount",
        "group_discount_rate": 0.8,
        "group_fixed_price": 39,
        "group_reduction_amount": 0,
        "group_threshold_amount": 0,
        "features": ["闀跨瘒鑷姩浠诲姟", "鏇撮珮鐢熸垚棰濆害", "500 涓?Token / 鏈?],
    },
    "max": {
        "id": "max",
        "name": "Max",
        "price": 99,
        "currency": "CNY",
        "period_days": 30,
        "monthly_token_quota": 20_000_000,
        "product_id": "member-max",
        "market_type": 1,
        "activity_id": 100123,
        "team_size": 3,
        "group_strategy_type": "discount",
        "group_discount_rate": 0.8,
        "group_fixed_price": 79,
        "group_reduction_amount": 0,
        "group_threshold_amount": 0,
        "features": ["鏈€楂?Token 棰濆害", "閫傚悎鐧句竾瀛楅暱绡?, "2000 涓?Token / 鏈?],
    },
}

SEED_TEAM_COUNTS = {"pro": 4, "plus": 3, "max": 2}
SEED_NAMES = ["鏋楄垷", "鍛ㄧ湢", "闄堥噹", "璁稿矚", "瀹嬫竻", "鍞愬畞", "椤剧櫧", "娌堢", "闊╂槦", "鍙舵緞", "闄嗗皬婊?, "闂诲"]


def _ensure_dir() -> None:
    MEMBERSHIP_DIR.mkdir(parents=True, exist_ok=True)


def _read_json(path: Path, default: Any) -> Any:
    _ensure_dir()
    if not path.exists():
        return deepcopy(default)
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except Exception:
        return deepcopy(default)


def _write_json(path: Path, data: Any) -> None:
    _ensure_dir()
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2), encoding="utf-8")


def _now() -> datetime:
    return datetime.utcnow()


def _today() -> str:
    return _now().date().isoformat()


def _as_datetime(value: Any) -> datetime | None:
    if isinstance(value, datetime):
        return value
    if not value:
        return None
    text = str(value).strip()
    for fmt in ("%Y-%m-%d %H:%M:%S", "%Y-%m-%dT%H:%M:%S"):
        try:
            return datetime.strptime(text[:19], fmt)
        except ValueError:
            continue
    return None


def _seconds_until(value: Any) -> int:
    end_at = _as_datetime(value)
    if not end_at:
        return 0
    return max(0, int((end_at - _now()).total_seconds()))


def _parse_time(value: str) -> datetime | None:
    try:
        return datetime.fromisoformat(value)
    except Exception:
        return None


def _super_admin_email() -> str:
    return os.getenv("SUPER_ADMIN_EMAIL", DEFAULT_SUPER_ADMIN_EMAIL).strip().lower()


def _user_email(user_id: str) -> str:
    for user in list_users():
        if user.get("id") == user_id:
            return str(user.get("email") or "").strip().lower()
    return ""


def _is_super_admin_user(user_id: str) -> bool:
    return bool(user_id and _user_email(user_id) == _super_admin_email())


def _default_plans() -> dict[str, dict[str, Any]]:
    plans = {"free": deepcopy(DEFAULT_FREE_PLAN)}
    plans.update(deepcopy(DEFAULT_PLAN_CONFIGS))
    return plans


def _drop_model_fields(plan: dict[str, Any]) -> dict[str, Any]:
    plan.pop("allowed_models", None)
    return plan


def _sanitize_legacy_plan(plan_id: str, plan: dict[str, Any], defaults: dict[str, dict[str, Any]]) -> dict[str, Any]:
    plan = _drop_model_fields(plan)
    if plan_id in DEFAULT_PLAN_CONFIGS:
        default_plan = defaults[plan_id]
        features = plan.get("features")
        if isinstance(features, list) and any(any(keyword in str(item) for keyword in MODEL_FEATURE_KEYWORDS) for item in features):
            plan["features"] = deepcopy(default_plan.get("features", []))
        if str(plan.get("group_strategy_type") or "discount") == "discount":
            try:
                rate = round(float(plan.get("group_discount_rate") or 0.8), 4)
            except Exception:
                rate = 0.8
            if rate in LEGACY_BAD_DISCOUNT_RATES.get(plan_id, set()):
                plan["group_discount_rate"] = 0.8
    return plan


def get_plan_configs(include_free: bool = False) -> dict[str, dict[str, Any]]:
    saved = _read_json(PLANS_FILE, {})
    plans = _default_plans()
    for plan_id, value in saved.items():
        if isinstance(value, dict) and plan_id in plans:
            plans[plan_id].update(value)
    defaults = _default_plans()
    plans = {key: _sanitize_legacy_plan(key, value, defaults) for key, value in plans.items()}
    if include_free:
        return plans
    return {key: value for key, value in plans.items() if key != "free"}


def update_plan_configs(plans: list[dict[str, Any]] | dict[str, Any]) -> dict[str, dict[str, Any]]:
    current = get_plan_configs(include_free=True)
    items = plans.values() if isinstance(plans, dict) else plans
    editable_keys = [
        "name", "price", "period_days", "monthly_token_quota", "initial_tokens",
        "daily_token_limit", "checkin_reward_tokens", "features", "product_id", "market_type",
        "activity_id", "team_size", "group_strategy_type", "group_discount_rate",
        "group_fixed_price", "group_reduction_amount", "group_threshold_amount",
    ]
    int_keys = {
        "price", "period_days", "monthly_token_quota", "initial_tokens", "daily_token_limit",
        "checkin_reward_tokens", "market_type", "activity_id", "team_size",
        "group_fixed_price", "group_reduction_amount", "group_threshold_amount",
    }
    for raw in items:
        if not isinstance(raw, dict):
            continue
        plan_id = str(raw.get("id", "")).strip().lower()
        if plan_id not in current:
            continue
        next_plan = current[plan_id]
        for key in editable_keys:
            if key in raw:
                next_plan[key] = raw[key]
        for key in int_keys:
            if key in next_plan and next_plan[key] not in ("", None):
                next_plan[key] = int(next_plan[key])
        if next_plan.get("group_discount_rate") not in ("", None):
            next_plan["group_discount_rate"] = float(next_plan.get("group_discount_rate") or 0.8)
        if isinstance(next_plan.get("features"), str):
            next_plan["features"] = [item.strip() for item in next_plan["features"].split("\n") if item.strip()]
        current[plan_id] = _drop_model_fields(next_plan)
    _write_json(PLANS_FILE, current)
    return current


def _group_price(plan: dict[str, Any]) -> int:
    price = int(plan.get("price") or 0)
    strategy = str(plan.get("group_strategy_type") or "discount").lower()
    if strategy == "fixed_price":
        return max(0, int(plan.get("group_fixed_price") or price))
    if strategy == "reduction":
        return max(0, price - int(plan.get("group_reduction_amount") or 0))
    if strategy == "threshold_reduction":
        threshold = int(plan.get("group_threshold_amount") or 0)
        reduction = int(plan.get("group_reduction_amount") or 0)
        return max(0, price - reduction) if price >= threshold else price
    rate = float(plan.get("group_discount_rate") or 0.8)
    return max(0, int(round(price * rate)))


def _strategy_label(plan: dict[str, Any]) -> str:
    strategy = str(plan.get("group_strategy_type") or "discount")
    if strategy == "fixed_price":
        return f"{int(plan.get('group_fixed_price') or _group_price(plan))} 鍏冭喘"
    if strategy == "reduction":
        return f"绔嬪噺 {int(plan.get('group_reduction_amount') or 0)} 鍏?
    if strategy == "threshold_reduction":
        return f"婊?{int(plan.get('group_threshold_amount') or 0)} 鍏冨噺 {int(plan.get('group_reduction_amount') or 0)} 鍏?
    return f"{float(plan.get('group_discount_rate') or 0.8) * 10:.1f} 鎶?


def resolve_plan_runtime(plan: dict[str, Any]) -> dict[str, Any]:
    resolved = _drop_model_fields(deepcopy(plan))
    if resolved["id"] != "free":
        resolved["product_id"] = _plan_product_id(resolved)
        resolved["activity_id"] = _plan_activity_id(resolved)
        resolved["market_type"] = _plan_market_type(resolved)
        resolved["team_size"] = int(resolved.get("team_size") or 3)
        resolved["group_price"] = _group_price(resolved)
        resolved["group_discount_label"] = _strategy_label(resolved)
    return resolved


def list_plans(include_free: bool = False) -> list[dict[str, Any]]:
    return [resolve_plan_runtime(plan) for plan in get_plan_configs(include_free=include_free).values()]


def get_plan(plan_id: str) -> dict[str, Any]:
    plan = get_plan_configs(include_free=True).get((plan_id or "").strip().lower())
    if not plan:
        raise ValueError("鏈煡浼氬憳濂楅")
    return resolve_plan_runtime(plan)


def _payment_base_url() -> str:
    return os.getenv("PAY_MALL_BASE_URL", os.getenv("GROUP_BUY_PAY_BASE_URL", "")).strip().rstrip("/")


def _group_buy_base_url() -> str:
    return os.getenv("GROUP_BUY_MARKET_BASE_URL", "http://group-buy-market:8091").strip().rstrip("/")


def _payment_timeout() -> float:
    return float(os.getenv("PAY_MALL_TIMEOUT", "20"))


def _truthy_env(name: str) -> bool:
    return os.getenv(name, "").strip().lower() in {"1", "true", "yes", "on"}


def _mysql_connect(database: str):
    try:
        import pymysql
        return pymysql.connect(
            host=os.getenv("MYSQL_HOST", "mysql"),
            port=int(os.getenv("MYSQL_PORT", "3306")),
            user=os.getenv("MYSQL_ROOT_USER", "root"),
            password=os.getenv("MYSQL_ROOT_PASSWORD", os.getenv("MYSQL_PASSWORD", "novel-root")),
            database=database,
            charset="utf8mb4",
            cursorclass=pymysql.cursors.DictCursor,
            autocommit=True,
        )
    except Exception as error:
        print(f"[MEMBERSHIP] MySQL unavailable for {database}: {error}")
        return None


def _group_buy_mysql():
    return _mysql_connect("group_buy_market")


def _external_group_teams_by_plan(plan: dict[str, Any]) -> list[dict[str, Any]]:
    conn = _group_buy_mysql()
    if not conn:
        return []
    activity_id = _plan_activity_id(plan)
    if not activity_id:
        return []
    with conn.cursor() as cursor:
        cursor.execute(
            """
            SELECT team_id, activity_id, target_count, complete_count, lock_count, status,
                   valid_start_time, valid_end_time, create_time
            FROM group_buy_order
            WHERE activity_id=%s
              AND status=0
              AND valid_end_time > NOW()
            ORDER BY create_time DESC
            LIMIT 20
            """,
            (activity_id,),
        )
        rows = cursor.fetchall()
        team_ids = [row["team_id"] for row in rows]
        members_by_team: dict[str, list[dict[str, Any]]] = {team_id: [] for team_id in team_ids}
        lock_counts_by_team: dict[str, int] = {team_id: 0 for team_id in team_ids}
        if team_ids:
            placeholders = ",".join(["%s"] * len(team_ids))
            cursor.execute(
                f"""
                SELECT team_id, COUNT(*) AS locked_count
                FROM group_buy_order_list
                WHERE team_id IN ({placeholders})
                  AND status IN (0, 1)
                GROUP BY team_id
                """,
                team_ids,
            )
            for row_count in cursor.fetchall():
                lock_counts_by_team[row_count["team_id"]] = int(row_count.get("locked_count") or 0)
            cursor.execute(
                f"""
                SELECT user_id, team_id, create_time
                FROM group_buy_order_list
                WHERE team_id IN ({placeholders})
                  AND status=1
                ORDER BY create_time ASC
                """,
                team_ids,
            )
            for member in cursor.fetchall():
                user_id = str(member.get("user_id") or "")
                display = get_user_display_by_id(user_id) or {
                    "user_id": user_id,
                    "email": "",
                    "name": user_id if len(user_id) <= 18 else f"{user_id[:8]}...{user_id[-4:]}",
                    "avatar": (user_id[:1] or "U").upper(),
                }
                members_by_team.setdefault(member["team_id"], []).append({
                    "user_id": user_id,
                    "email": display.get("email", ""),
                    "name": display.get("name") or user_id,
                    "avatar": display.get("avatar") or (user_id[:1] or "U").upper(),
                    "joined_at": str(member.get("create_time") or ""),
                    "source": "group_buy_market",
                })
    conn.close()
    teams = []
    for row in rows:
        team_size = int(row.get("target_count") or plan.get("team_size") or 3)
        members = members_by_team.get(row["team_id"], [])
        paid_count = len(members)
        locked_count = max(lock_counts_by_team.get(row["team_id"], 0), paid_count)
        # Pending payments reserve a seat, but they are not shown as paid
        # members and do not activate membership until pay-mall says paid.
        if locked_count >= team_size:
            continue
        remaining = max(0, team_size - locked_count)
        valid_end_time = row.get("valid_end_time")
        progress = int(round((paid_count / team_size) * 100)) if team_size else 0
        teams.append({
            "team_id": row["team_id"],
            "plan_id": plan["id"],
            "plan_name": plan.get("name", plan["id"].title()),
            "team_size": team_size,
            "joined": locked_count,
            "paid_count": paid_count,
            "locked_count": locked_count,
            "remaining": remaining,
            "members": members,
            "status": "recruiting",
            "group_price": plan.get("group_price", _group_price(plan)),
            "hint": f"杩樺樊 {max(0, team_size - paid_count)} 浜烘敮浠樻垚鍥?,
            "progress": progress,
            "valid_start_time": str(row.get("valid_start_time") or ""),
            "valid_end_time": str(valid_end_time or ""),
            "countdown_seconds": _seconds_until(valid_end_time),
            "created_at": str(row.get("create_time") or ""),
            "source": "group_buy_market",
        })
    return teams


def _external_team_exists(plan: dict[str, Any], team_id: str, user_id: str) -> bool:
    conn = _group_buy_mysql()
    if not conn:
        return False
    with conn.cursor() as cursor:
        cursor.execute(
            """
            SELECT team_id, target_count
            FROM group_buy_order
            WHERE team_id=%s AND activity_id=%s AND status=0 AND valid_end_time > NOW()
            """,
            (team_id, _plan_activity_id(plan)),
        )
        team = cursor.fetchone()
        if not team:
            conn.close()
            return False
        cursor.execute("SELECT COUNT(*) AS count FROM group_buy_order_list WHERE team_id=%s AND user_id=%s AND status IN (0, 1)", (team_id, user_id))
        exists = int(cursor.fetchone().get("count") or 0) > 0
        cursor.execute("SELECT COUNT(*) AS count FROM group_buy_order_list WHERE team_id=%s AND status IN (0, 1)", (team_id,))
        actual_members = int(cursor.fetchone().get("count") or 0)
        is_full = actual_members >= int(team.get("target_count") or plan.get("team_size") or 3)
    conn.close()
    return not exists and not is_full


def _plan_product_id(plan: dict[str, Any]) -> str:
    return os.getenv(f"MEMBERSHIP_{plan['id'].upper()}_PRODUCT_ID", str(plan.get("product_id") or plan["id"])).strip()


def _plan_activity_id(plan: dict[str, Any]) -> int | None:
    value = os.getenv(f"MEMBERSHIP_{plan['id'].upper()}_ACTIVITY_ID", "").strip()
    if value.isdigit():
        return int(value)
    default = plan.get("activity_id")
    return int(default) if default else None


def _plan_market_type(plan: dict[str, Any]) -> int:
    value = os.getenv(f"MEMBERSHIP_{plan['id'].upper()}_MARKET_TYPE", "").strip()
    if value.isdigit():
        return int(value)
    return int(plan.get("market_type", 0))


def _user_snapshot(user_id: str) -> dict[str, str]:
    for user in list_users():
        if user.get("id") == user_id:
            name = user.get("name") or user.get("email", "").split("@")[0] or "閻劍鍩?
            return {"user_id": user_id, "name": name, "avatar": name[:1].upper(), "joined_at": "閸掓艾鍨?, "seed": False}
    return {"user_id": user_id, "name": user_id[-6:] or "閻劍鍩?, "avatar": "U", "joined_at": "閸掓艾鍨?, "seed": False}


def _seed_member(plan_id: str, team_index: int, member_index: int) -> dict[str, Any]:
    name = SEED_NAMES[(team_index * 2 + member_index) % len(SEED_NAMES)]
    return {
        "user_id": f"seed-{plan_id}-{team_index}-{member_index}",
        "name": name,
        "avatar": name[:1],
        "joined_at": "閸掓艾鍨?,
        "seed": True,
    }


def _read_teams(seed: bool = True) -> dict[str, Any]:
    teams = _read_json(GROUP_TEAMS_FILE, {})
    if seed:
        teams = _ensure_seed_teams(teams)
    return teams


def _write_teams(teams: dict[str, Any]) -> None:
    _write_json(GROUP_TEAMS_FILE, teams)


def _ensure_seed_teams(teams: dict[str, Any]) -> dict[str, Any]:
    plans = get_plan_configs(include_free=False)
    changed = False
    for plan_id, count in SEED_TEAM_COUNTS.items():
        plan = resolve_plan_runtime(plans[plan_id])
        existing = [t for t in teams.values() if t.get("plan_id") == plan_id and t.get("seed") and t.get("status") == "recruiting"]
        seed_index = 1
        for idx in range(len(existing), count):
            joined = 2 if idx % 2 == 0 else 1
            while f"seed-{plan_id}-{seed_index}" in teams:
                seed_index += 1
            team_id = f"seed-{plan_id}-{seed_index}"
            members = [_seed_member(plan_id, seed_index, member_idx) for member_idx in range(joined)]
            teams[team_id] = {
                "team_id": team_id,
                "plan_id": plan_id,
                "team_size": int(plan.get("team_size") or 3),
                "member_user_ids": [m["user_id"] for m in members],
                "members": members,
                "owner_user_id": members[0]["user_id"],
                "status": "recruiting",
                "seed": True,
                "created_at": (_now() - timedelta(minutes=idx + 1)).isoformat(),
                "formed_at": "",
            }
            changed = True
    if changed:
        _write_teams(teams)
    return teams


def _create_team(plan: dict[str, Any], owner_user_id: str) -> dict[str, Any]:
    teams = _read_teams()
    team_id = f"team-{plan['id']}-{secrets.token_hex(6)}"
    team = {
        "team_id": team_id,
        "plan_id": plan["id"],
        "team_size": int(plan.get("team_size") or 3),
        "member_user_ids": [],
        "members": [],
        "owner_user_id": owner_user_id,
        "status": "recruiting",
        "seed": False,
        "created_at": _now().isoformat(),
        "formed_at": "",
    }
    teams[team_id] = team
    _write_teams(teams)
    return team


def _get_or_create_team(plan: dict[str, Any], user_id: str, team_id: str | None) -> dict[str, Any] | None:
    expire_pending_membership_orders()
    if not team_id:
        return None
    if _payment_base_url():
        if team_id in CREATE_TEAM_SENTINELS:
            return {"team_id": "", "plan_id": plan["id"], "team_size": int(plan.get("team_size") or 3), "external": True}
        if not _external_team_exists(plan, team_id, user_id):
            raise ValueError("璇ユ嫾鍥笉瀛樺湪銆佸凡婊″憳锛屾垨浣犲凡缁忓姞鍏ヨ繃璇ュ洟")
        return {"team_id": team_id, "plan_id": plan["id"], "team_size": int(plan.get("team_size") or 3), "external": True}
    if team_id in CREATE_TEAM_SENTINELS:
        return _create_team(plan, user_id)
    teams = _read_teams()
    team = teams.get(team_id)
    if not team or team.get("plan_id") != plan["id"] or team.get("status") != "recruiting":
        raise ValueError("璇ユ嫾鍥笉瀛樺湪鎴栧凡缁撴潫")
    if user_id in team.get("member_user_ids", []):
        raise ValueError("浣犲凡缁忓姞鍏ヨ繃璇ユ嫾鍥?)
    if len(team.get("member_user_ids", [])) >= int(team.get("team_size") or 3):
        raise ValueError("璇ユ嫾鍥㈠凡婊″憳")
    return team


def _add_paid_group_member(team_id: str, user_id: str) -> dict[str, Any] | None:
    teams = _read_teams()
    team = teams.get(team_id)
    if not team:
        return None
    member_ids = team.setdefault("member_user_ids", [])
    if user_id not in member_ids:
        member_ids.append(user_id)
        team.setdefault("members", []).append(_user_snapshot(user_id))
    if len(member_ids) >= int(team.get("team_size") or 3):
        team["status"] = "formed"
        team["formed_at"] = _now().isoformat()
        for paid_user_id in member_ids:
            if not str(paid_user_id).startswith("seed-"):
                _activate_membership(paid_user_id, team["plan_id"])
    teams[team_id] = team
    _write_teams(teams)
    return team


def list_group_buy_teams() -> list[dict[str, Any]]:
    expire_pending_membership_orders()
    plans = get_plan_configs(include_free=False)
    if _payment_base_url():
        teams: list[dict[str, Any]] = []
        for plan in plans.values():
            teams.extend(_external_group_teams_by_plan(resolve_plan_runtime(plan)))
        return sorted(teams, key=lambda item: item.get("created_at", ""), reverse=True)

    result = []
    for team in _read_teams().values():
        if team.get("status") != "recruiting":
            continue
        if team.get("plan_id") not in plans:
            continue
        plan = resolve_plan_runtime(plans[team["plan_id"]])
        members = team.get("members") or [_user_snapshot(user_id) for user_id in team.get("member_user_ids", [])]
        team_size = int(team.get("team_size") or plan.get("team_size") or 3)
        joined = len(team.get("member_user_ids", []))
        remaining = max(0, team_size - joined)
        result.append({
            "team_id": team["team_id"],
            "plan_id": team["plan_id"],
            "plan_name": plan.get("name", team["plan_id"].title()),
            "team_size": team_size,
            "joined": joined,
            "remaining": remaining,
            "members": members,
            "status": team.get("status", "recruiting"),
            "group_price": plan.get("group_price", _group_price(plan)),
            "hint": f"杩樺樊 {remaining} 浜烘垚鍥?,
            "created_at": team.get("created_at", ""),
            "seed": bool(team.get("seed")),
        })
    return sorted(result, key=lambda item: (item.get("seed", False), item.get("created_at", "")), reverse=True)


def _quota_record(user_id: str) -> dict[str, Any]:
    all_quota = _read_json(QUOTA_FILE, {})
    record = all_quota.get(user_id)
    if record:
        if record.get("daily_date") != _today():
            record["daily_date"] = _today()
            record["daily_used_tokens"] = 0
        return record
    free_plan = get_plan("free")
    record = {
        "user_id": user_id,
        "free_tokens": int(free_plan.get("initial_tokens", 0)),
        "daily_date": _today(),
        "daily_used_tokens": 0,
        "last_checkin_date": "",
        "checkin_days": 0,
        "updated_at": _now().isoformat(),
    }
    all_quota[user_id] = record
    _write_json(QUOTA_FILE, all_quota)
    return record


def _save_quota_record(user_id: str, record: dict[str, Any]) -> None:
    all_quota = _read_json(QUOTA_FILE, {})
    record["updated_at"] = _now().isoformat()
    all_quota[user_id] = record
    _write_json(QUOTA_FILE, all_quota)


def get_membership(user_id: str) -> dict[str, Any] | None:
    member = _read_json(MEMBERS_FILE, {}).get(user_id)
    if not member:
        return None
    expires_at = _parse_time(member.get("expires_at", ""))
    if not member.get("unlimited") and (not expires_at or expires_at <= _now()):
        member["status"] = "expired"
    return member


def _super_admin_public(user_id: str) -> dict[str, Any]:
    plan = get_plan("max")
    member = {
        "user_id": user_id,
        "plan_id": "max",
        "status": "active",
        "started_at": "",
        "expires_at": "",
        "token_quota": -1,
        "used_tokens": 0,
        "unlimited": True,
        "updated_at": _now().isoformat(),
    }
    return {
        "active": True,
        "plan": plan,
        "membership": member,
        "remaining_tokens": -1,
        "unlimited": True,
        "quota": {"free_tokens": 0, "daily_limit": 0, "daily_used_tokens": 0, "daily_remaining_tokens": -1, "checkin_reward_tokens": 0, "last_checkin_date": "", "checked_in_today": False, "checkin_days": 0},
    }


def get_public_membership(user_id: str) -> dict[str, Any]:
    if _is_super_admin_user(user_id):
        return _super_admin_public(user_id)
    free_plan = get_plan("free")
    quota = _quota_record(user_id)
    member = get_membership(user_id)
    active = bool(member and member.get("status") == "active")
    plan = get_plan(member["plan_id"]) if active else free_plan
    member_remaining = 0
    if active and member:
        member_remaining = max(0, int(member.get("token_quota", 0)) - int(member.get("used_tokens", 0)))
    free_daily_remaining = max(0, int(free_plan.get("daily_token_limit", 0)) - int(quota.get("daily_used_tokens", 0)))
    return {
        "active": active,
        "plan": plan,
        "membership": member,
        "remaining_tokens": member_remaining if active else int(quota.get("free_tokens", 0)),
        "unlimited": False,
        "quota": {
            "free_tokens": int(quota.get("free_tokens", 0)),
            "daily_limit": int(free_plan.get("daily_token_limit", 0)),
            "daily_used_tokens": int(quota.get("daily_used_tokens", 0)),
            "daily_remaining_tokens": free_daily_remaining,
            "checkin_reward_tokens": int(free_plan.get("checkin_reward_tokens", 0)),
            "last_checkin_date": quota.get("last_checkin_date", ""),
            "checked_in_today": quota.get("last_checkin_date") == _today(),
            "checkin_days": int(quota.get("checkin_days", 0)),
        },
    }


def checkin(user_id: str) -> dict[str, Any]:
    free_plan = get_plan("free")
    record = _quota_record(user_id)
    if record.get("last_checkin_date") == _today():
        return {"success": False, "message": "浠婂ぉ宸茬粡绛惧埌杩囦簡", **get_public_membership(user_id)}
    reward = int(free_plan.get("checkin_reward_tokens", 0))
    record["free_tokens"] = int(record.get("free_tokens", 0)) + reward
    record["last_checkin_date"] = _today()
    record["checkin_days"] = int(record.get("checkin_days", 0)) + 1
    _save_quota_record(user_id, record)
    return {"success": True, "message": f"绛惧埌鎴愬姛锛岃幏寰?{reward} Token", **get_public_membership(user_id)}


def _request_success(payload: dict[str, Any]) -> bool:
    return str(payload.get("code")) == "0000"


def _extract_out_trade_no(pay_url: str) -> str:
    patterns = [r"out_trade_no&quot;:&quot;([^&<\"]+)", r'"out_trade_no"\s*:\s*"([^"]+)"', r"out_trade_no=([^&\"'>]+)"]
    for pattern in patterns:
        match = re.search(pattern, pay_url or "")
        if match:
            return match.group(1)
    return ""


def _external_post(path: str, payload: dict[str, Any] | None = None, params: dict[str, Any] | None = None) -> dict[str, Any]:
    base_url = _payment_base_url()
    if not base_url:
        raise HTTPException(status_code=503, detail="鏀粯鏈嶅姟鏈厤缃細璇疯缃?PAY_MALL_BASE_URL")
    try:
        with httpx.Client(timeout=_payment_timeout()) as client:
            response = client.post(f"{base_url}{path}", json=payload, params=params)
            response.raise_for_status()
            return response.json()
    except HTTPException:
        raise
    except Exception as error:
        raise HTTPException(status_code=502, detail=f"鎷煎洟鏀粯鏈嶅姟璋冪敤澶辫触锛歿error}")


def _group_buy_post(path: str, payload: dict[str, Any]) -> dict[str, Any] | None:
    base_url = _group_buy_base_url()
    if not base_url:
        return None
    try:
        with httpx.Client(timeout=_payment_timeout()) as client:
            response = client.post(f"{base_url}{path}", json=payload)
            response.raise_for_status()
            return response.json()
    except Exception as error:
        print(f"[MEMBERSHIP] group-buy service call failed for {path}: {error}")
        return None


def _settle_external_group_order(user_id: str, order_id: str, pay_time: Any) -> None:
    base_url = _group_buy_base_url()
    if not base_url:
        return
    payload = {
        "source": "s01",
        "channel": "c01",
        "userId": user_id,
        "outTradeNo": order_id,
        "outTradeTime": str(pay_time or _now().strftime("%Y-%m-%d %H:%M:%S"))[:19],
    }
    try:
        with httpx.Client(timeout=_payment_timeout()) as client:
            response = client.post(f"{base_url}/api/v1/gbm/trade/settlement_market_pay_order", json=payload)
            response.raise_for_status()
            body = response.json()
        if not _request_success(body):
            raise HTTPException(status_code=502, detail=f"Group-buy settlement failed: {body.get('info') or body}")
    except HTTPException:
        raise
    except Exception as error:
        raise HTTPException(status_code=502, detail=f"Group-buy settlement service call failed: {error}")


def _create_external_payment(user_id: str, plan: dict[str, Any], team_id: str | None, is_group: bool) -> tuple[str, str, str]:
    if not _payment_base_url():
        local_order_id = f"local-{user_id}-{plan['id']}-{int(_now().timestamp())}-{secrets.token_hex(3)}"
        return local_order_id, local_order_id, ""
    payload = {
        "userId": user_id,
        "productId": _plan_product_id(plan),
        "teamId": team_id if is_group else None,
        "activityId": _plan_activity_id(plan) if is_group else None,
        "marketType": _plan_market_type(plan) if is_group else 0,
    }
    payment_response = _external_post("/api/v1/alipay/create_pay_order", payload)
    if not _request_success(payment_response):
        raise HTTPException(status_code=502, detail=f"鎷煎洟鏀粯涓嬪崟澶辫触锛歿payment_response.get('info') or payment_response}")
    pay_url = payment_response.get("data") or ""
    out_trade_no = _extract_out_trade_no(pay_url) or f"external-{user_id}-{plan['id']}-{int(_now().timestamp())}"
    return out_trade_no, out_trade_no, pay_url



def _order_expires_at(created_at: str | None = None) -> datetime:
    created = _parse_time(created_at or "") or _now()
    return created + timedelta(seconds=max(60, ORDER_PAY_TTL_SECONDS))


def _decorate_order(order: dict[str, Any]) -> dict[str, Any]:
    result = deepcopy(order)
    expires_at = result.get("expires_at") or _order_expires_at(result.get("created_at")).isoformat()
    result["expires_at"] = expires_at
    result["pay_ttl_seconds"] = max(0, int(((_parse_time(expires_at) or _now()) - _now()).total_seconds()))
    result["test_payment_enabled"] = _truthy_env("MEMBERSHIP_ENABLE_TEST_PAYMENT")
    return result


def _refund_external_order(user_id: str, order_id: str) -> dict[str, Any] | None:
    if not _payment_base_url():
        return None
    return _external_post("/api/v1/alipay/refund_order", {"userId": user_id, "orderId": order_id})


def _refund_external_group_order(user_id: str, order_id: str) -> dict[str, Any] | None:
    return _group_buy_post("/api/v1/gbm/trade/refund_market_pay_order", {
        "source": "s01",
        "channel": "c01",
        "userId": user_id,
        "outTradeNo": order_id,
    })


def _release_local_pending_group_seat(order: dict[str, Any]) -> None:
    team_id = str(order.get("team_id") or "")
    user_id = str(order.get("user_id") or "")
    if not team_id or not user_id:
        return
    teams = _read_teams(seed=False)
    team = teams.get(team_id)
    if not team or team.get("status") != "recruiting":
        return
    member_ids = team.get("member_user_ids", [])
    if user_id in member_ids:
        team["member_user_ids"] = [item for item in member_ids if item != user_id]
        team["members"] = [item for item in team.get("members", []) if item.get("user_id") != user_id]
        teams[team_id] = team
        _write_teams(teams)


def _expire_one_pending_order(order_id: str, order: dict[str, Any], *, reason: str = "expired") -> bool:
    if order.get("status") != "pending":
        return False
    expires_at = _parse_time(order.get("expires_at") or "")
    if expires_at and expires_at > _now():
        return False
    user_id = str(order.get("user_id") or "")
    external_order_id = str(order.get("external_order_id") or order_id)
    if not user_id:
        return False

    if _payment_base_url():
        pay_mall_released = False
        try:
            response = _refund_external_order(user_id, external_order_id)
            pay_mall_released = response is not None and _request_success(response) and bool((response.get("data") or {}).get("success", True))
            if response is not None and not pay_mall_released:
                print(f"[MEMBERSHIP] pay-mall refund failed for expired order {order_id}: {response}")
        except HTTPException as error:
            print(f"[MEMBERSHIP] pay-mall refund failed for expired order {order_id}: {error.detail}")
        if not pay_mall_released and order.get("purchase_mode") == "group" and order.get("team_id"):
            response = _refund_external_group_order(user_id, external_order_id)
            if response is not None and not _request_success(response):
                print(f"[MEMBERSHIP] group-buy refund failed for expired order {order_id}: {response}")
    elif order.get("purchase_mode") == "group":
        _release_local_pending_group_seat(order)

    order["status"] = reason
    order["expired_at"] = _now().isoformat()
    return True


def expire_pending_membership_orders() -> dict[str, Any]:
    orders = _read_json(ORDERS_FILE, {})
    changed = False
    expired_order_ids: list[str] = []
    for order_id, order in list(orders.items()):
        if _expire_one_pending_order(order_id, order):
            orders[order_id] = order
            expired_order_ids.append(order_id)
            changed = True
    if changed:
        _write_json(ORDERS_FILE, orders)
    return {"success": True, "expired_count": len(expired_order_ids), "order_ids": expired_order_ids}


def cancel_membership_order(user_id: str, order_id: str) -> dict[str, Any]:
    orders = _read_json(ORDERS_FILE, {})
    order = orders.get(order_id)
    if not order or order.get("user_id") != user_id:
        raise ValueError("Order not found")
    if order.get("status") == "paid":
        raise ValueError("Paid orders cannot be cancelled here")
    if order.get("status") == "cancelled":
        return {"success": True, "order": _decorate_order(order)}
    if order.get("status") == "expired":
        return {"success": True, "order": _decorate_order(order)}
    external_order_id = order.get("external_order_id") or order_id
    if _payment_base_url():
        response = _refund_external_order(user_id, external_order_id)
        if response is not None and not _request_success(response):
            raise HTTPException(status_code=502, detail=f"Cancel order failed: {response.get('info') or response}")
    order["status"] = "cancelled"
    order["cancelled_at"] = _now().isoformat()
    orders[order_id] = order
    _write_json(ORDERS_FILE, orders)
    return {"success": True, "order": _decorate_order(order)}


def simulate_membership_order_paid(user_id: str, order_id: str) -> dict[str, Any]:
    if not _truthy_env("MEMBERSHIP_ENABLE_TEST_PAYMENT"):
        raise HTTPException(status_code=403, detail="Test payment is disabled")
    orders = _read_json(ORDERS_FILE, {})
    order = orders.get(order_id)
    if not order or order.get("user_id") != user_id:
        raise ValueError("Order not found")
    if order.get("status") == "cancelled":
        raise ValueError("Cancelled order cannot be paid")
    external_order_id = order.get("external_order_id") or order_id
    if _payment_base_url():
        conn = _mysql_connect("s-pay-mall-ddd-market")
        if not conn:
            raise HTTPException(status_code=503, detail="Payment database is unavailable")
        try:
            with conn.cursor() as cursor:
                cursor.execute(
                    """
                    UPDATE pay_order
                    SET status='PAY_SUCCESS', pay_time=NOW(), update_time=NOW()
                    WHERE order_id=%s AND user_id=%s AND status <> 'PAY_SUCCESS'
                    """,
                    (external_order_id, user_id),
                )
        finally:
            conn.close()
    return confirm_membership_order(user_id, order_id)

def create_membership_order(user_id: str, plan_id: str, team_id: str | None = None) -> dict[str, Any]:
    expire_pending_membership_orders()
    plan = get_plan(plan_id)
    if plan["id"] == "free":
        raise ValueError("鍏嶈垂璐︽埛鏃犻渶璐拱")
    team = _get_or_create_team(plan, user_id, team_id)
    is_group = bool(team)
    order_id, external_order_id, pay_url = _create_external_payment(user_id, plan, (team.get("team_id") if team and team.get("team_id") else None), is_group)
    order = {
        "order_id": order_id,
        "external_order_id": external_order_id,
        "user_id": user_id,
        "plan_id": plan["id"],
        "product_id": _plan_product_id(plan),
        "team_id": team["team_id"] if team else "",
        "purchase_mode": "group" if is_group else "direct",
        "status": "pending",
        "amount": int(plan.get("group_price") if is_group else plan.get("price")),
        "direct_price": int(plan.get("price") or 0),
        "group_price": int(plan.get("group_price") or _group_price(plan)),
        "currency": plan["currency"],
        "provider": "jiang-group-buy" if _payment_base_url() else "local-test-payment",
        "payment_base_url": _payment_base_url(),
        "created_at": _now().isoformat(),
        "expires_at": "",
        "paid_at": "",
        "pay_url": pay_url,
    }
    orders = _read_json(ORDERS_FILE, {})
    order["expires_at"] = _order_expires_at(order.get("created_at")).isoformat()
    orders[order["order_id"]] = order
    _write_json(ORDERS_FILE, orders)
    return _decorate_order(order)


def _activate_membership(user_id: str, plan_id: str) -> dict[str, Any]:
    if str(user_id).startswith("seed-"):
        return {}
    plan = get_plan(plan_id)
    now = _now()
    member = {
        "user_id": user_id,
        "plan_id": plan["id"],
        "status": "active",
        "started_at": now.isoformat(),
        "expires_at": (now + timedelta(days=int(plan["period_days"]))).isoformat(),
        "token_quota": int(plan["monthly_token_quota"]),
        "used_tokens": 0,
        "unlimited": False,
        "updated_at": now.isoformat(),
    }
    members = _read_json(MEMBERS_FILE, {})
    members[user_id] = member
    _write_json(MEMBERS_FILE, members)
    return get_public_membership(user_id)


def _query_external_order(user_id: str, order_id: str) -> dict[str, Any] | None:
    response = _external_post("/api/v1/alipay/query_user_order_list", {"userId": user_id, "lastId": None, "pageSize": 50})
    if not _request_success(response):
        return None
    for order in (response.get("data") or {}).get("orderList") or []:
        if order.get("orderId") == order_id:
            return order
    return None


def _is_paid_external_order(order: dict[str, Any] | None) -> bool:
    if not order:
        return False
    return str(order.get("status") or "").upper() in PAID_STATUSES


def _external_group_payment_state(team_id: str) -> dict[str, Any] | None:
    conn = _group_buy_mysql()
    if not conn:
        return None
    try:
        with conn.cursor() as cursor:
            cursor.execute(
                """
                SELECT team_id, target_count, complete_count, lock_count, status
                FROM group_buy_order
                WHERE team_id=%s
                """,
                (team_id,),
            )
            team = cursor.fetchone()
            if not team:
                return None
            cursor.execute(
                """
                SELECT user_id, status, out_trade_no, create_time
                FROM group_buy_order_list
                WHERE team_id=%s
                ORDER BY create_time ASC
                """,
                (team_id,),
            )
            members = cursor.fetchall()
    finally:
        conn.close()

    paid_user_ids = [str(item.get("user_id") or "") for item in members if int(item.get("status") or 0) == 1]
    target_count = int(team.get("target_count") or 3)
    paid_count = len([user_id for user_id in paid_user_ids if user_id])
    formed = int(team.get("status") or 0) == 1 or paid_count >= target_count
    return {
        "team_id": team_id,
        "target_count": target_count,
        "paid_count": paid_count,
        "locked_count": len(members),
        "formed": formed,
        "paid_user_ids": paid_user_ids,
        "members": members,
    }


def _external_group_membership_result(order: dict[str, Any], user_id: str) -> dict[str, Any]:
    state = _external_group_payment_state(str(order.get("team_id") or ""))
    if not state:
        public = get_public_membership(user_id)
        public["active"] = False
        public["message"] = "鎷煎洟鐘舵€佹殏鏈悓姝ワ紝璇风◢鍚庡埛鏂般€?
        return public

    if state["formed"]:
        for paid_user_id in state["paid_user_ids"]:
            _activate_membership(paid_user_id, order["plan_id"])
        return get_public_membership(user_id)

    public = get_public_membership(user_id)
    public["active"] = False
    public["group_team"] = state
    public["message"] = f"宸叉敮浠樻嫾鍥环锛屽綋鍓嶅凡鏀粯 {state['paid_count']}/{state['target_count']}锛屾垚鍥㈠悗鑷姩寮€閫氥€?
    return public


def confirm_membership_order(user_id: str, order_id: str) -> dict[str, Any]:
    expire_pending_membership_orders()
    orders = _read_json(ORDERS_FILE, {})
    order = orders.get(order_id)
    if not order or order.get("user_id") != user_id:
        raise ValueError("璁㈠崟涓嶅瓨鍦?)
    if order.get("status") == "expired":
        raise HTTPException(status_code=410, detail="璁㈠崟宸茶秴鏃跺叧闂紝鎷煎洟鍗犱綅宸查噴鏀撅紝璇烽噸鏂颁笅鍗曘€?)
    if order.get("status") == "cancelled":
        raise HTTPException(status_code=410, detail="璁㈠崟宸插彇娑堬紝璇烽噸鏂颁笅鍗曘€?)
    if order.get("status") == "paid":
        if order.get("purchase_mode") == "group" and order.get("team_id") and _payment_base_url():
            return _external_group_membership_result(order, user_id)
        return get_public_membership(user_id)

    external_order_id = order.get("external_order_id") or order_id
    if _payment_base_url():
        external_order = _query_external_order(user_id, external_order_id)
        if not _is_paid_external_order(external_order):
            try:
                _external_post("/api/v1/alipay/active_pay_notify", params={"outTradeNo": external_order_id})
            except HTTPException as error:
                print(f"[MEMBERSHIP] active pay sync failed for {external_order_id}: {error.detail}")
            external_order = _query_external_order(user_id, external_order_id)
        if not _is_paid_external_order(external_order):
            raise HTTPException(status_code=409, detail="鏀粯灏氭湭瀹屾垚锛岃瀹屾垚鏀粯鍚庡啀鍒锋柊浼氬憳鐘舵€併€?)
        order["external_status"] = external_order
        if order.get("purchase_mode") == "group" and order.get("team_id") and not order.get("group_settled_at"):
            _settle_external_group_order(user_id, external_order_id, external_order.get("payTime") or external_order.get("pay_time"))
            order["group_settled_at"] = _now().isoformat()
    else:
        order["external_status"] = {"status": "PAY_SUCCESS", "source": "local-test-payment"}

    order["status"] = "paid"
    order["paid_at"] = _now().isoformat()
    orders[order_id] = order
    _write_json(ORDERS_FILE, orders)

    if order.get("purchase_mode") == "group" and order.get("team_id"):
        if _payment_base_url():
            return _external_group_membership_result(order, user_id)
        team = _add_paid_group_member(order["team_id"], user_id)
        if team and team.get("status") != "formed":
            public = get_public_membership(user_id)
            public["group_team"] = team
            public["active"] = False
            public["message"] = f"宸叉敮浠樻嫾鍥环锛屽綋鍓?{len(team.get('member_user_ids', []))}/{team.get('team_size', 3)}锛屾垚鍥㈠悗鑷姩寮€閫氥€?
            return public
        return get_public_membership(user_id)

    return _activate_membership(user_id, order["plan_id"])


def _token_from_header(authorization: str | None) -> str:
    if not authorization:
        return ""
    return authorization.removeprefix("Bearer ").strip()


def require_auth_user(authorization: str | None) -> Any:
    user = get_user_by_token(_token_from_header(authorization))
    if not user:
        raise HTTPException(status_code=401, detail="璇峰厛鐧诲綍")
    return user


def consume_tokens(user_id: str, amount: int) -> dict[str, Any]:
    amount = max(0, int(amount or 0))
    public = get_public_membership(user_id)
    if public.get("unlimited"):
        return public
    if public.get("active"):
        members = _read_json(MEMBERS_FILE, {})
        member = members.get(user_id)
        if not member:
            raise HTTPException(status_code=402, detail="浼氬憳鐘舵€佸紓甯革紝璇烽噸鏂扮櫥褰?)
        remaining = int(member.get("token_quota", 0)) - int(member.get("used_tokens", 0))
        if amount > remaining:
            raise HTTPException(status_code=402, detail="浼氬憳 Token 棰濆害涓嶈冻锛岃鍗囩骇鎴栫画璐?)
        member["used_tokens"] = int(member.get("used_tokens", 0)) + amount
        member["updated_at"] = _now().isoformat()
        members[user_id] = member
        _write_json(MEMBERS_FILE, members)
        return member

    free_plan = get_plan("free")
    quota = _quota_record(user_id)
    daily_remaining = int(free_plan.get("daily_token_limit", 0)) - int(quota.get("daily_used_tokens", 0))
    balance = int(quota.get("free_tokens", 0))
    if amount > daily_remaining:
        raise HTTPException(status_code=402, detail="浠婃棩鍏嶈垂 Token 宸茬敤瀹岋紝璇锋槑澶╁啀鏉ャ€佺鍒板鍔犱綑棰濓紝鎴栧紑閫氫細鍛?)
    if amount > balance:
        raise HTTPException(status_code=402, detail="鍏嶈垂 Token 浣欓涓嶈冻锛岃绛惧埌鎴栧紑閫氫細鍛?)
    quota["free_tokens"] = balance - amount
    quota["daily_used_tokens"] = int(quota.get("daily_used_tokens", 0)) + amount
    _save_quota_record(user_id, quota)
    return quota


def require_active_membership(
    authorization: str | None = Header(None),
    *,
    estimated_tokens: int = 0,
    model: str = "",
) -> tuple[Any, dict[str, Any]]:
    user = require_auth_user(authorization)
    public = get_public_membership(user.id)
    if estimated_tokens > 0:
        consume_tokens(user.id, estimated_tokens)
        public = get_public_membership(user.id)
    return user, public
