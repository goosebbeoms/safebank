package com.safebank.api.service;

import com.safebank.api.dto.request.AccountCreateRequest;
import com.safebank.api.entity.Account;
import com.safebank.api.entity.Member;
import com.safebank.api.exception.AccountNotFoundException;
import com.safebank.api.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final MemberService memberService;

    /**
     * 전체 계좌 조회
     */
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    /**
     * 계좌 생성
     * @param request
     * @return
     */
    @Transactional
    public Account createAccount(AccountCreateRequest request) {
        Member member = memberService.getMember(request.getMemberId());
        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .member(member)
                .balance(request.getInitialBalance())
                .build();

        Account savedAccount = accountRepository.save(account);
        log.info("새 계좌가 생성되었습니다. 계좌번호: {}, 소유자: {}", savedAccount.getAccountNumber(), savedAccount.getMember().getName());

        return savedAccount;
    }

    /**
     * 계좌 조회
     * @param id
     * @return
     */
    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("계좌를 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 계좌번호로 계좌 정보 조회
     * @param accountNumber
     * @return
     */
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("계좌를 찾을 수 없습니다. 계좌번호: " + accountNumber));
    }

    /**
     * 회원 ID로 회원의 계좌 리스트 조회
     * @param memberId
     * @return
     */
    public List<Account> getAccountsByMemberId(Long memberId) {
        // 회원 존재 여부 확인
        memberService.getMember(memberId);
        return accountRepository.findByMemberId(memberId);
    }

    /**
     * 계좌번호 난수 생성기
     * @return
     */
    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = "3333" + String.format("%012d", new Random().nextLong() % 1000000000000L);
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}
