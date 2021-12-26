package com.jpabook.jpashop.api;

import com.jpabook.jpashop.Repository.OrderRepository;
import com.jpabook.jpashop.Repository.OrderSearch;
import com.jpabook.jpashop.Repository.OrderSimpleQueryDto;
import com.jpabook.jpashop.domain.Address;
import com.jpabook.jpashop.domain.Order;
import com.jpabook.jpashop.domain.OrderStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// x to one
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    // entity 노출로 잘못된 방법!
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for(Order order : all){
            order.getMember().getName();
            order.getDelivery().getAddress();
        }
        return all;
    }

    // entity 대신 dto를 활용 하지만 n+1의 문제가 발생
    @GetMapping("/api/v2/simple-orders")
    public List<OrderSimpleDto> ordersV2(){
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        List<OrderSimpleDto> result = orders.stream()
                .map(o -> new OrderSimpleDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // join fetch로 호출 query를 줄여서 n+1 문제를 차단
    @GetMapping("/api/v3/simple-orders")
    public List<OrderSimpleDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<OrderSimpleDto> result = orders.stream()
                .map(o -> new OrderSimpleDto(o))
                .collect(Collectors.toList());
        return result;
    }

    // query를 직접 수정하여 select 해올 부분을 줄이는 방법
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4(){
        return orderRepository.findOrderDtos();
    }
    // v3 vs v4 ? trade off가 있음 : v4는 성능이 약간 좋지만 직접 query를 작성해야되서 특정 dto에서만 쓸 수밖에 없다. 즉 재사용성이 없음!

    // Object for this practice only!
    @Data
    public class OrderSimpleDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public OrderSimpleDto(Order order){
            orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getMember().getAddress();
        }
    }

}
