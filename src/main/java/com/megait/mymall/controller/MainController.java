package com.megait.mymall.controller;

import com.megait.mymall.domain.Member;
import com.megait.mymall.repository.MemberRepository;
import com.megait.mymall.service.MemberService;
import com.megait.mymall.validation.SignUpForm;
import com.megait.mymall.validation.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@Slf4j // log 변수
@RequiredArgsConstructor // final 변수를 외부에서 받아서 초기화하기 위해 생성자를 만듬
public class MainController {

    // final로 선언해서 requiredargsconstructor 해라! = Autowired (오류가 많이 떠서..)
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @InitBinder("signUpForm") // 컨트롤러에만 넣을 수 있음, ("")요청받기 전에 할일
    protected void initBinder(WebDataBinder binder){
        binder.addValidators(new SignUpFormValidator(memberRepository));
    }

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/signup")
    public String signUpForm(Model model){
        model.addAttribute("signUpForm", new SignUpForm());
        return "member/signup";
    }

    @PostMapping("/signup")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors){
        // 유효성 검사 시작. - initBinder() 가 실행됨.
        if(errors.hasErrors()){
            log.error("errors : {}", errors.getAllErrors());
            return "member/signup"; // "redirect:/signup"
        }
        log.info("올바른 회원 정보.");

        // 회원가입 서비스 실행
        Member member = memberService.processNewMember(signUpForm);

        // 로그인했다고 처리
        memberService.login(member);

        return "redirect:/"; // "/"로 리다이렉트
    }

    @GetMapping("/email-check-token")
    @Transactional
    public String emailCheckToken(String token, String email){
        Optional<Member> optional = memberRepository.findByEmail(email);
        if(optional.isEmpty()){
            log.info("Email does not exist.");
            return "redirect:/";
        }

        Member member = optional.get();
        if(!token.equals(member.getEmailCheckToken())){
            log.info("Token does not match.");
            return "redirect:/";
        }
        log.info("Success!");
        member.setEmailVerified(true);
        return "redirect:/";
    }
}
