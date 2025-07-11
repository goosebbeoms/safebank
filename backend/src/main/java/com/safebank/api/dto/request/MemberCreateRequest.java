package com.safebank.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberCreateRequest {

    @NotBlank(message = "회원명은 필수입니다")
    private String name;

    @Email(message = "이메일 형식이 올바르지 않습니다")
    @NotBlank(message = "이메일은 필수입니다")
    private String email;

    @NotBlank(message = "전화번호는 필수입니다")
    private String phoneNumber;
}
