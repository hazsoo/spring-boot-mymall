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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {
    // UserDetailsService : 로그인, 회원가입 등의 회원을 다루는 서비스에 구현하는 인터페이스

    private final MemberRepository memberRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    public Member processNewMember(SignUpForm signUpForm) {

        // 올바른 form인 경우 DB 저장
        Member member = Member.builder()
                .email(signUpForm.getEmail())
                .password(passwordEncoder.encode(signUpForm.getPassword())) // DB에 집어 넣을때 암호화
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
        emailService.sendEmail(newMember);

        // 새로 추가된 회원 (Member 엔티티)를 return
        return newMember;
    }


    public void login(Member member) {

        MemberUser memberUser = new MemberUser(member);

        // Username(=principal) 과 Password(=credencial) 를 가지고
        // 스프링 시큐리티에게 인증을 요청할 때 사용하는 token
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        /*username*/ memberUser,
                        /*password*/ memberUser.getMember().getPassword(),
                        /*authorities*/ memberUser.getAuthorities());

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token); // token으로 하나의 principal 만듦

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        // 매개변수 email: 사용자가 로그인을 시도했을 시, 그 id가 들어옴
        // loadUserByUsername(유저네임) : 유저네임을 가지고 유저 정보를 조회할 때 호출될 메서드
        // '어떻게 유저 정보를 가지고 올 지'를 작성하면 됨
        // 우리는? Member DB에서 유저정보를 꺼내야 하므로.. MemberRepository 가 사용됨
        // 주의! 없는 유저의 경우 반드시 UsernameNotFoundException 예외를 발생시켜야 함 (return null 하면 안됨)
        // 유저가 있다면? 유저 정보를 UserDetails 객체에 담아서 return 해줘야 한다

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if(optionalMember.isEmpty()){
            log.info("없는 이메일로 로그인 시도");
            throw new UsernameNotFoundException(email);
        }
//        log.info("있는 이메일로 로그인 시도");
//        return new MemberUser(optionalMember.get());

        Member member = optionalMember.get();
        User user = new User(
                member.getEmail(),
                member.getPassword(),
                List.of(new SimpleGrantedAuthority(member.getType().name()))
        );
        return user;
    }

}
