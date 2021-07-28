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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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

        // JPA Repository로부터 return 된 Entity 객체는 영속 상태
        // member는 영속객체 아니고 일반 객체라서 디비에 안들어감
       Member newMember = memberRepository.save(member);

        // 회원 인증 이메일 전송
        sendEmail(newMember);

        // 새로 추가된 회원 (Member 엔티티)를 return
        return newMember;
    }

    private void sendEmail(Member member) {
        member.generateEmailCheckToken();
        String url = "http://127.0.0.1:8080/email-check-token?token="
                    + member.getEmailCheckToken() + "&email=" + member.getEmail();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(member.getEmail());
        message.setFrom("admin@mymall.com");
        message.setSubject("[mymall] 회원가입 이메일 인증 링크입니다.");
        message.setText("다음 링크를 클릭해주세요. =>" + url);
        consoleMailSender.send(message);
    }

    public void login(Member member) {

        Collection<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(member.getType().name()));

        // "ROLE_USER" , "ROLE_ADMIN" , "ROLE_WRITER"
        // authorities : { new SGA("ROLE_USER"), new SGA("ROLE_ADMIN"), new SGA("ROLE_WRITER") }

        // Username(=principal) 과 Password(=credencial) 를 가지고
        // 스프링 시큐리티에게 인증을 요청할 때 사용하는 token
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        /*username*/ member.getEmail(),
                        /*password*/ member.getPassword(),
                        /*authorities*/ authorities);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token); // token으로 하나의 principal 만듬
    }
}
