package com.safebank.api.repository;

import com.safebank.api.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired private TestEntityManager em;
    @Autowired private MemberRepository memberRepository;

    @Test
    @DisplayName("이메일로 회원 조회 성공")
    void findByEmail_success() throws Exception {
        //given
        Member member = Member.builder()
                .name("김철수")
                .email("test1234@test.test")
                .phoneNumber("01012341234")
                .build();

        em.persist(member);
        em.flush();

        //when
        Optional<Member> findMember = memberRepository.findByEmail("test1234@test.test");

        //then
        assertThat(findMember).isPresent();
        assertThat(findMember.get().getName()).isEqualTo("김철수");
    }
}