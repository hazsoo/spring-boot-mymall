package com.megait.mymall.controller;

import com.google.gson.JsonObject;
import com.megait.mymall.domain.Item;
import com.megait.mymall.domain.Member;
import com.megait.mymall.domain.OrderItem;
import com.megait.mymall.repository.MemberRepository;
import com.megait.mymall.service.ItemService;
import com.megait.mymall.service.OrderService;
import com.megait.mymall.validation.SignUpForm;
import com.megait.mymall.service.MemberService;
import com.megait.mymall.validation.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.Validator;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Slf4j // log 변수
@RequiredArgsConstructor
public class MainController {

    private final MemberService memberService;

    private final MemberRepository memberRepository;

    private final OrderService orderService;

    private final ItemService itemService;

    @InitBinder("signUpForm")
    protected void initBinder(WebDataBinder binder){
        binder.addValidators(new SignUpFormValidator(memberRepository));
    }

    @RequestMapping("/")
    public String index(@AuthenticationMember Member member, Model model) {


        model.addAttribute("bookList", itemService.getBookList());
        model.addAttribute("albumList", itemService.getAlbumList());


        if(member != null){
            model.addAttribute(member);
        }
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
    public String emailCheckToken(String token, String email, Model model){
        Optional<Member> optional = memberRepository.findByEmail(email);
        if(optional.isEmpty()){
//            log.info("Email does not exist.");
//            return "redirect:/";
            model.addAttribute("error", "wrong.email");
            return "member/checked-email";
        }

        Member member = optional.get();
        if(!member.isValidToken(token)){
//            log.info("Token does not match.");
//            return "redirect:/";
            model.addAttribute("error", "wrong.token");
            return "member/checked-email";
        }
        // log.info("Success!");
        model.addAttribute("email", member.getEmail());
        member.completeSignUp();
        return "member/checked-email";
    }

    @GetMapping("/login")
    public String login(){
        return "member/login";
    }

    @GetMapping("/item/detail/{id}")
    public String detail(@PathVariable Long id, Model model){
        Item item = itemService.getItem(id);
        model.addAttribute("item", item);
        return "item/detail";
    }

    @ResponseBody
    @GetMapping("/item/like/{id}")
    public String likeItem(@AuthenticationMember Member member, @PathVariable Long id) {
        JsonObject object = new JsonObject();
        if(member == null){
            object.addProperty("result", false);
            object.addProperty("message", "로그인이 필요한 기능입니다.");
            return object.toString();
            // { "result" : false, "message" : "로그인이 필요한 기능입니다." }
        }

        try {
            itemService.addLike(member, id);
            object.addProperty("result", true);
            object.addProperty("message", "찜 목록에 등록되었습니다.");

        } catch (IllegalArgumentException e){
            object.addProperty("result", false);
            object.addProperty("message", e.getMessage());
        }

        return object.toString();
    }

    @GetMapping("/item/like-list")
    public String likeList(@AuthenticationMember Member member, Model model) {
        List<Item> likeList = memberService.getLikeList(member);
        model.addAttribute("likeList", likeList);
        return "item/like_list";
    }


    @PostMapping("/cart/list")
    public String addCart(@AuthenticationMember Member member,
                          @RequestParam("item_id") String[] itemIds, // 여러개를 체크해도 나란히 배열로 넘어간다
                          Model model){

        //밑에꺼랑 같은 식
//        Long[] arr = new Long[itemIds.length];
//        for(int i = 0; i < arr.length; ++i){
//            arr[i] = Long.parseLong(itemIds[i]);
//        }
//        List<Long> list = new ArrayList<>();
//        list.addAll(List.of(arr));

        // 이거랑
//        List<Long> idList = List.of(Arrays.stream(itemIds).map(Long::parseLong).toArray(Long[]::new));
        // String[] 을 List<Long>로 바꾸는 작업
        List<Long> idList = Arrays.stream(itemIds).map(Long::parseLong).collect(Collectors.toList());

        orderService.addCart(member, idList);
        itemService.deleteLikes(member, idList);

        return cartList(member, model);
    }
    @GetMapping("/cart/list")
    public String cartList(@AuthenticationMember Member member, Model model){

        try {
            List<OrderItem> cartList = orderService.getCart(member);
            model.addAttribute("cartList", cartList);
            model.addAttribute("totalPrice", orderService.getTotalPrice(cartList));

        } catch (IllegalStateException e){
            model.addAttribute("error_message", e.getMessage());
        }
        return "cart/list";
    }
}
