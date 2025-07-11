package com.safebank.api.repository;

import com.safebank.api.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("TransactionRepository 통합 테스트")
class TransactionRepositoryTest {

    @Autowired private TestEntityManager em;

    @Autowired private TransactionRepository transactionRepository;

    @Autowired private AccountRepository accountRepository;

    @Autowired private MemberRepository memberRepository;

    private Member member1;
    private Member member2;
    private Member member3;
    private Account account1;
    private Account account2;
    private Account account3;
    private Account account4;

    @BeforeEach
    void setUp() {
        // 회원 데이터 생성
        member1 = Member.builder()
                .name("홍길동")
                .email("hong@test.com")
                .phoneNumber("010-1111-1111")
                .status(MemberStatus.ACTIVE)
                .build();
        member1 = memberRepository.saveAndFlush(member1);

        member2 = Member.builder()
                .name("김철수")
                .email("kim@test.com")
                .phoneNumber("010-2222-2222")
                .status(MemberStatus.ACTIVE)
                .build();
        member2 = memberRepository.saveAndFlush(member2);

        member3 = Member.builder()
                .name("이영희")
                .email("lee@test.com")
                .phoneNumber("010-3333-3333")
                .status(MemberStatus.ACTIVE)
                .build();
        member3 = memberRepository.saveAndFlush(member3);

        // 계좌 데이터 생성
        account1 = Account.builder()
                .accountNumber("1000000000000001")
                .member(member1)
                .balance(new BigDecimal("1000000.00"))
                .status(AccountStatus.ACTIVE)
                .build();
        account1 = accountRepository.saveAndFlush(account1);

        account2 = Account.builder()
                .accountNumber("1000000000000002")
                .member(member1)
                .balance(new BigDecimal("500000.00"))
                .status(AccountStatus.ACTIVE)
                .build();
        account2 = accountRepository.saveAndFlush(account2);

        account3 = Account.builder()
                .accountNumber("2000000000000001")
                .member(member2)
                .balance(new BigDecimal("750000.00"))
                .status(AccountStatus.ACTIVE)
                .build();
        account3 = accountRepository.saveAndFlush(account3);

        account4 = Account.builder()
                .accountNumber("3000000000000001")
                .member(member3)
                .balance(new BigDecimal("300000.00"))
                .status(AccountStatus.ACTIVE)
                .build();
        account4 = accountRepository.saveAndFlush(account4);

        // 거래 데이터 생성 (시간순으로 생성)
        createTransactionData();

        em.clear();
    }

    private void createTransactionData() {
        LocalDateTime baseTime = LocalDateTime.now().minusDays(10);

        // 1. account1 -> account3 송금 (10일 전)
        Transaction tx1 = Transaction.builder()
                .fromAccount(account1)
                .toAccount(account3)
                .amount(new BigDecimal("100000.00"))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description("송금 - 생활비")
                .build();
        transactionRepository.saveAndFlush(tx1);

        // 시간 차이 두기 위한 목적으로 sleep
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 2. account2에 입금 (9일 전)
        Transaction tx2 = Transaction.builder()
                .fromAccount(null)
                .toAccount(account2)
                .amount(new BigDecimal("200000.00"))
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.COMPLETED)
                .description("급여 입금")
                .build();
        transactionRepository.saveAndFlush(tx2);

