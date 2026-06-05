#!/usr/bin/env bash
set -Eeuo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
HTTP_PORT="${HTTP_PORT:-8080}"
PAY_MALL_PORT="${PAY_MALL_PORT:-8070}"
GROUP_BUY_PORT="${GROUP_BUY_PORT:-8091}"
SERVER_NAME="${SERVER_NAME:-}"
COMPOSE_ENV="${COMPOSE_ENV:-${PROJECT_ROOT}/deploy/docker.env}"
COMPOSE_FILE="${PROJECT_ROOT}/deploy/docker-compose.full.yml"
PAY_MALL_DIR="${PROJECT_ROOT}/integrations/jiang-pintuan/s-pay-mall-ddd-market"
GROUP_BUY_DIR="${PROJECT_ROOT}/integrations/jiang-pintuan/group-buy-market"

need_cmd() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "ERROR: missing command: $1" >&2
    exit 1
  }
}

need_cmd docker

if docker compose version >/dev/null 2>&1; then
  DOCKER_COMPOSE=(docker compose)
else
  need_cmd docker-compose
  DOCKER_COMPOSE=(docker-compose)
fi

if [[ "${HTTP_PORT}" == "3000" || "${PAY_MALL_PORT}" == "3000" || "${GROUP_BUY_PORT}" == "3000" ]]; then
  echo "ERROR: port 3000 is reserved/occupied. Use HTTP_PORT/PAY_MALL_PORT/GROUP_BUY_PORT other than 3000." >&2
  exit 1
fi

if [[ ! -f "${PAY_MALL_DIR}/pom.xml" || ! -d "${PAY_MALL_DIR}/s-pay-mall-ddd-app" ]]; then
  echo "ERROR: missing vendored payment project: ${PAY_MALL_DIR}" >&2
  exit 1
fi

if [[ ! -f "${GROUP_BUY_DIR}/pom.xml" || ! -d "${GROUP_BUY_DIR}/group-buy-market-app" ]]; then
  echo "ERROR: missing vendored group-buy project: ${GROUP_BUY_DIR}" >&2
  exit 1
fi

if [[ -z "${SERVER_NAME}" ]]; then
  SERVER_NAME="$(hostname -I 2>/dev/null | awk '{print $1}')"
fi

FRONTEND_URL="${FRONTEND_URL:-http://${SERVER_NAME}:${HTTP_PORT}}"
PAY_MALL_PUBLIC_URL="${PAY_MALL_PUBLIC_URL:-http://${SERVER_NAME}:${PAY_MALL_PORT}}"
GROUP_BUY_SQL="${GROUP_BUY_DIR}/docs/dev-ops/mysql/sql/2-29-group_buy_market.sql"

echo "==> Full deploy: 灏忕尗鍐欎綔 + 鎷煎洟钀ラ攢 + 鏀粯鍟嗗煄"
echo "    Project: ${PROJECT_ROOT}"
echo "    Frontend: ${FRONTEND_URL}"
echo "    Pay callback: ${PAY_MALL_PUBLIC_URL}/api/v1/alipay/alipay_notify_url"
echo "    Group buy source: ${GROUP_BUY_DIR}"
echo "    Pay mall source: ${PAY_MALL_DIR}"

echo "==> Ensuring docker env"
if [[ ! -f "${COMPOSE_ENV}" ]]; then
  cp "${PROJECT_ROOT}/deploy/docker.env.example" "${COMPOSE_ENV}"
fi

set_env_value() {
  local key="$1"
  local value="$2"
  if grep -q "^${key}=" "${COMPOSE_ENV}"; then
    sed -i "s#^${key}=.*#${key}=${value}#g" "${COMPOSE_ENV}"
  else
    printf "\n%s=%s\n" "${key}" "${value}" >> "${COMPOSE_ENV}"
  fi
}

env_value() {
  local key="$1"
  local fallback="$2"
  local value
  value="$(grep -E "^${key}=" "${COMPOSE_ENV}" | tail -n 1 | cut -d= -f2- || true)"
  if [[ -n "${value}" ]]; then
    printf "%s" "${value}"
  else
    printf "%s" "${fallback}"
  fi
}

