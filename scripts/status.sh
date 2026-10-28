#!/usr/bin/env bash
cd "$(dirname "$0")/.."

git fetch --quiet origin || true

echo "== Onde estou =="
git status -sb
echo
echo "== Últimos commits =="
git --no-pager log --oneline -8
echo
echo "== Últimos pull requests mergeados =="
git --no-pager log --merges --oneline -8 origin/main
