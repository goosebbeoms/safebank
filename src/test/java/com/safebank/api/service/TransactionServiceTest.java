package com.safebank.api.service;

import com.safebank.api.dto.request.TransferRequest;
import com.safebank.api.entity.*;
import com.safebank.api.exception.AccountNotFoundException;
import com.safebank.api.exception.InsufficientBalanceException;
import com.safebank.api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService 테스트")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    private Member testMember1;
    private Member testMember2;
    private Account fromAccount;
    private Account toAccount;
    private Transaction testTransaction;
    private TransferRequest transferRequest;

    @BeforeEach
    void setUp() {
        testMember1 = Member.builder()
                .id(1L)
                .name("홍길동")
                .email("hong@examble.com")
                .phoneNumber("010-1234-5678")
                .status(MemberStatus.ACTIVE)
                .build();

        testMember2 = Member.builder()
                .id(2L)
                .name("김철수")
                .email("kim@examble.com")
                .phoneNumber("010-9876-5432")
                .status(MemberStatus.ACTIVE)
                .build();

        fromAccount = Account.builder()
                .id(1L)
                .accountNumber("1111111111111111")
                .member(testMember1)
                .balance(new BigDecimal("100000.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        toAccount = Account.builder()
                .id(2L)
                .accountNumber("2222222222222222")
                .member(testMember2)
                .balance(new BigDecimal("50000.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        testTransaction = Transaction.builder()
                .id(1L)
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(new BigDecimal("10000.00"))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description("송금 테스트")
                .build();

        transferRequest = new TransferRequest();
        transferRequest.setFromAccountNumber("1111111111111111");
        transferRequest.setToAccountNumber("2222222222222222");
        transferRequest.setAmount(new BigDecimal("10000.00"));
        transferRequest.setDescription("송금 테스트");
    }

    @Test
    @DisplayName("계좌 이체 성공")
    void transfer_success() throws Exception {
        //given
        given(accountService.getAccountByNumber("1111111111111111")).willReturn(fromAccount);
        given(accountService.getAccountByNumber("2222222222222222")).willReturn(toAccount);
        given(transactionRepository.save(any(Transaction.class))).willReturn(testTransaction);

        //when
        Transaction result = transactionService.transfer(transferRequest);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFromAccount().getAccountNumber()).isEqualTo("1111111111111111");
        assertThat(result.getToAccount().getAccountNumber()).isEqualTo("2222222222222222");
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("10000.00"));
        assertThat(result.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);

        // 계좌 잔액 변경 확인
        assertThat(fromAccount.getBalance()).isEqualTo(new BigDecimal("90000.00"));
        assertThat(toAccount.getBalance()).isEqualTo(new BigDecimal("60000.00"));

        verify(accountService, times(1)).getAccountByNumber("1111111111111111");
        verify(accountService, times(1)).getAccountByNumber("2222222222222222");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("계좌 이체 실패 - 출금 계좌 존재하지 않음")
    void transfer_fromAccountNotFound() throws Exception {
        // given
        given(accountService.getAccountByNumber("1111111111111111"))
                .willThrow(new AccountNotFoundException("계좌를 찾을 수 없습니다. 계좌번호: 1111111111111111"));

        // when & then
        assertThatThrownBy(() -> transactionService.transfer(transferRequest))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("계좌를 찾을 수 없습니다. 계좌번호: 1111111111111111");

        verify(accountService, times(1)).getAccountByNumber("1111111111111111");
        verify(accountService, never()).getAccountByNumber("2222222222222222");
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("계좌 이체 실패 - 입금 계좌 존재하지 않음")
    void transfer_toAccountNotFound() throws Exception {
        // given
        given(accountService.getAccountByNumber("1111111111111111")).willReturn(fromAccount);
        given(accountService.getAccountByNumber("2222222222222222"))
                .willThrow(new AccountNotFoundException("계좌를 찾을 수 없습니다. 계좌번호: 2222222222222222"));

        // when & then
        assertThatThrownBy(() -> transactionService.transfer(transferRequest))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("계좌를 찾을 수 없습니다. 계좌번호: 2222222222222222");

        verify(accountService, times(1)).getAccountByNumber("1111111111111111");
        verify(accountService, times(1)).getAccountByNumber("2222222222222222");
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("계좌 이체 실패 - 잔액 부족")
    void transfer_insufficientBalance() throws Exception {
        // given
        Account poorAccount = Account.builder()
                .id(3L)
                .accountNumber("1111111111111111")
                .member(testMember1)
                .balance(new BigDecimal("5000.00")) // 이체 금액보다 적은 잔액
                .status(AccountStatus.ACTIVE)
                .build();

        given(accountService.getAccountByNumber("1111111111111111")).willReturn(poorAccount);
        given(accountService.getAccountByNumber("2222222222222222")).willReturn(toAccount);

        // when & then
        assertThatThrownBy(() -> transactionService.transfer(transferRequest))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("잔액이 부족합니다");

        verify(accountService, times(1)).getAccountByNumber("1111111111111111");
        verify(accountService, times(1)).getAccountByNumber("2222222222222222");
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("계좌 이체 실패 - 동일 계좌 이체")
    void transfer_sameAccount() throws Exception {
        // given
        transferRequest.setToAccountNumber("1111111111111111"); // 동일한 계좌번호

        given(accountService.getAccountByNumber("1111111111111111")).willReturn(fromAccount);

        // when & then
        assertThatThrownBy(() -> transactionService.transfer(transferRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("동일한 계좌로는 이체할 수 없습니다");

        verify(accountService, times(2)).getAccountByNumber("1111111111111111");
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("계좌별 거래 내역 조회 성공")
    void getAccountTransactions_success() throws Exception {
        // given
        String accountNumber = "1111111111111111";
        Pageable pageable = PageRequest.of(0, 10);

        List<Transaction> transactions = Arrays.asList(testTransaction);
        Page<Transaction> transactionPage = new PageImpl<>(transactions, pageable, 1);

        given(transactionRepository.findByAccountNumber(accountNumber, pageable)).willReturn(transactionPage);

        // when
        Page<Transaction> result = transactionService.getAccountTransactions(accountNumber, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(testTransaction.getId());
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(transactionRepository, times(1)).findByAccountNumber(accountNumber, pageable);
    }

    @Test
    @DisplayName("계좌별 거래 내역 조회 - 빈 결과")
    void getAccountTransactions_emptyResult() throws Exception {
        // given
        String accountNumber = "9999999999999999";
        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        given(transactionRepository.findByAccountNumber(accountNumber, pageable)).willReturn(emptyPage);

        // when
        Page<Transaction> result = transactionService.getAccountTransactions(accountNumber, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);

        verify(transactionRepository, times(1)).findByAccountNumber(accountNumber, pageable);
    }

    @Test
    @DisplayName("거래 내역 단건 조회 성공")
    void getTransaction_success() throws Exception {
        // given
        Long transactionId = 1L;
        given(transactionRepository.findById(transactionId)).willReturn(Optional.of(testTransaction));

        // when
        Transaction result = transactionService.getTransaction(transactionId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(transactionId);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("10000.00"));
        assertThat(result.getType()).isEqualTo(TransactionType.TRANSFER);

        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    @DisplayName("거래 내역 단건 조회 실패 - 존재하지 않는 거래")
    void getTransaction_notFound() throws Exception {
        // given
        Long nonExistentId = 999L;
        given(transactionRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transactionService.getTransaction(nonExistentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("거래 내역을 찾을 수 없습니다. ID: " + nonExistentId);

        verify(transactionRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("이체 시 Account 엔티티의 withdraw/deposit 메서드 호출 확인")
    void transfer_accountMethodsCalled() throws Exception {
        // given
        Account spyFromAccount = spy(fromAccount);
        Account spyToAccount = spy(toAccount);

        given(accountService.getAccountByNumber("1111111111111111")).willReturn(spyFromAccount);
        given(accountService.getAccountByNumber("2222222222222222")).willReturn(spyToAccount);
        given(transactionRepository.save(any(Transaction.class))).willReturn(testTransaction);

        // when
        transactionService.transfer(transferRequest);

        // then
        /**
         * hasEnoughBalance 메서드가 Account.withdraw() 메서드 내부 로직과 TransactionService.transfer() 호출 흐름에 중복 호출되고 있음
         * TransactionService에서 잔액 확인을 제거하고 Account.withdraw()에만 의존할 수 있으나,
         * 방어적 프로그래밍 관점에서 현재 구조를 유지하기로 함
         * - 서비스 레벨에서 명시적 잔액 확인 (비즈니스 로직 명확하게)
         * - 엔티티 레벨에서 추가 안전장치 (데이터 무결성 보장)
         */
        verify(spyFromAccount, times(2)).hasEnoughBalance(new BigDecimal("10000.00"));
        verify(spyFromAccount, times(1)).withdraw(new BigDecimal("10000.00"));
        verify(spyToAccount, times(1)).deposit(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("이체 시 Transaction 빌더 패턴 사용 검증")
    void transfer_usesTransactionBuilder() throws Exception {
        // given
        given(accountService.getAccountByNumber("1111111111111111")).willReturn(fromAccount);
        given(accountService.getAccountByNumber("2222222222222222")).willReturn(toAccount);
        given(transactionRepository.save(any(Transaction.class))).willReturn(testTransaction);

        // when
        Transaction result = transactionService.transfer(transferRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("이체 금액이 매우 큰 경우 처리")
    void transfer_largeAmount() throws Exception {
        // given
        BigDecimal largeAmount = new BigDecimal("99999999999.99");
        transferRequest.setAmount(largeAmount);

        // 충분한 잔액을 가진 계좌 설정
        Account richAccount = Account.builder()
                .id(1L)
                .accountNumber("1111111111111111")
                .member(testMember1)
                .balance(new BigDecimal("100000000000.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        Transaction largeTransaction = Transaction.builder()
                .id(1L)
                .fromAccount(richAccount)
                .toAccount(toAccount)
                .amount(largeAmount)
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description("송금 테스트")
                .build();

        given(accountService.getAccountByNumber("1111111111111111")).willReturn(richAccount);
        given(accountService.getAccountByNumber("2222222222222222")).willReturn(toAccount);
        given(transactionRepository.save(any(Transaction.class))).willReturn(largeTransaction);

        // when
        Transaction result = transactionService.transfer(transferRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(largeAmount);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("이체 시 설명(description) 필드 처리")
    void transfer_withDescription() throws Exception {
        // given
        String customDescription = "월세 송금";
        transferRequest.setDescription(customDescription);

        Transaction transactionWithDescription = Transaction.builder()
                .id(1L)
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(new BigDecimal("10000.00"))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description(customDescription)
                .build();

        given(accountService.getAccountByNumber("1111111111111111")).willReturn(fromAccount);
        given(accountService.getAccountByNumber("2222222222222222")).willReturn(toAccount);
        given(transactionRepository.save(any(Transaction.class))).willReturn(transactionWithDescription);

        // when
        Transaction result = transactionService.transfer(transferRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo(customDescription);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}