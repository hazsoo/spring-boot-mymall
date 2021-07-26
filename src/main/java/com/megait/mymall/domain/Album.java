package com.megait.mymall.domain;

import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("al")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Album extends Item{
    private String title;
    private String artist;
}
