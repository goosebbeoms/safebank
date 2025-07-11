package com.safebank.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountCreateRequest {

    @NotNull(message = "회원 ID는 필수입니다")
    private Long memberId;

    @NotNull(message = "초기 잔액은 필수입니다")
    @DecimalMin(value = "1000.0", message = "초기 잔액은 1,000원 이상이어야 합니다")
    private BigDecimal initialBalance;
}
