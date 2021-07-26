package com.megait.mymall.repository;

import com.megait.mymall.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // Member findByEmail(String email); // Nullable : null 가능성 있다
    Optional<Member> findByEmail(String email); // NonNull : null 가능성 0
    // Optional : NullPointerException 을 방지하고 싶어서 나온 클래스
}
