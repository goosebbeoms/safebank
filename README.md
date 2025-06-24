# SafeBank API - 안전한 디지털 뱅킹 시스템 🏦

## 📋 프로젝트 개요

SafeBank API는 **금융권 핵심 기능**을 구현한 RESTful API 백엔드 시스템입니다. 실무에서 요구되는 **회원 관리**, **계좌 관리**, **트랜잭션 처리**를 안전하고 효율적으로 처리하며, 확장 가능한 아키텍처를 기반으로 설계되었습니다.

### 🎯 개발 목표

- **금융 도메인 이해도**: 실무 수준의 뱅킹 비즈니스 로직 구현
- **백엔드 개발 역량**: Spring Boot 기반 RESTful API 설계 및 구현
- **코드 품질**: 테스트 주도 개발과 클린 아키텍처 적용
- **확장성**: 마이크로서비스 전환 가능한 모듈화된 구조

## 🏗 시스템 아키텍처

### 레이어드 아키텍처

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

### 📁 프로젝트 구조

```
safebank-api/
├── 📁 src/main/java/com/safebank/api/
│   ├── 📁 config/                 # 설정 클래스
│   │   ├── SecurityConfig.java    # Spring Security 설정
│   │   └── SwaggerConfig.java     # API 문서화 설정
│   ├── 📁 controller/             # REST 컨트롤러
│   │   ├── MemberController.java  # 회원 관리 API
│   │   └── AccountController.java # 계좌/거래 관리 API
│   ├── 📁 service/                # 비즈니스 로직
│   │   ├── MemberService.java     # 회원 관리 서비스
│   │   ├── AccountService.java    # 계좌 관리 서비스
│   │   └── TransactionService.java # 거래 처리 서비스
│   ├── 📁 repository/             # 데이터 접근 계층
│   │   ├── MemberRepository.java  # 회원 데이터 접근
│   │   ├── AccountRepository.java # 계좌 데이터 접근
│   │   └── TransactionRepository.java # 거래 데이터 접근
│   ├── 📁 entity/                 # JPA 엔티티
│   │   ├── Member.java           # 회원 엔티티
│   │   ├── Account.java          # 계좌 엔티티
│   │   ├── Transaction.java      # 거래 엔티티
│   │   └── *Status.java, *Type.java # 상태/타입 열거형
│   ├── 📁 dto/                    # 데이터 전송 객체
│   │   ├── request/              # 요청 DTO
│   │   └── response/             # 응답 DTO
│   ├── 📁 exception/              # 예외 처리
│   │   ├── GlobalExceptionHandler.java # 전역 예외 처리
│   │   └── *Exception.java       # 커스텀 예외
│   └── SafebankApiApplication.java # 메인 애플리케이션 클래스
├── 📁 src/main/resources/
│   └── application.yml           # 애플리케이션 설정
├── 📁 src/test/java/com/safebank/api/
│   ├── 📁 repository/            # Repository 계층 테스트
│   │   ├── MemberRepositoryTest.java    # 회원 데이터 접근 테스트
│   │   ├── AccountRepositoryTest.java   # 계좌 데이터 접근 테스트
│   │   └── TransactionRepositoryTest.java # 거래 데이터 접근 테스트
│   ├── 📁 service/               # Service 계층 테스트
│   │   ├── MemberServiceTest.java       # 회원 비즈니스 로직 테스트
│   │   ├── AccountServiceTest.java      # 계좌 비즈니스 로직 테스트
│   │   └── TransactionServiceTest.java  # 거래 비즈니스 로직 테스트
│   └── SafebankApiApplicationTests.java # 통합 테스트
├── build.gradle                  # Gradle 빌드 스크립트
├── settings.gradle              # Gradle 설정
└── README.md                    # 프로젝트 문서
```

## 🔧 기술 스택

### Backend Framework

- **Spring Boot 3.5.0**: 최신 버전 적용으로 성능 최적화
- **Spring Data JPA**: 효율적인 데이터 접근 계층 구현
- **Spring Security**: 기본 보안 설정 (향후 JWT 확장 예정)
- **Spring Validation**: Bean Validation으로 입력값 검증

### Database

