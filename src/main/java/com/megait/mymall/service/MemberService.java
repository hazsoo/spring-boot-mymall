package com.megait.mymall.service;

import com.megait.mymall.domain.Address;
import com.megait.mymall.domain.Member;
import com.megait.mymall.domain.MemberType;
import com.megait.mymall.repository.MemberRepository;
import com.megait.mymall.util.ConsoleMailSender;
import com.megait.mymall.validation.SignUpForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    private final ConsoleMailSender consoleMailSender;

    public Member processNewMember(SignUpForm signUpForm) {

        // 올바른 form인 경우 DB 저장
        Member member = Member.builder()
                .email(signUpForm.getEmail())
                .password(signUpForm.getPassword())
                .address(Address.builder()
                        .city(signUpForm.getCity())
                        .street(signUpForm.getStreet())
                        .zip(signUpForm.getZipcode())
                        .build())
                .type(MemberType.ROLE_USER)
                .joinedAt(LocalDateTime.now())
//                .emailCheckToken(UUID.randomUUID().toString())
                .build();
        member.generateEmailCheckToken();
        memberRepository.save(member); // 영속성 검증후 디비 저장

        // TODO 이메일에 인증 링크 전송

        String url = "http://127.0.0.1:8080/email-check-token?token="
                    + member.getEmailCheckToken() + "&email=" + member.getEmail();
        log.info("url : {}", url);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(member.getEmail());
        message.setFrom("admin@mymall.com");
        message.setSubject("[mymall] 회원가입 이메일 인증 링크입니다.");
        message.setText("다음 링크를 클릭해주세요. =>" + url);
        consoleMailSender.send(message);

        // 새로 추가된 회원 (Member 엔티티)를 return
        return member;
    }

    public void login(Member member) {
        // 해당 member를 authenticated 상태로 저장 => 로그인 완료 처리
    }
}
