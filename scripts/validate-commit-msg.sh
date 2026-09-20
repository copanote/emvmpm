#!/usr/bin/env bash
# 커밋 메시지 제목이 CONTRIBUTING.md의 "[type] 제목" 형식을 따르는지 검증한다.
# 사용법: validate-commit-msg.sh "<커밋 메시지 제목>"
set -euo pipefail

PATTERN='^\[(feat|fix|refactor|test|doc|style|ci|security|api|perf|chore|cleanup)\] .+'

subject="${1:-}"

if [[ -z "$subject" ]]; then
  echo "커밋 메시지 제목이 비어 있습니다." >&2
  exit 1
fi

# GitHub의 자동 병합 커밋 메시지는 검증 대상에서 제외한다.
if [[ "$subject" =~ ^Merge ]]; then
  exit 0
fi

if [[ ! "$subject" =~ $PATTERN ]]; then
  echo "커밋 메시지 형식이 올바르지 않습니다: \"$subject\"" >&2
  echo "형식: [type] 제목  (type: feat|fix|refactor|test|doc|style|ci|security|api|perf|chore|cleanup)" >&2
  echo "자세한 내용은 CONTRIBUTING.md를 참고하세요." >&2
  exit 1
fi
