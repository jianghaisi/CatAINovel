#!/usr/bin/env bash
set -Eeuo pipefail

HTTP_PORT="${HTTP_PORT:-8080}"

if [[ "${HTTP_PORT}" == "3000" ]]; then
  echo "ERROR: port 3000 is occupied/reserved. Use another HTTP_PORT, for example 8080 or 80." >&2
  exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}/.."

if [[ ! -f "deploy/docker.env" ]]; then
  cp deploy/docker.env.example deploy/docker.env
  echo "Created deploy/docker.env. Please edit API keys if this is the first deployment."
fi

mkdir -p data/home data/projects

export HTTP_PORT
docker compose -f deploy/docker-compose.yml up -d --build

echo
echo "Docker deploy complete."
echo "Open: http://YOUR_SERVER_IP:${HTTP_PORT}"
echo "Logs: docker compose -f deploy/docker-compose.yml logs -f"
