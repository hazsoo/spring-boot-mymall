package com.megait.mymall.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Delivery {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order; // 배송하는 주문

    @Embedded
    private Address address; // 배송지

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; // 배송 상태
}
