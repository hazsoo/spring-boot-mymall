package com.megait.mymall.validation;

import com.megait.mymall.domain.Member;
import com.megait.mymall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class  SignUpFormValidator implements Validator {

    private final MemberRepository repository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpForm form = (SignUpForm) target;
        Optional<Member> optional = repository.findByEmail(form.getEmail());
        if(optional.isPresent()){
            errors.rejectValue("email", "duplicate.email", "이미 가입된 이메일입니다.");
        }
    }
}
