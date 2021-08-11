package com.megait.mymall.repository;

import com.megait.mymall.domain.Member;
import com.megait.mymall.domain.Order;
import com.megait.mymall.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByStatusAndMember(OrderStatus status, Member member); //JpaRepository 가 자동으로 채워줄거임
}
