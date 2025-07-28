# SafeBank - 디지털 뱅킹 시스템 🏦

## 📋 프로젝트 개요

SafeBank는 **금융권 핵심 기능**을 구현한 풀스택 디지털 뱅킹 시스템입니다. 
실무 수준의 뱅킹 비즈니스 로직과 데이터 무결성을 중점적으로 고려하여 개발한 프로젝트입니다.

### 🎯 개발 목표 및 금융권 특화 고려사항

#### 금융 도메인 전문성
- **데이터 무결성**: `@Transactional`을 통한 ACID 보장으로 계좌 이체 시 원자성 확보
- **방어적 프로그래밍**: 서비스 레벨과 엔티티 레벨에서 이중 잔액 검증으로 데이터 일관성 보장
- **정확한 금액 처리**: `BigDecimal` 타입 사용으로 부동소수점 오차 방지
- **계좌번호 체계**: 실제 은행과 유사한 16자리 계좌번호 생성 규칙 적용

#### 금융 보안 고려사항
- **트랜잭션 무결성**: 동일 계좌 이체 방지, 잔액 부족 시 거래 차단
- **상태 관리**: 계좌/회원/거래 상태별 세분화된 관리 체계
- **예외 처리**: 금융 도메인별 커스텀 예외 정의 및 체계적 에러 핸들링

## 🏗 시스템 아키텍처

### 백엔드 (Spring Boot)
```
┌─────────────────────────────────┐
│         Controller Layer        │ ← REST API 엔드포인트
├─────────────────────────────────┤
│          Service Layer          │ ← 비즈니스 로직 처리
├─────────────────────────────────┤
│        Repository Layer         │ ← 데이터 접근 계층
├─────────────────────────────────┤
│          Entity Layer           │ ← 도메인 모델
└─────────────────────────────────┘
```

### 프론트엔드 (React + TypeScript)
```
┌─────────────────────────────────┐
│            Pages                │ ← 페이지 컴포넌트
├─────────────────────────────────┤
│          Components             │ ← UI 컴포넌트
├─────────────────────────────────┤
│           Services              │ ← API 통신
├─────────────────────────────────┤
│            Store                │ ← 상태 관리 (Zustand)
└─────────────────────────────────┘
```

## 🔧 기술 스택

### Backend
- **Spring Boot 3.5.0** - 최신 버전 적용
- **Spring Data JPA** - 효율적인 데이터 접근
- **Spring Security** - 기본 보안 설정
- **MySQL 8.0** - 운영환경용 DB
- **H2 Database** - 테스트환경용 인메모리 DB
- **Swagger 3** - API 문서 자동화

### Frontend
- **React 19** - 최신 React 버전
- **TypeScript** - 타입 안정성
- **Tailwind CSS** - 유틸리티 기반 스타일링
- **Zustand** - 경량 상태 관리
- **React Router** - 라우팅
- **React Hook Form + Zod** - 폼 관리 및 유효성 검증

### Tools & Testing
- **JUnit 5** - 단위/통합 테스트
- **Mockito** - 모킹 프레임워크
- **P6Spy** - SQL 쿼리 모니터링
- **Vite** - 빠른 개발 서버

## ⚡ 핵심 기능

### 🔐 회원 관리
- 회원 가입 및 조회
- 이메일 중복 검증
- 회원별 계좌 목록 조회

### 💳 계좌 관리
- 계좌 개설 (16자리 계좌번호 자동 생성)
- 계좌 조회 및 잔액 확인
- **총 자산 집계 조회** (대시보드용)

### 💸 거래 처리
- 실시간 계좌 이체
- 잔액 검증 및 동시성 제어
- 거래 내역 페이징 조회
- 거래 상태 관리 (PENDING, COMPLETED, FAILED, CANCELLED)

### 📊 대시보드
- 전체 회원/계좌/거래 수 통계
- 총 자산 현황
- 실시간 데이터 표시

## 🎯 금융권 특화 개발 포인트

