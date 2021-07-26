package com.megait.mymall.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String zip; // 우편번호
    private String city; // 기본주소
    private String street; // 상세주소
}
