package com.safebank.api.repository;

import com.safebank.api.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MemberRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(MemberRepositoryTest.class);
    @Autowired private TestEntityManager em;
    @Autowired private MemberRepository memberRepository;

    @Test
    @DisplayName("이메일로 회원 조회 성공")
    void findByEmail_success() throws Exception {
        //given
        Member member = getMember();

        em.persist(member);
        em.flush();

        //when
        Optional<Member> findMember = memberRepository.findByEmail("test1234@test.test");

        //then
        assertThat(findMember).isPresent();
        assertThat(findMember.get().getName()).isEqualTo("김철수");
    }

    @Test
    @DisplayName("기등록 이메일 존재 유무 조회 성공")
    void existsByEmail_success() throws Exception {
        //given
        Member member = getMember();

        em.persistAndFlush(member);

        //when
        boolean result = memberRepository.existsByEmail("honggildong_wow@test.test");

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("미등록 이메일 존재 유무 조회 성공")
    void existsByEmail_fail() throws Exception {
        //given
        Member member = getMember();
        em.persistAndFlush(member);

        //when
        boolean result = memberRepository.existsByEmail("honggildong_wow@test.test");

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("이름으로 회원 조회 성공")
    void findByName_success() throws Exception {
        //given
        Member member = getMember();
        em.persistAndFlush(member);

        //when
        List<Member> findMember = memberRepository.findByName("홍길동");

        //then
        System.out.println(findMember.toString());
        assertThat(findMember).isNotEmpty();
    }

    private static Member getMember() {
        Member member = Member.builder()
                .name("홍길동")
                .email("honggildong@test.test")
                .phoneNumber("01012341234")
                .build();
        return member;
    }
}