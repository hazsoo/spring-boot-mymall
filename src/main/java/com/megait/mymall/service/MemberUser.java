package com.megait.mymall.service;

import com.megait.mymall.domain.Member;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class MemberUser extends User {
    private final Member member;

    public MemberUser(Member member) {
        super(
                member.getEmail(), // User 의 email 필드
                member.getPassword(), // User 의 password 필드
                List.of(new SimpleGrantedAuthority(member.getType().name())) // User 의 authorities 필드
        );

        this.member = member;
    }
}