set_env_value "HTTP_PORT" "${HTTP_PORT}"
set_env_value "PAY_MALL_PORT" "${PAY_MALL_PORT}"
set_env_value "GROUP_BUY_PORT" "${GROUP_BUY_PORT}"
set_env_value "FRONTEND_URL" "${FRONTEND_URL}/"
set_env_value "PAY_MALL_PUBLIC_URL" "${PAY_MALL_PUBLIC_URL}"
set_env_value "PAY_MALL_RETURN_URL" "${FRONTEND_URL}/membership"
set_env_value "PAY_MALL_BASE_URL" "http://pay-mall:8070"
set_env_value "MEMBERSHIP_PRO_PRODUCT_ID" "member-pro"
set_env_value "MEMBERSHIP_PLUS_PRODUCT_ID" "member-plus"
set_env_value "MEMBERSHIP_MAX_PRODUCT_ID" "member-max"
set_env_value "MEMBERSHIP_PRO_MARKET_TYPE" "1"
set_env_value "MEMBERSHIP_PLUS_MARKET_TYPE" "1"
set_env_value "MEMBERSHIP_MAX_MARKET_TYPE" "1"
set_env_value "MEMBERSHIP_PRO_ACTIVITY_ID" "100123"
set_env_value "MEMBERSHIP_PLUS_ACTIVITY_ID" "100123"
set_env_value "MEMBERSHIP_MAX_ACTIVITY_ID" "100123"

MYSQL_ROOT_PASSWORD_VALUE="$(env_value MYSQL_ROOT_PASSWORD novel-root)"

echo "==> Building and starting containers"
cd "${PROJECT_ROOT}"
"${DOCKER_COMPOSE[@]}" --env-file "${COMPOSE_ENV}" -f "${COMPOSE_FILE}" up -d --build

echo "==> Applying payment database schema"
"${DOCKER_COMPOSE[@]}" --env-file "${COMPOSE_ENV}" -f "${COMPOSE_FILE}" exec -T mysql \
  mysql -uroot -p"${MYSQL_ROOT_PASSWORD_VALUE}" < "${PROJECT_ROOT}/deploy/mysql-init/01-pay-mall.sql" || true

GROUP_TABLE_EXISTS="$("${DOCKER_COMPOSE[@]}" --env-file "${COMPOSE_ENV}" -f "${COMPOSE_FILE}" exec -T mysql \
  mysql -N -uroot -p"${MYSQL_ROOT_PASSWORD_VALUE}" -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='group_buy_market' AND table_name='sku';" 2>/dev/null || echo 0)"

if [[ "${GROUP_TABLE_EXISTS}" == "0" ]]; then
  if [[ -f "${GROUP_BUY_SQL}" ]]; then
    echo "==> Applying group-buy-market schema"
    "${DOCKER_COMPOSE[@]}" --env-file "${COMPOSE_ENV}" -f "${COMPOSE_FILE}" exec -T mysql \
      mysql -uroot -p"${MYSQL_ROOT_PASSWORD_VALUE}" < "${GROUP_BUY_SQL}" || true
  else
    echo "WARNING: group_buy_market SQL file not found: ${GROUP_BUY_SQL}" >&2
  fi
else
  echo "==> group_buy_market schema already exists; skip destructive import"
fi

echo "==> Applying membership SKU seed"
"${DOCKER_COMPOSE[@]}" --env-file "${COMPOSE_ENV}" -f "${COMPOSE_FILE}" exec -T mysql \
  mysql -uroot -p"${MYSQL_ROOT_PASSWORD_VALUE}" < "${PROJECT_ROOT}/deploy/group-buy-membership-seed.sql" || true

echo "==> Restarting app services after schema bootstrap"
"${DOCKER_COMPOSE[@]}" --env-file "${COMPOSE_ENV}" -f "${COMPOSE_FILE}" restart group-buy-market pay-mall backend

echo "==> Container status"
"${DOCKER_COMPOSE[@]}" --env-file "${COMPOSE_ENV}" -f "${COMPOSE_FILE}" ps

echo
echo "Deploy complete."
echo "Open 灏忕尗鍐欎綔: ${FRONTEND_URL}"
echo "Pay service:   ${PAY_MALL_PUBLIC_URL}/api/v1/alipay/create_pay_order"
echo "Group buy:     http://${SERVER_NAME}:${GROUP_BUY_PORT}/api/v1/gbm/index/query_group_buy_market_config"
echo "Logs:"
echo "  ${DOCKER_COMPOSE[*]} --env-file ${COMPOSE_ENV} -f ${COMPOSE_FILE} logs -f backend"
echo "  ${DOCKER_COMPOSE[*]} --env-file ${COMPOSE_ENV} -f ${COMPOSE_FILE} logs -f pay-mall"
echo "  ${DOCKER_COMPOSE[*]} --env-file ${COMPOSE_ENV} -f ${COMPOSE_FILE} logs -f group-buy-market"
