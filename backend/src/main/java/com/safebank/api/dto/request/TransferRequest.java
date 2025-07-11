package com.safebank.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotBlank(message = "출금 계좌번호는 필수입니다")
    private String fromAccountNumber;

    @NotBlank(message = "입급 계좌번호는 필수입니다")
    private String toAccountNumber;

    @NotNull(message = "이체 금액은 필수입니다")
    @DecimalMin(value = "1.0", message = "이체 금액은 1원 이상이어야 합니다")
    private BigDecimal amount;
    private String description;
}
