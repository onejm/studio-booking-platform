# studio-booking-platform
실시간 예약 동시성 제어와 멀티 테넌트 데이터 분리를 핵심으로, 음악·공연·댄스 등 시간제 공간의 중복 예약을 방지하고 운영 자동화와 이력 관리를 지원하는 통합 예약 관리 SaaS입니다.

## Docker 실행 방법 (MySQL / Redis)

프로젝트 루트의 `docker-compose.yml`로 로컬 개발용 MySQL, Redis를 실행할 수 있습니다.

### 1) 컨테이너 실행

```bash
docker compose up -d
```

### 2) 실행 상태 확인

```bash
docker compose ps
```

### 3) 로그 확인 (필요 시)

```bash
docker compose logs -f mysql
docker compose logs -f redis
```

### 4) 컨테이너 중지/삭제

```bash
docker compose down
```

### 5) 볼륨까지 삭제 (DB 데이터 초기화)

```bash
docker compose down -v
```

## 로컬 개발용 기본 접속 정보

### MySQL

- Host: `localhost`
- Port: `3307`
- Database: `studio_reservation`
- Username: `studio`
- Password: `studio123`
- Root Password: `1234`

### Redis

- Host: `localhost`
- Port: `6379`

## 참고

- MySQL 설정 파일: `docker/mysql/my.cnf`
- 문자셋/콜레이션은 `utf8mb4`, `utf8mb4_unicode_ci`로 설정되어 있습니다.
