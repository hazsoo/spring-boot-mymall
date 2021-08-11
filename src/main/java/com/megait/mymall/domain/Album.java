package com.megait.mymall.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("al")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@SuperBuilder // 부모타입의 빌더를 그대로 쓰겠다
public class Album extends Item{
    private String title;
    private String artist;
}
