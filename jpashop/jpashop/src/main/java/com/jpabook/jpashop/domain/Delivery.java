package com.jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Delivery {

    @Id @GeneratedValue
    @Column(name="delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // enum 쓸때는 꼭 STRING으로! 추가시 에러 발생할 수 있음
    private DeliveryStatus status; // Ready, Comp



}
