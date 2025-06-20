package com.safebank.api.service;

import com.safebank.api.dto.request.TransferRequest;
import com.safebank.api.entity.Account;
import com.safebank.api.entity.Transaction;
import com.safebank.api.entity.TransactionStatus;
import com.safebank.api.entity.TransactionType;
import com.safebank.api.exception.InsufficientBalanceException;
import com.safebank.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Transactional
    public Transaction transfer(TransferRequest request) {
        Account fromAccount = accountService.getAccountByNumber(request.getFromAccountNumber());
        Account toAccount = accountService.getAccountByNumber(request.getToAccountNumber());

        // 잔액 확인
        if (!fromAccount.hasEnoughBalance(request.getAmount())) {
            throw new InsufficientBalanceException("잔액이 부족합니다. 현재 잔악 " + fromAccount.getBalance());
        }

        // 계좌 간 이체 실행
        fromAccount.withdraw(request.getAmount());
        toAccount.deposit(request.getAmount());

        // 거래 내역 저장
        Transaction transaction = Transaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description(request.getDescription())
                .build();

        Transaction savedTransacition = transactionRepository.save(transaction);

        log.info("이체가 완료되었습니다. 거래 ID: {}, 금액: {}, {} -> {}", savedTransacition.getId(), request.getAmount(), request.getFromAccountNumber(), request.getToAccountNumber());

        return savedTransacition;
    }

    public Page<Transaction> getAccountTransactions(String accountNumber, Pageable pageable) {
        return transactionRepository.findByAccountNumber(accountNumber, pageable);
    }

    public Transaction getTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("거래 내역을 찾을 수 없습니다. ID: " + id));
    }
}
