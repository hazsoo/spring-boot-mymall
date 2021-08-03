package com.megait.mymall;

import com.megait.mymall.repository.MemberRepository;
import com.megait.mymall.util.ConsoleMailSender;
import com.megait.mymall.validation.SignUpForm;
import com.megait.mymall.validation.SignUpFormValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.config.annotation.web.configurers.UrlAuthorizationConfigurer;
import org.springframework.test.web.servlet.MockMvc;


import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc // MockMvc 빈에 대해 자동 설정 좀 해줘
public class MemberSignUpTests { // 전체 실행시 적힌 테스트 순서대로 진행안함

    @Autowired
    private MockMvc mockMvc; // 나 대신 요청을 날려줄 가짜 mvc 객체

    @Autowired
    private MemberRepository memberRepository;

    @MockBean // then() 안에 사용될 bean 은 @MockBean 으로 선언되어야 한다
    private ConsoleMailSender mailSender;

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("대문 페이지 GET 요청")
    void index() throws Exception{ // 대문페이지가 응답이 잘 오는지
        mockMvc.perform(get("/")) // get 방식으로 "/" URL 요청을 날린다              // import post 한다음에 get 으로 수정하면 간편
                .andDo(print()) // 그 다음 응답 결과를 출력해라                                 // import static method
                .andExpect(status().isOk()) // 그 다음 응답코드가 200 (OK) 인지 확인해라
                .andExpect(view().name("index")); // 그 다음 컨트롤러가 return 한 뷰의 이름을 확인해라
    }

    @Test
    @DisplayName("회원 가입 화면 보이는지 테스트")
    void get_sign_up() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/signup"))
                .andExpect(model().attributeExists("signUpForm"));
                // signUpForm 이라는 이름의 애트리뷰트가 존재하는 지 확인
    }

    @Test
    @DisplayName("회원가입 유효성 확인 - 성공")
    void post_sign_up_valid() throws Exception {
        String email = "valid_email@test.com";
        String password = "P@ssw0rd";
        String agree = "true";

        mockMvc.perform(post("/signup")
                    .param("email", email)
                    .param("password", password)
                    .param("agreeTermsOfService", agree)
                    .with(csrf()) // thymeleaf 는 post 방식의 form에 csrf를 넣음
            ).andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        // DB에 새 회원이 정상적으로 저장되었는지 확인
        assertThat(memberRepository.findByEmail(email).isPresent()).isTrue();

        // 메일이 잘 날아갔는지 (send(SimpleMailMessage)가 실행되었니?)
        then(mailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("회원가입 유효성 확인 - 실패 (이메일 형식 오류)")
    void post_sign_up_invalid_email() throws Exception {
        // GIVEN
        String email = "invalid_email";
        String password = "P@ssw0rd";
        String agree = "true";

        SignUpForm dto = new SignUpForm();
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setAgreeTermsOfService(agree);

        // THEN
        assertThat(validator.validate(dto).isEmpty()).isFalse();
        // 유효하지 않은 잘못된 데이터를 검사했을 때 validator.validate()의 결과로 Set 이 리턴됨
        // 그 Set 안에는 에러 메시지 (ConstraintViolation 객체들) 이 들어있다
        // Set 이 isEmpty() ? ~> 에러가 없다. 정상적인 Dto 다
    }

}
