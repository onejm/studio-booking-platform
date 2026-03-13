# Git Convention

## 목적

브랜치명과 커밋 메시지 형식을 통일해 변경 이력 추적, 리뷰, 검색을 쉽게 하기 위한 협업 규칙입니다.

참고:
- 이 문서는 코드 스타일 컨벤션이라기보다 Git/협업 컨벤션에 가깝습니다.
- 문서 위치는 관리 편의상 `docs/convention/` 하위에 둡니다.

## 기본 원칙

- 커밋 메시지에는 `TASK` 티켓만 포함합니다.
- 브랜치명에도 `TASK` 티켓만 포함합니다.
- `EPIC` 티켓은 커밋 메시지에 포함하지 않습니다.
- `EPIC` 연결은 노션 Relation 또는 PR 단위에서 관리합니다.
- 하나의 커밋은 가능하면 하나의 작업 목적만 담습니다.
- 하나의 브랜치는 가능하면 하나의 `TASK`만 담당합니다.

## 브랜치 네이밍 규칙

### 형식

```text
<type>/TASK-xxx-short-description
```

예시:

```text
feat/TASK-013-create-reservation-api
fix/TASK-021-duplicate-booking-validation
chore/TASK-034-migrate-to-application-yml
docs/TASK-040-add-git-convention-doc
```

### 브랜치 타입 (`<type>`)

- `feat`: 기능 개발
- `fix`: 버그 수정
- `refactor`: 리팩터링
- `chore`: 설정/빌드/환경 작업
- `docs`: 문서 작업
- `test`: 테스트 작업

### 설명 규칙 (`short-description`)

- 영어 `kebab-case`를 사용합니다.
- 짧고 검색 가능한 단어 중심으로 작성합니다.
- 구현 상세보다 작업 목적 중심으로 작성합니다.

좋은 예:
- `payment-timeout-retry`
- `reservation-policy-validation`
- `swagger-config`

지양:
- `temp`
- `work`
- `final-final`

## 커밋 메시지 형식

```text
[TASK-xxx] <gitmoji> <type>: <message>
```

예시:

```text
[TASK-013] ✨ feat: 예약 생성 API 추가
[TASK-021] 🐛 fix: 중복 예약 검증 누락 수정
[TASK-034] 🧹 chore: application.yml로 설정 파일 전환
```

## 구성 요소 설명

### 1. 티켓 번호 (`[TASK-xxx]`)

- 형식: `[TASK-001]`, `[TASK-013]`
- 숫자 자릿수는 가능하면 3자리 이상으로 고정합니다.
- 티켓이 없는 긴급 수정은 예외로 허용할 수 있지만, 팀 규칙으로 별도 관리합니다.

### 2. gitmoji (`<gitmoji>`)

- 커밋 성격을 빠르게 파악하기 위해 사용합니다.
- 팀에서 자주 쓰는 최소 세트를 먼저 고정해 사용하는 것을 권장합니다.

자주 사용하는 예시:

- `✨` 기능 추가
- `🐛` 버그 수정
- `🧹` 설정/정리/잡무성 변경
- `♻️` 리팩터링
- `📝` 문서 수정
- `✅` 테스트 추가/수정
- `🚚` 파일/구조 이동

### 3. 타입 (`<type>`)

Conventional Commits 스타일의 타입을 사용합니다.

- `feat`: 기능 추가
- `fix`: 버그 수정
- `chore`: 설정/빌드/잡무성 작업
- `refactor`: 동작 변경 없는 구조 개선
- `docs`: 문서 변경
- `test`: 테스트 추가/수정
- `style`: 포맷팅 등 비기능 변경

### 4. 메시지 (`<message>`)

- 한국어로 명확하게 작성합니다.
- 무엇을 바꿨는지 중심으로 작성합니다.
- 길어지면 상세 내용은 PR/노션 태스크에 작성합니다.

좋은 예:
- `예약 생성 API 추가`
- `Redis 연결 설정 분리`
- `Swagger UI 경로 설정 추가`

지양:
- `수정`
- `작업중`
- `이거저거 반영`

## 권장 운영 규칙

- 커밋: `TASK` 중심 추적
- 브랜치: `TASK` 중심 추적
- PR 제목: 필요 시 `EPIC` + `TASK` 함께 표기 가능
- 노션: `TASK`와 `EPIC`는 Relation으로 연결

## 예외/주의 사항

- 브랜치 하나에서 여러 `TASK`를 함께 처리하지 않습니다.
- 여러 태스크를 한 커밋에 섞어 넣지 않습니다.
- 리네이밍/정리와 기능 변경은 가능하면 커밋을 분리합니다.
- 대규모 리팩터링은 리뷰 가능한 단위로 잘게 나눕니다.
