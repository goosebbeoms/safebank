package com.safebank.api.dto.response;

import com.safebank.api.entity.Transaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {

    private Long id;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private String type;
    private String status;
    private String description;
    private LocalDateTime createdAt;

    public static TransactionResponse from (Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .fromAccountNumber((transaction.getFromAccount() != null) ? transaction.getFromAccount().getAccountNumber() : null)
                .toAccountNumber(transaction.getToAccount().getAccountNumber())
                .amount(transaction.getAmount())
                .type(transaction.getType().name())
                .status(transaction.getStatus().name())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