        // 시간 차이 두기 위한 목적으로 sleep
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 3. account1에서 출금 (8일 전)
        Transaction tx3 = Transaction.builder()
                .fromAccount(account1)
                .toAccount(account1)
                .amount(new BigDecimal("50000.00"))
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.COMPLETED)
                .description("ATM 출금")
                .build();
        transactionRepository.saveAndFlush(tx3);

        // 시간 차이 두기 위한 목적으로 sleep
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 4. account3 -> account4 송금 (7일 전)
        Transaction tx4 = Transaction.builder()
                .fromAccount(account3)
                .toAccount(account4)
                .amount(new BigDecimal("75000.00"))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description("용돈 송금")
                .build();
        transactionRepository.saveAndFlush(tx4);

        // 시간 차이 두기 위한 목적으로 sleep
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 5. account2 -> account1 송금 (6일 전)
        Transaction tx5 = Transaction.builder()
                .fromAccount(account2)
                .toAccount(account1)
                .amount(new BigDecimal("150000.00"))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description("대출 상환")
                .build();
        transactionRepository.saveAndFlush(tx5);

        // 시간 차이 두기 위한 목적으로 sleep
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 6. account4에 입금 (5일 전)
        Transaction tx6 = Transaction.builder()
                .fromAccount(null)
                .toAccount(account4)
                .amount(new BigDecimal("30000.00"))
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.COMPLETED)
                .description("용돈 입금")
                .build();
        transactionRepository.saveAndFlush(tx6);

        // 시간 차이 두기 위한 목적으로 sleep
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 7. 실패한 거래 (4일 전)
        Transaction tx7 = Transaction.builder()
                .fromAccount(account1)
                .toAccount(account3)
                .amount(new BigDecimal("2000000.00"))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.FAILED)
                .description("잔액 부족으로 실패")
                .build();
        transactionRepository.saveAndFlush(tx7);
    }

    @Test
    @DisplayName("계좌번호로 거래 내역 조화 - 복잡한 시나리오")
    void findByAccountNumber_complexScenario() throws Exception {
        //given
        String accountNumber = account1.getAccountNumber();
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<Transaction> result = transactionRepository.findByAccountNumber(accountNumber, pageable);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getTotalElements()).isEqualTo(4);

        List<Transaction> transactions = result.getContent();

        // 최신순으로 정렬되었는지 확인
        assertThat(transactions.get(0).getStatus()).isEqualTo(TransactionStatus.FAILED);
        assertThat(transactions.get(1).getFromAccount().getAccountNumber()).isEqualTo(account2.getAccountNumber());
        assertThat(transactions.get(2).getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(transactions.get(3).getToAccount().getAccountNumber()).isEqualTo(account3.getAccountNumber());

        // 거래 유형 확인
        assertThat(transactions)
                .extracting(Transaction::getType)
                .containsExactly(
                        TransactionType.TRANSFER,
                        TransactionType.TRANSFER,
                        TransactionType.WITHDRAWAL,
                        TransactionType.TRANSFER
                );
    }

    @Test
    @DisplayName("계좌 ID로 거래 내역 조회 - 페이징 테스트")
    void findByAccountId_paginationTest() throws Exception {
        //given
        Long accountId = account1.getId();
        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);

        //when
        Page<Transaction> firstResult = transactionRepository.findByAccountId(accountId, firstPage);
        Page<Transaction> secondResult = transactionRepository.findByAccountId(accountId, secondPage);

        //then
        // 첫 번째 페이지
        assertThat(firstResult.getContent()).hasSize(2);
        assertThat(firstResult.getTotalElements()).isEqualTo(4);
        assertThat(firstResult.getTotalPages()).isEqualTo(2);
        assertThat(firstResult.hasNext()).isTrue();
        assertThat(firstResult.hasPrevious()).isFalse();

        // 두 번째 페이지
        assertThat(secondResult.getContent()).hasSize(2);
        assertThat(secondResult.getTotalElements()).isEqualTo(4);
        assertThat(secondResult.getTotalPages()).isEqualTo(2);
        assertThat(secondResult.hasNext()).isFalse();
        assertThat(secondResult.hasPrevious()).isTrue();

        // 중복되지 않는지 확인
        List<Long> firstPageIds = firstResult.getContent().stream()
                .map(Transaction::getId)
                .toList();
        List<Long> secondPageIds = secondResult.getContent().stream()
                .map(Transaction::getId)
                .toList();

        assertThat(firstPageIds).doesNotContainAnyElementsOf(secondPageIds);
    }

    @Test
    @DisplayName("거래 유형별 조회 검증")
    void findByAccountNumber() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 20);

        //when
        Page<Transaction> account1Transactions = transactionRepository.findByAccountNumber(account1.getAccountNumber(), pageable);
        Page<Transaction> account2Transactions = transactionRepository.findByAccountNumber(account2.getAccountNumber(), pageable);
        Page<Transaction> account4Transactions = transactionRepository.findByAccountNumber(account4.getAccountNumber(), pageable);

        //then
        // account1: TRANSFER, WITHDRAWAL
        assertThat(account1Transactions.getContent())
                .extracting(Transaction::getType)
                .contains(TransactionType.TRANSFER, TransactionType.WITHDRAWAL);

        // account2: DEPOSIT, TRANSFER
        assertThat(account2Transactions.getContent())
                .extracting(Transaction::getType)
                .contains(TransactionType.DEPOSIT, TransactionType.TRANSFER);

        // account4: TRANSFER, DEPOSIT
        assertThat(account4Transactions.getContent())
                .extracting(Transaction::getType)
                .contains(TransactionType.TRANSFER, TransactionType.DEPOSIT);
    }

    @Test
    @DisplayName("거래 상태별 조회 검증")
    void findByAccountNumber_transactionStatus() throws Exception {
        // given
        String accountNumber = account1.getAccountNumber();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Transaction> result = transactionRepository.findByAccountNumber(accountNumber, pageable);

        // then
        List<Transaction> transactions = result.getContent();

        // COMPLETED와 FAILED 상태 모두 포함
        assertThat(transactions)
                .extracting(Transaction::getStatus)
                .contains(TransactionStatus.COMPLETED, TransactionStatus.FAILED);

        // 실패한 거래 확인
        Optional<Transaction> failedTransaction = transactions.stream()
                .filter(tx -> tx.getStatus() == TransactionStatus.FAILED)
                .findFirst();

        assertThat(failedTransaction).isPresent();
        assertThat(failedTransaction.get().getAmount()).isEqualTo(new BigDecimal("2000000.00"));
        assertThat(failedTransaction.get().getDescription()).contains("잔액 부족");
    }

    @Test
    @DisplayName("JPQL 쿼리 OR 조건 테스트")
    void findByAccountNumber_orConditionTest() throws Exception {
        // given
        String accountNumber = account3.getAccountNumber();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Transaction> result = transactionRepository.findByAccountNumber(accountNumber, pageable);

        // then
        assertThat(result.getContent()).hasSize(3);  // account3 관련 거래 2건

        List<Transaction> transactions = result.getContent();

        // account3이 fromAccount인 거래
        boolean hasFromAccount = transactions.stream()
                .anyMatch(tx -> tx.getFromAccount() != null && tx.getFromAccount().getAccountNumber().equals(accountNumber));

        // account3이 toAccount인 거래
        boolean hasToAccount = transactions.stream()
                .anyMatch(tx -> tx.getToAccount().getAccountNumber().equals(accountNumber));

        assertThat(hasFromAccount).isTrue();
        assertThat(hasToAccount).isTrue();
    }

    @Test
    @DisplayName("대용량 데이터 페이징 성능 테스트")
    void findByAccountNumber_performanceTest() throws Exception {
        // given
        // 추가 거래 데이터 생성 (100개)
        for (int i = 0; i < 100; i++) {
            Transaction tx = Transaction.builder()
                    .fromAccount(account1)
                    .toAccount(account2)
                    .amount(new BigDecimal("1000.00"))
                    .type(TransactionType.TRANSFER)
                    .status(TransactionStatus.COMPLETED)
                    .description("테스트 거래 " + i)
                    .build();
            transactionRepository.save(tx);
        }
        em.flush();

        Pageable pageable = PageRequest.of(0, 20);

        // when
        long startTime = System.currentTimeMillis();
        Page<Transaction> result = transactionRepository.findByAccountNumber(account1.getAccountNumber(), pageable);
        long endTime = System.currentTimeMillis();

        // then
        assertThat(result.getContent()).hasSize(20);
        assertThat(result.getTotalElements()).isGreaterThan(100);

        // 성능 검증 (1초 이내)
        long executionTime = endTime - startTime;
        assertThat(executionTime).isLessThan(1000);

        System.out.println("쿼리 실행 시간: " + executionTime + "ms");
    }

    @Test
    @DisplayName("NULL 값 처리 테스트 - fromAccount가 null인 입금 거래")
    void findByAccountNumber_nullFromAccount() throws Exception {
        // given
        String accountNumber = account2.getAccountNumber();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Transaction> result = transactionRepository.findByAccountNumber(accountNumber, pageable);

        // then
        List<Transaction> transactions = result.getContent();


        // fromAccount가 null인 입금 거래 확인
        Optional<Transaction> depositTransaction = transactions.stream()
                .filter(tx -> tx.getType() == TransactionType.DEPOSIT)
                .findFirst();

        System.out.println(depositTransaction);

        assertThat(depositTransaction).isPresent();
        assertThat(depositTransaction.get().getFromAccount()).isNull();
        assertThat(depositTransaction.get().getToAccount().getAccountNumber()).isEqualTo(accountNumber);
        assertThat(depositTransaction.get().getDescription()).contains("급여");
    }

    @Test
    @DisplayName("정렬 순서 검증 - createdAt DESC")
    void findByAccountNumber_orderByCreatedAtDesc() throws Exception {
        // given
        String accountNumber = account1.getAccountNumber();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Transaction> result = transactionRepository.findByAccountNumber(accountNumber, pageable);

        // then
        List<Transaction> transactions = result.getContent();

        // 최신순으로 정렬되었는지 확인
        for (int i = 0; i < transactions.size()-1; i++) {
            LocalDateTime current = transactions.get(i).getCreatedAt();
            LocalDateTime next = transactions.get(i + 1).getCreatedAt();

            assertThat(current).isAfterOrEqualTo(next);  // 최신순(내림차순)으로 정렬이므로 current가 next보다 현재 기준 최신 데이터
        }
    }

    @Test
    @DisplayName("계좌별 거래 금액 합계 검증")
    void findByAccountNumber_amountSummary() throws Exception {
        // given
        String accountNumber = account1.getAccountNumber();
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Transaction> result = transactionRepository.findByAccountNumber(accountNumber, pageable);

        // then
        List<Transaction> transactions = result.getContent();

        // 출금 총액 계산
        BigDecimal totalWithdraw = transactions.stream()
                .filter(tx -> tx.getFromAccount() != null && tx.getFromAccount().getAccountNumber().equals(accountNumber))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 입금 총액 계산
        BigDecimal totalDeposit = transactions.stream()
                .filter(tx -> tx.getToAccount().getAccountNumber().equals(accountNumber) &&
                        (tx.getFromAccount() == null || !tx.getFromAccount().getAccountNumber().equals(accountNumber)))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(totalWithdraw).isGreaterThan(BigDecimal.ZERO);
        assertThat(totalDeposit).isGreaterThan(BigDecimal.ZERO);
    }
}