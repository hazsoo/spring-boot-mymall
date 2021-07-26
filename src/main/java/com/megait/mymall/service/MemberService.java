package com.megait.mymall.service;

import com.megait.mymall.domain.Address;
import com.megait.mymall.domain.Member;
import com.megait.mymall.domain.MemberType;
import com.megait.mymall.validation.SignUpFormValidator;
import com.megait.mymall.validation.SignUpForm;
import com.megait.mymall.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.validation.Validator;
import java.time.LocalDateTime;

@Service // 비지니스 로직. 유효성검사 같은거
public class MemberService {

    @Autowired
    SignUpFormValidator signUpFormValidator;

    @Autowired
    LocalValidatorFactoryBean localValidatorFactoryBean;

    @Autowired
    Validator validator;

    @Autowired
    MemberRepository memberRepository;

    @InitBinder
    protected void initBinder(WebDataBinder binder){
        binder.setValidator(new SignUpFormValidator(memberRepository));
    }

    public Member processNewMember(SignUpForm signupForm) {

        // 유효성 검사 시작 - initBinder() 가 실행됨

        // 올바른 form인 경우 DB 저장
        Member member = Member.builder()
                .email(signupForm.getEmail())
                .password(signupForm.getPassword())
                .address(Address.builder()
                        .city(signupForm.getCity())
                        .street(signupForm.getStreet())
                        .zip(signupForm.getZipcode())
                        .build())
                .type(MemberType.ROLE_USER)
                .joinedAt(LocalDateTime.now())
                .build();
        memberRepository.save(member);

        // TODO 이메일로 인증 링크 전송

        // 새로 추가된 회원 (Member 엔티티)을 return
        return member;
    }

    public void login(Member member) {

        // 해당 member를 authenticated 상태로 저장 => 로그인 완료 처리

    }
}
