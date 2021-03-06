package com.jpabook.jpashop.domain.item;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.ArrayList;

@Entity
@DiscriminatorValue("M")
@Getter
public class Movie extends Item{

    private String director;
    private String actor;

}