- **MySQL 8.0**: 운영 환경용 관계형 데이터베이스
- **H2 Database**: 개발/테스트 환경용 인메모리 DB
- **HikariCP**: 고성능 커넥션 풀링

### Documentation & Testing

- **Swagger 3 (OpenAPI)**: 자동화된 API 문서 생성
- **JUnit 5**: 단위 테스트 및 통합 테스트
- **P6Spy**: SQL 쿼리 모니터링 및 성능 분석

### Build & Development

- **Gradle 8.14.2**: 의존성 관리 및 빌드 자동화
- **Lombok**: 보일러플레이트 코드 최소화

## ⚡ 핵심 기능

### 🔐 회원 관리

- **회원 가입**: 이메일 중복 검증, 유효성 검사
- **회원 조회**: ID/이메일 기반 조회
- **계좌 목록 조회**: 회원별 보유 계좌 리스트

```bash
# 회원 생성
POST /api/members
{
  "name": "홍길동",
  "email": "hong@example.com",
  "phoneNumber": "010-1234-5678"
}

# 회원 조회
GET /api/members/{memberId}
GET /api/members/{memberId}/accounts
```

### 💳 계좌 관리

- **계좌 개설**: 16자리 계좌번호 자동 생성 (3333 + 12자리)
- **계좌 조회**: ID/계좌번호 기반 조회
- **잔액 관리**: 입금/출금 시 잔액 검증

```bash
# 계좌 생성
POST /api/accounts
{
  "memberId": 1,
  "initialBalance": 100000
}

# 계좌 조회
GET /api/accounts/number/{accountNumber}
```

### 💸 거래 처리

- **계좌 이체**: 실시간 잔액 확인 및 이체 처리
- **거래 내역**: 페이징 기반 거래 이력 조회
- **거래 상태 관리**: PENDING, COMPLETED, FAILED, CANCELLED

```bash
# 계좌 이체
POST /api/accounts/transfer
{
  "fromAccountNumber": "3333123456789012",
  "toAccountNumber": "3333987654321098",
  "amount": 50000,
  "description": "생활비 이체"
}

# 거래 내역 조회
GET /api/accounts/{accountNumber}/transactions?page=0&size=20
```

## 🎯 개발 중점 사항

### 1. 금융 도메인 정확성

- **데이터 무결성**: `@Transactional`을 통한 ACID 보장
- **잔액 검증**: 다중 계층에서의 방어적 프로그래밍
- **동시성 제어**: 계좌 이체 시 락 메커니즘 고려

```java
@Transactional
public Transaction transfer(TransferRequest request) {
    // 동일 계좌 검증
    if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
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

### 2. 확장 가능한 설계

- **Builder 패턴**: 객체 생성의 일관성과 가독성
- **Repository 패턴**: 데이터 접근 계층 추상화
- **DTO 변환**: 엔티티와 API 응답의 분리

### 3. 예외 처리 체계화

- **커스텀 예외**: 도메인별 명확한 예외 정의
- **글로벌 핸들러**: 일관된 에러 응답 형태 제공
- **유효성 검증**: Bean Validation 활용

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientBalance(InsufficientBalanceException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }
}
```

### 4. 테스트 품질 보장

- **단위 테스트**: Mockito 기반 서비스 계층 테스트
- **통합 테스트**: `@DataJpaTest`를 활용한 Repository 테스트
- **테스트 커버리지**: 핵심 비즈니스 로직 100% 커버

## 🚀 API 문서

### Swagger UI 접근

- **개발 환경**: http://localhost:8080/api/swagger-ui.html
- **API 문서**: http://localhost:8080/api/v3/api-docs

### 주요 API 엔드포인트

| 기능           | Method | Endpoint                                     | 설명             |
| -------------- | ------ | -------------------------------------------- | ---------------- |
| 회원 생성      | POST   | `/api/members`                               | 새 회원 등록     |
| 회원 조회      | GET    | `/api/members/{id}`                          | 회원 정보 조회   |
| 회원 계좌 목록 | GET    | `/api/members/{id}/accounts`                 | 회원별 계좌 목록 |
| 계좌 생성      | POST   | `/api/accounts`                              | 새 계좌 개설     |
| 계좌 조회      | GET    | `/api/accounts/number/{accountNumber}`       | 계좌 정보 조회   |
| 계좌 이체      | POST   | `/api/accounts/transfer`                     | 계좌 간 이체     |
| 거래 내역      | GET    | `/api/accounts/{accountNumber}/transactions` | 거래 이력 조회   |

