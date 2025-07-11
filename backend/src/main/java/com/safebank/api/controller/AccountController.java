package com.safebank.api.controller;

import com.safebank.api.dto.request.AccountCreateRequest;
import com.safebank.api.dto.request.TransferRequest;
import com.safebank.api.dto.response.AccountResponse;
import com.safebank.api.dto.response.ApiResponse;
import com.safebank.api.dto.response.TransactionResponse;
import com.safebank.api.entity.Account;
import com.safebank.api.entity.Transaction;
import com.safebank.api.service.AccountService;
import com.safebank.api.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "계좌 관리", description = "계좌 생성, 조회 및 거래 관련 API")
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "계좌 생성", description = "새로운 계좌를 생성합니다")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@Valid @RequestBody AccountCreateRequest request) {
        Account account = accountService.createAccount(request);
        AccountResponse response = AccountResponse.from(account);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("계좌가 성공적으로 생성되었습니다", response));
    }

    @GetMapping("/number/{accountNumber}")
    @Operation(summary = "계좌번호로 조회", description = "계좌번호로 계좌 정보를 조회합니다")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountByNumber(@Parameter(description = "계좌번호", required = true) @PathVariable String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);
        AccountResponse response = AccountResponse.from(account);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/transfer")
    @Operation(summary = "계좌 이체", description = "계좌 간 이체를 실행합니다")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(@Valid @RequestBody TransferRequest request) {
        Transaction transaction = transactionService.transfer(request);
        TransactionResponse response = TransactionResponse.from(transaction);

        return ResponseEntity.ok(ApiResponse.success("이체가 성공적으로 완료되었습니다", response));
    }

    @GetMapping("/{accountNumber}/transactions")
    @Operation(summary = "거래 내역 조회", description = "특정 계좌의 거래 내역을 조회합니다")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactions(
            @Parameter(description = "계좌번호", required = true) @PathVariable String accountNumber,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getAccountTransactions(accountNumber, pageable);
        Page<TransactionResponse> response = transactions.map(TransactionResponse::from);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}