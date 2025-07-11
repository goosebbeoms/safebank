package com.safebank.api.repository;

import com.safebank.api.entity.Account;
import com.safebank.api.entity.AccountStatus;
import com.safebank.api.entity.Member;
import com.safebank.api.entity.MemberStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
class AccountRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired AccountRepository accountRepository;

    private Member testMember1;
    private Member testMember2;
    private Account testAccount1;
    private Account testAccount2;
    private Account testAccount3;

    @BeforeEach
    void setUp() {
        // 테스트용 Member 생성
        testMember1 = Member.builder()
                .name("홍길동")
                .email("hong@example.com")
                .phoneNumber("01012345678")
                .status(MemberStatus.ACTIVE)
                .build();
        testMember1 = em.persistAndFlush(testMember1);

        testMember2 = Member.builder()
                .name("김철수")
                .email("kim@example.com")
                .phoneNumber("01098765432")
                .status(MemberStatus.ACTIVE)
                .build();
        testMember2 = em.persistAndFlush(testMember2);

        // 테스트용 Account 생성
        testAccount1 = Account.builder()
                .accountNumber("1234567890")
                .member(testMember1)
                .balance(new BigDecimal("10000.00"))
                .status(AccountStatus.ACTIVE)
                .build();
        testAccount1 = em.persistAndFlush(testAccount1);

        testAccount2 = Account.builder()
                .accountNumber("0987654321")
                .member(testMember1)
                .balance(new BigDecimal("50000.00"))
                .status(AccountStatus.ACTIVE)
                .build();
        testAccount2 = em.persistAndFlush(testAccount2);

        testAccount3 = Account.builder()
                .accountNumber("1111222233")
                .member(testMember2)
                .balance(new BigDecimal("20000.00"))
                .status(AccountStatus.INACTIVE)
                .build();
        testAccount3 = em.persistAndFlush(testAccount3);

        em.clear();
    }

    @Test
    @DisplayName("계죄번호로 계좌 조회 - 성공")
    void findByAccountNumber_Success() throws Exception {
        //given
        String accountNumber = "1234567890";

        //when
        Optional<Account> result = accountRepository.findByAccountNumber(accountNumber);

        //then
        assertThat(result).isPresent();
        assertThat(result.get().getAccountNumber()).isEqualTo(accountNumber);
        assertThat(result.get().getBalance()).isEqualTo(new BigDecimal("10000.00"));
        assertThat(result.get().getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(result.get().getMember().getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("계좌번호로 계좌 조회 - 존재하지 않는 계죄")
    void findByAccountNumber_notFound() throws Exception {
        //given
        String nonExistentAccountNumber = "999999999";

        //when
        Optional<Account> result = accountRepository.findByAccountNumber(nonExistentAccountNumber);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("회원 ID로 계좌 목록 조회 - 복수 계좌")
    void findByMemberId_multipleAccounts() throws Exception {
        //given
        Long memberId = testMember1.getId();

        //when
        List<Account> accounts = accountRepository.findByMemberId(memberId);

        //then
        assertThat(accounts).hasSize(2);
        assertThat(accounts)
                .extracting(Account::getAccountNumber)
                .containsExactlyInAnyOrder("1234567890", "0987654321");
    }

    @Test
    @DisplayName("회원 ID로 계좌 목록 조회 - 단일 계좌")
    void findByMemberId_singleAccount() throws Exception {
        //given
        Long memberId = testMember2.getId();

        //when
        List<Account> accounts = accountRepository.findByMemberId(memberId);

        //then
        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getAccountNumber()).isEqualTo("1111222233");
        assertThat(accounts.get(0).getStatus()).isEqualTo(AccountStatus.INACTIVE);
    }

    @Test
    @DisplayName("회원 ID로 계좌 목록 조회 - 계좌 없음")
    void findByMemberId_noAccounts() throws Exception {
        // given
        Member memberWithoutAccount = Member.builder()
                .name("이영희")
                .email("lee@example.com")
                .phoneNumber("01055556666")
                .status(MemberStatus.ACTIVE)
                .build();
        memberWithoutAccount = em.persistAndFlush(memberWithoutAccount);
        Long memberId = memberWithoutAccount.getId();

        // when
        List<Account> accounts = accountRepository.findByMemberId(memberId);

        // then
        assertThat(accounts).isEmpty();
    }

    @Test
    @DisplayName("회원 이메일로 계좌 목록 조회 성공")
    void findByMemberEmail_success() throws Exception {
        // given
        String email = "hong@example.com";

        // when
        List<Account> accounts = accountRepository.findByMemberEmail(email);

        // then
        assertThat(accounts).hasSize(2);
        assertThat(accounts)
                .extracting(Account::getAccountNumber)
                .containsExactlyInAnyOrder("1234567890", "0987654321");

        // 모든 계좌의 소유자 이메일이 동일한지 확인
        assertThat(accounts)
                .extracting(account -> account.getMember().getEmail())
                .containsOnly(email);
    }

    @Test
    @DisplayName("회원 이메일로 계좌 목록 조회 - 존재하지 않는 이메일")
    void findByMemberEmail_notFound() throws Exception {
        // given
        String nonExistentEmail = "notfound@example.com";

        // when
        List<Account> accounts = accountRepository.findByMemberEmail(nonExistentEmail);

        // then
        assertThat(accounts).isEmpty();
    }

    @Test
    @DisplayName("Account 편의 메서드 테스트 - 입금")
    void account_deposit_success() throws Exception {
        // given
        BigDecimal initialBalance = testAccount1.getBalance();
        BigDecimal depositAmount = new BigDecimal("5000.00");

        // when
        testAccount1.deposit(depositAmount);
        Account savedAccount = accountRepository.save(testAccount1);

        // then
        assertThat(savedAccount.getBalance()).isEqualTo(initialBalance.add(depositAmount));
        assertThat(savedAccount.getBalance()).isEqualTo(new BigDecimal("15000.00"));
    }

    @Test
    @DisplayName("Account 편의 메서드 테스트 - 출금 성공")
    void account_withdraw_success() throws Exception {
        // given
        BigDecimal initialBalance = testAccount1.getBalance();
        BigDecimal withdrawAmount = new BigDecimal("3000.00");

        // when
        testAccount1.withdraw(withdrawAmount);
        Account savedAccount = accountRepository.save(testAccount1);

        // then
        assertThat(savedAccount.getBalance()).isEqualTo(initialBalance.subtract(withdrawAmount));
        assertThat(savedAccount.getBalance()).isEqualTo(new BigDecimal("7000.00"));
    }

    @Test
    @DisplayName("Account 편의 메서드 테스트 - 출금 실패 (잔액 부족)")
    void account_withdraw_insufficientBalance() throws Exception {
        // given
        BigDecimal withdrawAmount = new BigDecimal("15000.00"); // 잔액보다 큰 금액

        // when & then
        assertThat(testAccount1.hasEnoughBalance(withdrawAmount)).isFalse();

        assertThrows(IllegalArgumentException.class, () -> testAccount1.withdraw(withdrawAmount), "잔액이 부족합니다");
    }

    @Test
    @DisplayName("Account 편의 메서드 테스트 - 잔액 확인")
    void account_hasEnoughBalance_success() throws Exception {
        // given
        BigDecimal currentBalance = testAccount1.getBalance(); // 10000.00

        // when & then
        assertThat(testAccount1.hasEnoughBalance(new BigDecimal("5000.00"))).isTrue();
        assertThat(testAccount1.hasEnoughBalance(new BigDecimal("10000.00"))).isTrue();
        assertThat(testAccount1.hasEnoughBalance(new BigDecimal("10000.01"))).isFalse();
        assertThat(testAccount1.hasEnoughBalance(new BigDecimal("15000.00"))).isFalse();
    }
}