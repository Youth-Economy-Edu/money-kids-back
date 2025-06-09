# Money Kids Back - 데이터베이스 설정 가이드 📊

## 🎯 개요
청소년 경제교육 플랫폼 **Money Kids Back**의 데이터베이스 재생성 스크립트 모음입니다.

## 📁 파일 설명

### 1. `database_setup.sql` - 스키마만 생성
- **용도**: 깨끗한 데이터베이스 스키마만 필요한 경우
- **내용**: 모든 테이블 구조 + 인덱스
- **특징**: 초기 데이터 없음

### 2. `database_with_data.sql` - 스키마 + 기본 데이터
- **용도**: 즉시 사용 가능한 완전한 데이터베이스
- **내용**: 모든 테이블 구조 + 인덱스 + 기본 주식 데이터
- **특징**: 9개의 기본 주식 정보 포함

### 3. `database_recreate.sql` - 완전한 백업 (자동 생성)
- **용도**: 현재 데이터베이스의 완전한 백업
- **내용**: 모든 테이블 + 현재 저장된 모든 데이터
- **특징**: mysqldump로 자동 생성된 완전한 덤프

## 🚀 사용 방법

### 방법 1: 기본 설정 (권장)
```bash
# 스키마 + 기본 데이터로 새로 시작
mysql -u root -p < database_with_data.sql
```

### 방법 2: 스키마만 생성
```bash
# 빈 데이터베이스 스키마만 생성
mysql -u root -p < database_setup.sql
```

### 방법 3: 완전한 복원
```bash
# 현재 백업된 모든 데이터 복원
mysql -u root -p < database_recreate.sql
```

## 🗂️ 데이터베이스 구조

### 📊 주요 테이블들

#### 👤 사용자 관리
- `USER` - 사용자 정보 (OAuth2 지원)
- `tendency_analysis` - 사용자 투자 성향 분석
- `activity_log` - 사용자 활동 로그

#### 🧩 교육 콘텐츠  
- `QUIZ` - 경제 퀴즈 (114개)
- `WORKSHEET` - 학습 워크시트 (15개)
- `user_quiz` - 사용자 퀴즈 결과
- `daily_quest` - 일일 퀘스트

#### 📈 주식 시뮬레이션
- `stock` - 주식 정보 (9개 기본 종목)
- `stock_price_log` - 주식 가격 이력  
- `user_stock` - 사용자 주식 보유
- `stock_log` - 주식 거래 이력
- `wishlist` - 관심 종목

#### 📰 AI 뉴스 시스템
- `article` - AI 생성 뉴스 기사 (45개)
- 뉴스 기반 주식 가격 변동 시스템

#### 💬 피드백
- `FEEDBACK` - 사용자 피드백
- `stock_notification` - 주식 알림

## 🎮 기본 제공 데이터

### 주식 종목 (9개)
1. **맥도날드** (MCD) - 외식업
2. **레고 코리아** (LEGO) - 완구업  
3. **포켓몬카드** (PKMN) - 엔터테인먼트
4. **넥슨게임즈** (NEXON) - IT/게임
5. **오리온** (ORION) - 식품업
6. **농심** (NONGSHIM) - 식품업
7. **스타벅스 코리아** (SBUX) - 외식업
8. **배스킨라빈스** (BR) - 외식업
9. **삼성전자** (SAMSUNG) - IT/전자

### 특징
- 청소년들이 친숙한 브랜드 중심
- 다양한 업종 포함 (IT, 식품, 외식, 완구 등)
- 회사 규모별 분류 (LARGE, MEDIUM)

## ⚙️ 환경 설정

### 1. 환경변수 설정 (.env)
```bash
# 데이터베이스 연결 정보
DB_URL=jdbc:mysql://localhost:3306/moneykids?serverTimezone=Asia/Seoul
DB_USERNAME=root
DB_PASSWORD=your_password
```

### 2. MySQL 접속 확인
```bash
mysql -u root -p
```

### 3. 권한 확인
```sql
SHOW GRANTS FOR 'root'@'localhost';
```

## 🔧 문제 해결

### 권한 오류
```bash
# MySQL 권한 부여
GRANT ALL PRIVILEGES ON moneykids.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

### 인코딩 문제
```sql
-- 데이터베이스 인코딩 확인
SHOW CREATE DATABASE moneykids;

-- UTF8MB4로 설정되어 있는지 확인
```

### 외래키 제약 조건 오류
```sql
-- 외래키 체크 비활성화 후 재생성
SET FOREIGN_KEY_CHECKS = 0;
-- SQL 스크립트 실행
SET FOREIGN_KEY_CHECKS = 1;
```

## 📋 체크리스트

- [ ] MySQL 서버 실행 중
- [ ] 적절한 권한의 사용자 계정
- [ ] UTF8MB4 인코딩 지원
- [ ] 충분한 디스크 공간
- [ ] `.env` 파일 설정 완료

## 🎯 다음 단계

1. **데이터베이스 생성** - 위 스크립트 중 하나 선택 실행
2. **Spring Boot 애플리케이션 실행** - `./gradlew bootRun`
3. **AI 뉴스 시스템 테스트** - `/api/news/generate` 엔드포인트 호출
4. **주식 시뮬레이션 확인** - 4시간마다 자동 뉴스 생성 및 가격 변동

## 🚨 주의사항

- 기존 `moneykids` 데이터베이스가 있다면 **완전히 삭제**됩니다
- 중요한 데이터가 있다면 미리 백업하세요
- 운영 환경에서는 반드시 백업 후 진행하세요

---

**Money Kids Back** - AI 뉴스 기반 주식 시뮬레이션과 청소년 경제교육 플랫폼 🚀 