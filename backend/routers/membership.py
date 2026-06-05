"""Membership and payment APIs."""

from fastapi import APIRouter, Header, HTTPException
from pydantic import BaseModel

from services.membership_service import (
    cancel_membership_order,
    checkin,
    confirm_membership_order,
    create_membership_order,
    get_public_membership,
    list_group_buy_teams,
    list_plans,
    require_auth_user,
    simulate_membership_order_paid,
)

router = APIRouter()


class CreateMembershipOrderRequest(BaseModel):
    plan_id: str
    team_id: str | None = None


class ConfirmMembershipOrderRequest(BaseModel):
    order_id: str


@router.get("/plans")
async def plans():
    return {"plans": list_plans(include_free=True)}


@router.get("/group-teams")
async def group_teams():
    return {"teams": list_group_buy_teams()}


@router.get("/me")
async def my_membership(authorization: str | None = Header(None)):
    user = require_auth_user(authorization)
    return get_public_membership(user.id)


@router.post("/checkin")
async def daily_checkin(authorization: str | None = Header(None)):
    user = require_auth_user(authorization)
    return checkin(user.id)


@router.post("/orders")
async def create_order(request: CreateMembershipOrderRequest, authorization: str | None = Header(None)):
    user = require_auth_user(authorization)
    try:
        order = create_membership_order(user.id, request.plan_id, request.team_id)
        return {"success": True, "order": order}
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))


@router.post("/orders/confirm")
async def confirm_order(request: ConfirmMembershipOrderRequest, authorization: str | None = Header(None)):
    user = require_auth_user(authorization)
    try:
        membership = confirm_membership_order(user.id, request.order_id)
        return {"success": True, **membership}
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))


@router.post("/orders/cancel")
async def cancel_order(request: ConfirmMembershipOrderRequest, authorization: str | None = Header(None)):
    user = require_auth_user(authorization)
    try:
        return cancel_membership_order(user.id, request.order_id)
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))


@router.post("/orders/test-pay")
async def test_pay_order(request: ConfirmMembershipOrderRequest, authorization: str | None = Header(None)):
    user = require_auth_user(authorization)
    try:
        membership = simulate_membership_order_paid(user.id, request.order_id)
        return {"success": True, **membership}
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))
