package com.megait.mymall.service;

import com.megait.mymall.domain.*;
import com.megait.mymall.repository.ItemRepository;
import com.megait.mymall.repository.MemberRepository;
import com.megait.mymall.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    private final ItemService itemService;

    @PostConstruct
    @Transactional
    protected void createTestUserLikes() { // admin 계정 모든 상품 다 찜하기 하는거
        Member member = memberRepository.findByEmail("admin@test.com").orElse(null);
        List<Item> itemList = itemRepository.findAll();
        itemList.forEach(e -> itemService.addLike(member, e.getId()));
    }

    @Transactional
    public void addCart(Member member0, List<Long> itemIds) {
        // 해당 회원 담궜다 빼기 (영속상태 만들기) 없어도되나?
        // final 로 선언한 이유? 익명함수 내부에서 호출되는 변수는 final 변수여야 함 (지역변수, 매개변수)
        final Member member = memberRepository.getById(member0.getId());

        // 장바구니 엔티티 조회하기 (장바구니 엔티티는 Order 에 들어있고, status 가 CART 임.
        // 한 외원 당 하나의 CART 엔티티를 가질 수 있음.
        // Order 테이블에는 상품이 Item 이 아닌 OrderItem 형으로 들어있음.
        // (Order : Item = 다 : 다 ~~~> Order : OrderItem = 1 : 다 / OrderItem : Item = 다 : 1)
        // 다 : 다 관계는 중간에 mapping 테이블을 이용해 다 : 1, 1 : 다 로 쪼개줘야한다
        Optional<Order> optional = orderRepository.findByStatusAndMember(OrderStatus.CART, member);
        log.info("order : {}", optional.isPresent());

        // optional로 포장된 장바구니의 알맹이 꺼내는데 만약 아직 한번도 장바구니를 이용안해서 order 엔티티가 없으면 람다 실행
        // 해당 회원의 장바구니 엔티티가 있었다면 그 엔티티를 order에 대입
        // 없었다면 새 엔티티를 만들어 Order 테이블에 추가하고 이 새 엔티티를 order 에 대입입
       final Order order = optional.orElseGet(
                ()->
                        orderRepository.save(
                                Order.builder()
                                        .member(member)
                                        .status(OrderStatus.CART)
                                        .orderDate(LocalDateTime.now())
                                        .build()
                        )
        );

        // 장바구니에 넣을 상품 엔티티들을 List 로 담음
        List<Item> newItemList = itemRepository.findAllById(itemIds);

        // List<Item> ~> List<OrderItem>
        List<OrderItem> newOrderItemList = newItemList.stream().map(item -> OrderItem.builder()
                .item(item)
                .order(order)
                .count(1)
                .orderPrice(item.getPrice())
                .build()).collect(Collectors.toList());

        // 장바구니 엔티티의 상품 List 가 null ?
        if (order.getOrderItems() == null) {
            // 새 빈 리스트를 생성해서 장바구니 엔티티의 상품 리스트를 등록
            order.setOrderItems(new ArrayList<>());
        }
        // 장바구니 엔티티의 상품(OrderItem) 리스트에 위에서 생성한 새 상품들(newOrderItemList)을 추가가
       order.getOrderItems().addAll(newOrderItemList);
    }

    public List<OrderItem> getCart(Member member) {
        Optional<Order> optional = orderRepository.findByStatusAndMember(OrderStatus.CART, member);
        return optional.orElseThrow(()->new IllegalStateException("empty.cart")).getOrderItems();
    }


    public int getTotalPrice(List<OrderItem> list) {
        return list.stream().mapToInt(orderItem -> orderItem.getItem().getPrice()).sum();
    }
}