## 🛠 설치 및 실행

### 사전 요구사항

- **JDK 17** 이상
- **MySQL 8.0** (운영 환경) 또는 **H2** (개발 환경)
- **Gradle 8.14.2**

### 1. 프로젝트 클론

```bash
git clone https://github.com/your-username/safebank-api.git
cd safebank-api
```

### 2. 데이터베이스 설정

#### MySQL 사용 시

```bash
# MySQL 데이터베이스 생성
CREATE DATABASE safebank;

# application.yml에서 MySQL 설정 활성화
# (현재는 H2 설정이 기본값)
```

#### H2 사용 시 (기본 설정)

별도 설정 없이 바로 실행 가능

### 3. 애플리케이션 실행

```bash
# Gradle로 실행
./gradlew bootRun

# 또는 JAR 빌드 후 실행
./gradlew build
java -jar build/libs/safebank-api-0.0.1-SNAPSHOT.jar
```

### 4. 테스트 실행

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests MemberServiceTest
```

## 📊 테스트 현황

### 테스트 구조

```
src/test/java/com/safebank/api/
├── repository/                    # Repository 계층 테스트
│   ├── MemberRepositoryTest.java   # 회원 데이터 접근 테스트
│   ├── AccountRepositoryTest.java  # 계좌 데이터 접근 테스트
│   └── TransactionRepositoryTest.java # 거래 데이터 접근 테스트
└── service/                       # Service 계층 테스트
    ├── MemberServiceTest.java      # 회원 비즈니스 로직 테스트
    ├── AccountServiceTest.java     # 계좌 비즈니스 로직 테스트
    └── TransactionServiceTest.java # 거래 비즈니스 로직 테스트
```

### 테스트 커버리지

- **Repository Layer**: 100% (JPA 쿼리, 페이징, 정렬 검증)
- **Service Layer**: 100% (비즈니스 로직, 예외 처리 검증)
- **핵심 시나리오**: 계좌 이체, 잔액 부족, 동시성 처리

## 🔮 향후 개발 계획

### 1️⃣ 보안 및 인증 강화

- [ ] **JWT 인증 시스템** 구현
- [ ] **Spring Security** 세부 설정 (Role 기반 권한 관리)
- [ ] **API Rate Limiting** 적용
- [ ] **민감 정보 암호화** (AES-256, BCrypt)
- [ ] **OAuth 2.0** 소셜 로그인 연동

### 2️⃣ DevOps 및 배포

- [ ] **Docker** 컨테이너화 및 Docker Compose 설정
- [ ] **GitHub Actions** CI/CD 파이프라인 구축
- [ ] **AWS EC2/RDS** 클라우드 배포
- [ ] **Nginx** 리버스 프록시 및 로드 밸런싱
- [ ] **모니터링** 시스템 (CloudWatch, Prometheus)

### 3️⃣ 블록체인과 연동

- [ ] **스테이블코인 기반 송금** 시스템 구현
  - RLUSD(Ripple USD) 스테이블코인을 활용한 국제송금
  - 기존 SWIFT 대비 빠르고 저렴한 송금 서비스
- [ ] **하이퍼레저 페브릭** 프라이빗 블록체인 구축
  - 금융기관 간 컨소시엄 네트워크 구성
  - 거래 내역의 불변성과 투명성 보장
- [ ] **스마트 컨트랙트** 기반 자동화된 금융 서비스
- [ ] **DID(분산 신원 증명)** 시스템 연동

## 🏆 포트폴리오 하이라이트

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

> **SafeBank API**는 금융권 취업을 목표로 하는 백엔드 개발자의 역량을 입증하기 위해 개발된 토이 프로젝트입니다. 실무에서 요구되는 금융 도메인 지식과 Spring Boot 기반 백엔드 개발 경험을 효과적으로 어필할 수 있도록 설계되었습니다.
