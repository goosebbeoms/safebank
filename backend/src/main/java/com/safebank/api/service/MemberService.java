package com.safebank.api.service;

import com.safebank.api.dto.request.MemberCreateRequest;
import com.safebank.api.entity.Member;
import com.safebank.api.exception.MemberNotFoundException;
import com.safebank.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 전체 회원 조회
     */
    public List<Member> getMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원가입
     * @param request
     * @return 생성한 회원 인스턴스
     */
    @Transactional
    public Member createMember(MemberCreateRequest request) {
        Member member = Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();

        Member savedMember = memberRepository.save(member);
        log.info("새 회원이 등록되었습니다. ID: {}, 이름: {}", savedMember.getId(), savedMember.getName());

        return savedMember;
    }

    /**
     * 회원 찾기(by ID)
     * @param id
     * @return Member
     */
    public Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 회원 찾기(by email)
     * @param email
     * @return Member
     */
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다. Email: " + email));
    }

    /**
     * 회원 수 반환
     */
    public int getMemberCount() {
        List<Member> members = this.getMembers();
        return members.size();
    }
}
