#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

carregar_env() {
  set -a
  . ./.env
  set +a
}

case "${1:-}" in
  db)
    docker compose up -d --wait db
    ;;
  reset-db)
    docker compose down
    docker compose up -d --wait db
    ;;
  test)
    docker compose up -d --wait db
    carregar_env
    ./mvnw test
    ;;
  run)
    docker compose up -d --wait db
    carregar_env
    ./mvnw spring-boot:run
    ;;
  *)
    echo "uso: scripts/dev.sh {db|reset-db|test|run}"
    exit 1
    ;;
esac