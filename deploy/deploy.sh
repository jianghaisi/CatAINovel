#!/usr/bin/env bash
set -Eeuo pipefail

APP_NAME="${APP_NAME:-novel-agent}"
APP_DIR="${APP_DIR:-/opt/langgraph-novel-agent}"
SERVICE_NAME="${SERVICE_NAME:-novel-agent}"
SERVICE_USER="${SERVICE_USER:-${SUDO_USER:-$(whoami)}}"
BACKEND_PORT="${BACKEND_PORT:-8001}"
NGINX_HTTP_PORT="${NGINX_HTTP_PORT:-80}"
SERVER_NAME="${SERVER_NAME:-_}"
PYTHON_BIN="${PYTHON_BIN:-python3}"
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

if [[ "${BACKEND_PORT}" == "3000" || "${NGINX_HTTP_PORT}" == "3000" ]]; then
  echo "ERROR: port 3000 is reserved/occupied. Choose another BACKEND_PORT or NGINX_HTTP_PORT." >&2
  exit 1
fi

need_cmd() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "ERROR: missing command: $1" >&2
    exit 1
  }
}

if [[ "$(id -u)" -ne 0 ]]; then
  need_cmd sudo
  SUDO="sudo"
else
  SUDO=""
fi

need_cmd rsync
need_cmd node
need_cmd npm
need_cmd "${PYTHON_BIN}"
need_cmd curl

NODE_MAJOR="$(node -p "Number(process.versions.node.split('.')[0])" 2>/dev/null || echo 0)"
if [[ "${NODE_MAJOR}" -lt 20 ]]; then
  echo "ERROR: Node.js 20+ is required for Vite 7. Current: $(node -v 2>/dev/null || echo missing)" >&2
  exit 1
fi

echo "==> Deploying ${APP_NAME}"
echo "    Source: ${PROJECT_ROOT}"
echo "    Target: ${APP_DIR}"
echo "    Backend: 127.0.0.1:${BACKEND_PORT}"
echo "    Nginx: :${NGINX_HTTP_PORT} (${SERVER_NAME})"
echo "    Service user: ${SERVICE_USER}"

echo "==> Creating directories"
${SUDO} mkdir -p "${APP_DIR}" /etc/novel-agent
${SUDO} chown -R "${SERVICE_USER}:${SERVICE_USER}" "${APP_DIR}"

echo "==> Syncing project files"
SOURCE_REAL="$(readlink -f "${PROJECT_ROOT}")"
TARGET_REAL="$(readlink -f "${APP_DIR}")"
if [[ "${SOURCE_REAL}" != "${TARGET_REAL}" ]]; then
  rsync -a --delete \
    --exclude ".git/" \
    --exclude "frontend/node_modules/" \
    --exclude "frontend/dist/" \
    --exclude "backend/__pycache__/" \
    --exclude "**/__pycache__/" \
    --exclude ".pycache-test/" \
    --exclude ".venv/" \
    --exclude ".env" \
    "${PROJECT_ROOT}/" "${APP_DIR}/"
else
  echo "    Source and target are the same directory; skipping rsync."
fi
${SUDO} chown -R "${SERVICE_USER}:${SERVICE_USER}" "${APP_DIR}"

echo "==> Preparing backend virtualenv"
cd "${APP_DIR}/backend"
if [[ ! -d ".venv" ]]; then
  "${PYTHON_BIN}" -m venv .venv
fi
./.venv/bin/python -m pip install --upgrade pip
./.venv/bin/pip install -r requirements.txt

echo "==> Preparing production environment file"
if [[ ! -f "/etc/novel-agent/novel-agent.env" ]]; then
  ${SUDO} cp "${APP_DIR}/deploy/env.production.example" /etc/novel-agent/novel-agent.env
  ${SUDO} chmod 600 /etc/novel-agent/novel-agent.env
  ${SUDO} chown root:root /etc/novel-agent/novel-agent.env
  echo "    Created /etc/novel-agent/novel-agent.env; edit API keys after deploy if needed."
fi

echo "==> Building frontend"
cd "${APP_DIR}/frontend"
if [[ -f "package-lock.json" ]]; then
  npm ci
else
  npm install
fi
npm run build

echo "==> Installing systemd service"
SERVICE_FILE="/etc/systemd/system/${SERVICE_NAME}.service"
${SUDO} sed \
  -e "s#__APP_DIR__#${APP_DIR}#g" \
  -e "s#__SERVICE_USER__#${SERVICE_USER}#g" \
  -e "s#__BACKEND_PORT__#${BACKEND_PORT}#g" \
  "${APP_DIR}/deploy/novel-agent.service.template" | ${SUDO} tee "${SERVICE_FILE}" >/dev/null

${SUDO} systemctl daemon-reload
${SUDO} systemctl enable "${SERVICE_NAME}"
${SUDO} systemctl restart "${SERVICE_NAME}"

echo "==> Installing nginx site"
if [[ ! -d "/etc/nginx/sites-available" || ! -d "/etc/nginx/sites-enabled" ]]; then
  echo "ERROR: this script expects Debian/Ubuntu style nginx directories: /etc/nginx/sites-available and /etc/nginx/sites-enabled" >&2
  exit 1
fi
NGINX_SITE="/etc/nginx/sites-available/${APP_NAME}.conf"
${SUDO} sed \
  -e "s#__APP_DIR__#${APP_DIR}#g" \
  -e "s#__BACKEND_PORT__#${BACKEND_PORT}#g" \
  -e "s#__NGINX_HTTP_PORT__#${NGINX_HTTP_PORT}#g" \
  -e "s#__SERVER_NAME__#${SERVER_NAME}#g" \
  "${APP_DIR}/deploy/nginx.conf.template" | ${SUDO} tee "${NGINX_SITE}" >/dev/null

${SUDO} ln -sfn "${NGINX_SITE}" "/etc/nginx/sites-enabled/${APP_NAME}.conf"
${SUDO} nginx -t
${SUDO} systemctl reload nginx

echo "==> Smoke checking backend"
sleep 2
if curl -fsS "http://127.0.0.1:${BACKEND_PORT}/api/ai/config" >/dev/null; then
  echo "Backend OK"
else
  echo "WARNING: backend smoke check failed. Run: sudo journalctl -u ${SERVICE_NAME} -n 100 --no-pager" >&2
fi

echo
echo "Deploy complete."
if [[ "${SERVER_NAME}" == "_" ]]; then
  echo "Open: http://YOUR_SERVER_IP:${NGINX_HTTP_PORT}"
else
  echo "Open: http://${SERVER_NAME}:${NGINX_HTTP_PORT}"
fi
echo "Logs: sudo journalctl -u ${SERVICE_NAME} -f"
echo "Env:  sudo nano /etc/novel-agent/novel-agent.env"
