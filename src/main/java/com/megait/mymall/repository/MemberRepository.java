package com.megait.mymall.repository;

import com.megait.mymall.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);  // NonNull : null 일 가능성 0!!!!
    // Member findByEmail(String email);  // Nullable : null 일 가능성이 있다.
    // ** Optional : NullPointerException 을 방지하고 싶어서 나온 클래스
}
