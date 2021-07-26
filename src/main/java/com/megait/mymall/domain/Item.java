package com.megait.mymall.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Item {
    @Id @GeneratedValue
    private Long id; // 상품 PK

    private String name; // 상품명

    private int price; // 상품가격

    private int stockQuantity; // 재고량

    @ManyToMany
    private List<Category> categories = new ArrayList<>(); // 소속 카테고리

    private String imageUrl; // 상품 이미지 경로

    private int liked; // 찜하기 수
}
