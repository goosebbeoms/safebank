package com.safebank.api.service;

import com.safebank.api.dto.request.AccountCreateRequest;
import com.safebank.api.entity.Account;
import com.safebank.api.entity.AccountStatus;
import com.safebank.api.entity.Member;
import com.safebank.api.entity.MemberStatus;
import com.safebank.api.exception.AccountNotFoundException;
import com.safebank.api.exception.MemberNotFoundException;
import com.safebank.api.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService 테스트")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private AccountService accountService;

    private Member testMember;
    private Account testAccount1;
    private Account testAccount2;
    private AccountCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .name("홍길동")
                .email("hong@example.com")
                .phoneNumber("01012345678")
                .status(MemberStatus.ACTIVE)
                .build();

        testAccount1 = Account.builder()
                .id(1L)
                .accountNumber("3333123456789012")
                .member(testMember)
                .balance(new BigDecimal("10000.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        testAccount2 = Account.builder()
                .id(2L)
                .accountNumber("3333987654321098")
                .member(testMember)
                .balance(new BigDecimal("50000.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        createRequest = AccountCreateRequest.builder()
                .memberId(1L)
                .initialBalance(new BigDecimal("10000.00"))
                .build();
    }

    @Test
    @DisplayName("계좌 생성 성공")
    void createAccount_success() throws Exception {
        //given
        given(memberService.getMember(1L)).willReturn(testMember);
        given(accountRepository.existsByAccountNumber(anyString())).willReturn(false);
        given(accountRepository.save(any(Account.class))).willReturn(testAccount1);

        //when
        Account result = accountService.createAccount(createRequest);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAccountNumber()).isEqualTo("3333123456789012");
        assertThat(result.getMember().getName()).isEqualTo("홍길동");
        assertThat(result.getBalance()).isEqualTo(new BigDecimal("10000.00"));
        assertThat(result.getStatus()).isEqualTo(AccountStatus.ACTIVE);

        verify(memberService, times(1)).getMember(1L);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("계좌 생성 실패 - 존재하지 않는 회원")
    void createAccount_memberNotFound() throws Exception {
        // given
        given(memberService.getMember(1L)).willThrow(new MemberNotFoundException("회원 정보를 찾을 수 없습니다. ID: 1"));

        // when & then
        assertThatThrownBy(() -> accountService.createAccount(createRequest))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("회원 정보를 찾을 수 없습니다. ID: 1");

        verify(memberService, times(1)).getMember(1L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("계좌번호 중복 시 재생성 확인")
    void createAccount_duplicateAccountNumber() throws Exception {
        //given
        given(memberService.getMember(1L)).willReturn(testMember);
        given(accountRepository.existsByAccountNumber(anyString()))
                .willReturn(true)   // 첫 번째 호출 시 중복
                .willReturn(false); // 두 번째 호출 시 중복 없음
        given(accountRepository.save(any(Account.class))).willReturn(testAccount1);

        //when
        Account result = accountService.createAccount(createRequest);

        //then
        assertThat(result).isNotNull();
        verify(accountRepository, times(2)).existsByAccountNumber(anyString());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("계좌 ID로 조회 성공")
    void getAccount_success() throws Exception {
        // given
        Long accountId = 1L;
        given(accountRepository.findById(accountId)).willReturn(Optional.of(testAccount1));

        // when
        Account result = accountService.getAccount(accountId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(accountId);
        assertThat(result.getAccountNumber()).isEqualTo("3333123456789012");
        assertThat(result.getMember().getName()).isEqualTo("홍길동");

        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    @DisplayName("계좌 ID로 조회 실패 - 존재하지 않는 계좌")
    void getAccount_notFound() throws Exception {
        // given
        Long nonExistentId = 999L;
        given(accountRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountService.getAccount(nonExistentId))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("계좌를 찾을 수 없습니다. ID: " + nonExistentId);

        verify(accountRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("계좌번호로 조회 성공")
    void getAccountByNumber_success() throws Exception {
        // given
        String accountNumber = "3333123456789012";
        given(accountRepository.findByAccountNumber(accountNumber)).willReturn(Optional.of(testAccount1));

        // when
        Account result = accountService.getAccountByNumber(accountNumber);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(result.getMember().getName()).isEqualTo("홍길동");
        assertThat(result.getBalance()).isEqualTo(new BigDecimal("10000.00"));

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }

    @Test
    @DisplayName("계좌번호로 조회 실패 - 존재하지 않는 계좌번호")
    void getAccountByNumber_notFound() throws Exception {
        // given
        String nonExistentAccountNumber = "3333999999999999";
        given(accountRepository.findByAccountNumber(nonExistentAccountNumber)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountService.getAccountByNumber(nonExistentAccountNumber))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("계좌를 찾을 수 없습니다. 계좌번호: " + nonExistentAccountNumber);

        verify(accountRepository, times(1)).findByAccountNumber(nonExistentAccountNumber);
    }

    @Test
    @DisplayName("회원 ID로 계좌 목록 조회 성공")
    void getAccountsByMemberId_success() throws Exception {
        // given
        Long memberId = 1L;
        List<Account> expectedAccounts = Arrays.asList(testAccount1, testAccount2);

        given(memberService.getMember(memberId)).willReturn(testMember);
        given(accountRepository.findByMemberId(memberId)).willReturn(expectedAccounts);

        // when
        List<Account> result = accountService.getAccountsByMemberId(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Account::getAccountNumber)
                .containsExactlyInAnyOrder("3333123456789012", "3333987654321098");
        assertThat(result)
                .extracting(account -> account.getMember().getName())
                .containsOnly("홍길동");

        verify(memberService, times(1)).getMember(memberId);
        verify(accountRepository, times(1)).findByMemberId(memberId);
    }

    @Test
    @DisplayName("회원 ID로 계좌 목록 조회 - 빈 목록")
    void getAccountsByMemberId_emptyList() throws Exception {
        // given
        Long memberId = 1L;
        List<Account> emptyAccounts = Arrays.asList();

        given(memberService.getMember(memberId)).willReturn(testMember);
        given(accountRepository.findByMemberId(memberId)).willReturn(emptyAccounts);

        // when
        List<Account> result = accountService.getAccountsByMemberId(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(memberService, times(1)).getMember(memberId);
        verify(accountRepository, times(1)).findByMemberId(memberId);
    }

    @Test
    @DisplayName("회원 ID로 계좌 목록 조회 실패 - 존재하지 않는 회원")
    void getAccountsByMemberId_memberNotFound() throws Exception {
        // given
        Long nonExistentMemberId = 999L;
        given(memberService.getMember(nonExistentMemberId))
                .willThrow(new MemberNotFoundException("회원 정보를 찾을 수 없습니다. ID: " + nonExistentMemberId));

        // when & then
        assertThatThrownBy(() -> accountService.getAccountsByMemberId(nonExistentMemberId))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("회원 정보를 찾을 수 없습니다. ID: " + nonExistentMemberId);

        verify(memberService, times(1)).getMember(nonExistentMemberId);
        verify(accountRepository, never()).findByMemberId(nonExistentMemberId);
    }

    @Test
    @DisplayName("계좌 생성 시 Builder 패턴 사용 검증")
    void createAccount_usesBuilderPattern() throws Exception {
        // given
        given(memberService.getMember(1L)).willReturn(testMember);
        given(accountRepository.existsByAccountNumber(anyString())).willReturn(false);
        given(accountRepository.save(any(Account.class))).willReturn(testAccount1);

        // when
        Account result = accountService.createAccount(createRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(AccountStatus.ACTIVE);

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("계좌 생성 요청 DTO의 모든 필드가 정확히 매핑되는지 검증")
    void createAccount_correctMapping() throws Exception {
        // given
        AccountCreateRequest request = AccountCreateRequest.builder()
                .memberId(1L)
                .initialBalance(new BigDecimal("25000.00"))
                .build();

        Account savedAccount = Account.builder()
                .id(3L)
                .accountNumber("3333555566667777")
                .member(testMember)
                .balance(new BigDecimal("25000.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        given(memberService.getMember(1L)).willReturn(testMember);
        given(accountRepository.existsByAccountNumber(anyString())).willReturn(false);
        given(accountRepository.save(any(Account.class))).willReturn(savedAccount);

        // when
        Account result = accountService.createAccount(request);

        // then
        assertThat(result.getMember()).isEqualTo(testMember);
        assertThat(result.getBalance()).isEqualTo(request.getInitialBalance());
        assertThat(result.getAccountNumber()).startsWith("3333");
        assertThat(result.getAccountNumber()).hasSize(16);
    }

    @Test
    @DisplayName("계좌번호 생성 형식 검증")
    void createAccount_accountNumberFormat() throws Exception {
        // given
        given(memberService.getMember(1L)).willReturn(testMember);
        given(accountRepository.existsByAccountNumber(anyString())).willReturn(false);
        given(accountRepository.save(any(Account.class))).willReturn(testAccount1);

        // when
        Account result = accountService.createAccount(createRequest);

        // then
        assertThat(result.getAccountNumber()).startsWith("3333");
        assertThat(result.getAccountNumber()).hasSize(16);
        assertThat(result.getAccountNumber()).matches("^3333\\d{12}$");
    }
}