package com.jpabook.jpashop.Repository;

import com.jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {

    // 회원명
    private String memberName;

    // 주문상태
    private OrderStatus orderStatus;

}
