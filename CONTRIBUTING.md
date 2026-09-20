# 커밋 메시지 컨벤션

이 프로젝트는 아래 형식을 따릅니다. 기존 커밋 히스토리(`git log`)에서 자연스럽게 자리잡은 패턴을 정리한 것입니다.

## 형식

```
[type] 제목

- 상세 변경사항 1 (선택)
- 상세 변경사항 2 (선택)
```

- **제목**은 한국어로, 50자 이내로 간결하게 작성합니다. 마침표는 붙이지 않습니다.
- **본문**은 필요할 때만 작성합니다. 변경 이유나 세부 내역이 제목만으로 부족할 때 `-` 목록으로 덧붙입니다.
- `type`은 소문자로 통일합니다.

## 커밋 타입

| type | 설명 |
|---|---|
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 동작 변경 없는 코드 구조 개선 |
| `test` | 테스트 추가/수정 |
| `doc` | 문서 추가/수정 (README, Javadoc, USAGE 등) |
| `style` | 포맷팅 등 코드 스타일 변경 (동작 영향 없음) |
| `ci` | CI/CD 설정 변경 |
| `security` | 보안 취약점 수정 |
| `api` | 공개 API 변경 (추가/제거/시그니처 변경) |
| `perf` | 성능 개선 (Performance) |
| `chore` | 기타 잡다한 작업 (Chore) |
| `cleanup` | 코드 정리 (Cleanup) |


새로운 성격의 변경이라 위 타입 중 적절한 것이 없다면, 이 표에 타입을 추가하는 커밋을 먼저 남깁니다.

## 예시

```
[fix] EmvMpmNode.add()의 length 두 자리 검증 누락 수정
```

```
[security] EmvMpmPackager XML 파서의 XXE 취약점 차단
```

```
[refactor] Java 버전을 21로 업그레이드

- maven-compiler-plugin source/target을 11에서 21로 변경
- commons-codec 의존성 제거
- EmvMpmNode에서 org.apache.commons.codec.binary.Hex 대신 java.util.HexFormat 사용
```

## 커밋 템플릿 사용하기

리포 루트의 `.gitmessage` 템플릿을 아래 명령으로 적용하면, `git commit`(메시지 옵션 없이) 실행 시 이 형식이 자동으로 채워집니다.

```bash
git config commit.template .gitmessage
```

## 커밋 메시지 형식 검증

### 로컬 (커밋 전에 즉시 확인)

`.githooks/commit-msg` 훅을 활성화하면 `git commit` 시점에 제목 형식을 바로 검증합니다. 리포별 설정이라 각자 한 번씩 적용해야 합니다.

```bash
git config core.hooksPath .githooks
```

### CI (PR 시 자동 검증)

`.github/workflows/ci.yml`의 `commit-lint` 잡이 `main`으로의 PR마다 base..head 범위의 모든 커밋 제목을 검사합니다. 형식에 맞지 않는 커밋이 하나라도 있으면 체크가 실패합니다. (GitHub이 자동으로 생성하는 `Merge ...` 커밋 메시지는 검사에서 제외됩니다.)

두 검증 모두 `scripts/validate-commit-msg.sh`를 공유해서 사용합니다.
