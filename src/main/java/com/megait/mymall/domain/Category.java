package com.megait.mymall.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter @Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Category {

    @Id @GeneratedValue
    private Long id; // 카테고리 PK

    private String name; // 카테고리 이름

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent; // 이 카테고리의 상위 카테고리

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>(); // 이 카테고리의 하위 카테고리

    @ManyToMany
    private List<Item> items = new ArrayList<>(); // 이 카테고리에 속한 상품들

    // TODO 0715 - Category 완성하기

}
