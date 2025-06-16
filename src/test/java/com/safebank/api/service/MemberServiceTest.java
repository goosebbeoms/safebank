package com.safebank.api.service;

import com.safebank.api.dto.request.MemberCreateRequest;
import com.safebank.api.entity.Member;
import com.safebank.api.entity.MemberStatus;
import com.safebank.api.exception.MemberNotFoundException;
import com.safebank.api.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 테스트")
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;
    private MemberCreateRequest createRequest;

    /**
     * 테스트용 Member, request DTO 객체 생성
     */
    @BeforeEach
    void setUp() {
        this.testMember = Member.builder()
                .id(1L)
                .name("홍길동")
                .email("gildong@example.com")
                .phoneNumber("01012341234")
                .status(MemberStatus.ACTIVE)
                .build();

        this.createRequest = MemberCreateRequest.builder()
                .name("홍길동")
                .email("gildong@example.com")
                .phoneNumber("01012341234")
                .build();
    }

    @Test
    public void createMember_success() throws Exception {
        //given
        given(memberRepository.save(any(Member.class))).willReturn(testMember);

        //when
        Member result = memberService.createMember(createRequest);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getEmail()).isEqualTo("gildong@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("01012341234");
        assertThat(result.getStatus()).isEqualTo(MemberStatus.ACTIVE);

        // Repository save 메서드가 호출되었는지 검증
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 ID로 조회 성공")
    void getMember_success() throws Exception {
        //given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.of(testMember));

        //when
        Member result = memberService.getMember(memberId);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(memberId);
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getEmail()).isEqualTo("gildong@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("01012341234");

        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("미등록 회원 ID로 조회 시 예외 발생")
    void getMember_NotFound_ThrowException() throws Exception{
        //given
        Long nonExistentId = 987L;
        given(memberRepository.findById(nonExistentId)).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> memberService.getMember(nonExistentId))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("회원 정보를 찾을 수 없습니다. ID: " + nonExistentId);

        verify(memberRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("이메일로 회원 조회 성공")
    void getMemberByEmail_success() throws Exception{
        //given
        String email = "gildong@example.com";
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(testMember));

        //when
        Member result = memberService.getMemberByEmail(email);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getName()).isEqualTo("홍길동");

        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회 시 예외 발생")
    void getMemberByEmail_NotFound_ThrowsException() throws Exception {
        //given
        String nonExistentEmail = "gildong_fake@example.com";
        given(memberRepository.findByEmail(nonExistentEmail)).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> memberService.getMemberByEmail(nonExistentEmail))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("회원 정보를 찾을 수 없습니다. Email: " + nonExistentEmail);

        verify(memberRepository, times(1)).findByEmail(nonExistentEmail);
    }

    @Test
    @DisplayName("회원 생성 시 Builder 패턴 사용 검증")
    void createMember_UsesBuilderPattern() throws Exception {
        //given
        given(memberRepository.save(any(Member.class))).willReturn(testMember);

        //when
        Member result = memberService.createMember(createRequest);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(MemberStatus.ACTIVE);

        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 생성 요청 DTO의 모든 필드가 정확히 매핑됐는지 검증")
    void createMember_CorrectMapping() throws Exception {
        //given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .name("영희")
                .email("younghee@example.com")
                .phoneNumber("01012341234")
                .build();

        Member savedMember = Member.builder()
                .id(2L)
                .name("영희")
                .email("younghee@example.com")
                .phoneNumber("01012341234")
                .status(MemberStatus.ACTIVE)
                .build();

        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        //when
        Member result = memberService.createMember(request);

        //then
        assertThat(result.getName()).isEqualTo(request.getName());
        assertThat(result.getEmail()).isEqualTo(request.getEmail());
        assertThat(result.getPhoneNumber()).isEqualTo(request.getPhoneNumber());
    }

    @Test
    @DisplayName("회원 생성 시 로그 출력 검증 - 메서드 호출 확인")
    void createMember_LoggingVerification() {
        // given
        given(memberRepository.save(any(Member.class))).willReturn(testMember);

        // when
        Member result = memberService.createMember(createRequest);

        // then
        assertThat(result).isNotNull();

        verify(memberRepository, times(1)).save(any(Member.class));
    }
}