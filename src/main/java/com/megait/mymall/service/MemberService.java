package com.megait.mymall.service;

import com.megait.mymall.domain.Address;
import com.megait.mymall.domain.Item;
import com.megait.mymall.domain.Member;
import com.megait.mymall.domain.MemberType;
import com.megait.mymall.repository.MemberRepository;
import com.megait.mymall.util.ConsoleMailSender;
import com.megait.mymall.validation.SignUpForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {
    // UserDetailsService : 로그인, 회원가입 등의 회원을 다루는 서비스에 구현하는 인터페이스
    private final MemberRepository memberRepository;

    private final ConsoleMailSender consoleMailSender;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Profile("local")
    public void createNewMember(){

        Member member = Member.builder()
                .email("admin@test.com")
                .password(passwordEncoder.encode("1234!qwer"))
                .type(MemberType.ROLE_ADMIN)
                .joinedAt(LocalDateTime.now())
                .build();

        Member newMember = memberRepository.save(member);
        emailService.sendEmail(newMember);
    }


    public Member processNewMember(SignUpForm signUpForm) {

        // 올바른 form인 경우 DB 저장
        Member member = Member.builder()
                .email(signUpForm.getEmail())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .address(Address.builder()
                        .city(signUpForm.getCity())
                        .street(signUpForm.getStreet())
                        .zip(signUpForm.getZipcode())
                        .build())
                .type(MemberType.ROLE_USER)
                .joinedAt(LocalDateTime.now())
//                .emailCheckToken(UUID.randomUUID().toString())
                .build();

        // JPA Respository로부터 리턴된 Entity 객체는 영속 상태임
        Member newMember = memberRepository.save(member);

        // 회원 인증 이메일 전송
        emailService.sendEmail(newMember);

        // 새로 추가된 회원 (Member 엔티티)를 return
        return newMember;
    }

    public void login(Member member) {
        MemberUser memberUser = new MemberUser(member);

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        memberUser,
                        memberUser.getMember().getPassword(),
                        memberUser.getAuthorities()
                );

        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(token);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 매개변수 username : 사용자 로그인을 시도했을 시, 그 id 가 들어옴
        // loadUserByUsername(유저네임) : 유저네임을 가지고 유저 정보를 조회할 때 호출될 메서드
        // '어떻게 유저 정보를 가지고 올 지'를 작성하면 됨.
        // 우리는? Member DB에서 유저정보를 꺼내야하므로.. MemberRepository 가 사용됨.
        // 주의! 없는 유저의 경우 반드시 UsernameNotFoundException 예외를 발생시켜야 함. (return null 하면 안됨)
        // 유저가 있다면? 유저 정보를 UserDetails 객체에 담아서 return 해줘야 한다.
        Optional<Member> optional = memberRepository.findByEmail(username);
        if(optional.isEmpty()){
            log.info("없는 이메일로 로그인 시도.");
            throw new UsernameNotFoundException(username);
        }

        log.info("있는 이메일로 로그인 시도");
        return new MemberUser(optional.get());

    }

    public List<Item> getLikeList(Member member) {
        return memberRepository
                .findById(member.getId())
                .orElseThrow(()->new UsernameNotFoundException("잘못된 회원입니다."))
                .getLikes();
    }
}
