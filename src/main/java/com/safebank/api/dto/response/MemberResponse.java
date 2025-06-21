package com.safebank.api.dto.response;

import com.safebank.api.entity.Member;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MemberResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String status;
    private LocalDateTime createdAt;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .status(member.getStatus().name())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
