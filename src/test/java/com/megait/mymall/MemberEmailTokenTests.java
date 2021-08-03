package com.megait.mymall;

import com.megait.mymall.domain.Member;
import com.megait.mymall.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberEmailTokenTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    private Member newMember;

    @PostConstruct // 모든 클래스에서 사용가능. 해당 클래스가 생성된 뒤에 바로 무엇을 할 지 작성
    void createNewMember() {

        String email = "admin@test.com";
        String password = "P@ssw0rd";

        Member member = Member.builder()
                .email(email)
                .password(password)
                .build();
        member.generateEmailCheckToken();

        newMember = memberRepository.save(member);
    }

    @Test
    @DisplayName("이메일 검증 - 입력값 오류 (잘못된 토큰)")
    void check_email_token_failed() throws Exception {
        mockMvc.perform(
                get("/email-check-token")
                        .param("email", newMember.getEmail())
                        .param("token", newMember.getEmailCheckToken() + "_wrong"))
                .andExpect(status().isOk());

        assertFalse(memberRepository.findByEmail(newMember.getEmail()).get().isEmailVerified());

    }

    @Test
    @DisplayName("이메일 검증 - 입력값 정상")
    void check_email_token_success() throws Exception {
        mockMvc.perform(
                get("/email-check-token")
                        .param("email", newMember.getEmail())
                        .param("token", newMember.getEmailCheckToken()))
                .andExpect(status().isOk());

        assertTrue(memberRepository.findByEmail(newMember.getEmail()).get().isEmailVerified());
    }


}