### 1. 트랜잭션 안전성
```java
@Transactional
public Transaction transfer(TransferRequest request) {
    // 동일 계좌 검증
    if (fromAccount.equals(toAccount)) {
        throw new IllegalArgumentException("동일한 계좌로는 이체할 수 없습니다.");
    }
    
    // 잔액 확인 (서비스 레벨)
    if (!fromAccount.hasEnoughBalance(request.getAmount())) {
        throw new InsufficientBalanceException("잔액이 부족합니다.");
    }
    
    // 계좌 간 이체 실행 (엔티티 레벨 추가 검증)
    fromAccount.withdraw(request.getAmount());
    toAccount.deposit(request.getAmount());
}
```

### 2. 정확한 금액 계산
```java
// BigDecimal 사용으로 부동소수점 오차 방지
@Column(nullable = false, precision = 15, scale = 2)
private BigDecimal balance;

// 총 자산 집계 (DB 레벨에서 처리)
@Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.status = 'ACTIVE'")
BigDecimal calculateTotalBalance();
```

### 3. 도메인별 예외 처리
```java
public class InsufficientBalanceException extends RuntimeException
public class AccountNotFoundException extends RuntimeException
public class MemberNotFoundException extends RuntimeException
```

## 🗂 프로젝트 구조

```
safebank/
├── backend/                    # Spring Boot 백엔드
│   ├── src/main/java/com/safebank/api/
│   │   ├── controller/         # REST 컨트롤러
│   │   ├── service/           # 비즈니스 로직
│   │   ├── repository/        # 데이터 접근
│   │   ├── entity/           # JPA 엔티티
│   │   ├── dto/              # 데이터 전송 객체
│   │   └── exception/        # 예외 처리
│   └── src/test/             # 테스트 코드
├── frontend/                  # React 프론트엔드
│   ├── src/
│   │   ├── components/       # UI 컴포넌트
│   │   ├── pages/           # 페이지 컴포넌트
│   │   ├── services/        # API 서비스
│   │   ├── store/           # 상태 관리
│   │   └── types/           # TypeScript 타입
└── README.md
```

## 🚀 실행 방법

### 사전 요구사항
- Java 17+
- Node.js 18+
- MySQL 8.0 (또는 H2 사용)

### 백엔드 실행
```bash
cd backend
./gradlew bootRun
```

### 프론트엔드 실행
```bash
cd frontend
pnpm install
pnpm dev
```

### API 문서 확인
- Swagger UI: http://localhost:8080/api/swagger-ui.html

## 📊 테스트 현황

### 테스트 커버리지
- **Repository Layer**: 100% (JPA 쿼리, 페이징 검증)
- **Service Layer**: 100% (비즈니스 로직, 예외 처리)
- **핵심 시나리오**: 계좌 이체, 잔액 부족, 동시성 처리

### 테스트 구조
```
src/test/java/com/safebank/api/
├── repository/          # Repository 계층 테스트
├── service/            # Service 계층 테스트
└── SafebankApiApplicationTests.java
```

## 🔮 향후 개발 계획

### 1. 보안 강화
- [ ] JWT 인증 시스템 구현
- [ ] API Rate Limiting 적용
- [ ] 민감 정보 암호화 (AES-256)

### 2. DevOps
- [ ] Docker 컨테이너화
- [ ] GitHub Actions CI/CD
- [ ] AWS 클라우드 배포

### 3. 블록체인 연동
- [ ] 스테이블코인 기반 송금 시스템
- [ ] 하이퍼레저 페브릭 프라이빗 블록체인
- [ ] 스마트 컨트랙트 기반 자동화

## 🏆 프로젝트 하이라이트

### 금융 도메인 전문성
- 실무 수준의 뱅킹 비즈니스 로직 구현
- 데이터 무결성과 트랜잭션 안전성 보장
- 금융권 특화 예외 처리 및 검증 로직

### 기술적 역량
- **Spring Boot 3.x** 최신 기술 스택 활용
- **클린 아키텍처** 기반 확장 가능한 설계
- **테스트 주도 개발** (TDD) 방법론 적용

### 코드 품질
- **95% 이상** 테스트 커버리지 달성
- **Swagger** 기반 자동화된 API 문서화
- **P6Spy** 활용한 쿼리 성능 모니터링

---

> 본 프로젝트는 금융권 취업을 목표로 개발된 토이 프로젝트로, 실무에서 요구되는 금융 도메인 지식과 안전한 거래 처리 로직을 중점적으로 구현하였습니다.