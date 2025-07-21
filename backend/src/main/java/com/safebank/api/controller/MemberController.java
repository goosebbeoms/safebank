package com.safebank.api.controller;

import com.safebank.api.dto.request.MemberCreateRequest;
import com.safebank.api.dto.response.AccountResponse;
import com.safebank.api.dto.response.ApiResponse;
import com.safebank.api.dto.response.MemberResponse;
import com.safebank.api.entity.Account;
import com.safebank.api.entity.Member;
import com.safebank.api.service.AccountService;
import com.safebank.api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "회원 관리", description = "회원 생성, 조회 관련 API")
public class MemberController {

    private final MemberService memberService;
    private final AccountService accountService;

    @GetMapping
    @Operation(summary = "전체 회원 조회", description = "생성된 회원 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getMembers() {
        List<Member> members = memberService.getMembers();
        List<MemberResponse> collect = members.stream().map(m -> MemberResponse.from(m)).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(collect));
    }

    @GetMapping("/count")
    @Operation(summary = "전체 멤버수 조회", description = "가입된 멤버의 수를 조회합니다.")
    public ResponseEntity<ApiResponse<Integer>> getMemberCount() {
        Integer memberCount = memberService.getMemberCount();

        return ResponseEntity.ok(ApiResponse.success(memberCount));
    }

    @PostMapping
    @Operation(summary = "회원 생성", description = "새로운 회원을 생성합니다")
    public ResponseEntity<ApiResponse<MemberResponse>> createMember(@Valid @RequestBody MemberCreateRequest request) {
        Member member = memberService.createMember(request);
        MemberResponse response = MemberResponse.from(member);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원이 성공적으로 생성되었습니다", response));
    }

    @GetMapping("/{memberId}")
    @Operation(summary = "회원 조회", description = "회원 ID로 회원 정보를 조회합니다")
    public ResponseEntity<ApiResponse<MemberResponse>> getMember(@PathVariable Long memberId) {
        Member member = memberService.getMember(memberId);
        MemberResponse response = MemberResponse.from(member);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{memberId}/accounts")
    @Operation(summary = "회원별 계좌 목록", description = "특정 회원의 모든 계좌를 조회합니다")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getMemberAccounts(@Parameter(description = "회원 ID", required = true) @PathVariable Long memberId) {
        List<Account> accounts = accountService.getAccountsByMemberId(memberId);
        List<AccountResponse> response = accounts.stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
