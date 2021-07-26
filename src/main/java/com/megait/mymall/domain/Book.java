package com.megait.mymall.domain;

import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("bk")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Book extends Item{
    private String isbn;
}
