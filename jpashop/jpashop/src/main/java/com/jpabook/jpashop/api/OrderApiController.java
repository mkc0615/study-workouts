package com.jpabook.jpashop.api;

import com.jpabook.jpashop.Repository.OrderRepository;
import com.jpabook.jpashop.Repository.OrderSearch;
import com.jpabook.jpashop.Repository.order.query.OrderFlatDto;
import com.jpabook.jpashop.Repository.order.query.OrderItemQueryDto;
import com.jpabook.jpashop.Repository.order.query.OrderQueryDto;
import com.jpabook.jpashop.Repository.order.query.OrderQueryRepository;
import com.jpabook.jpashop.domain.Address;
import com.jpabook.jpashop.domain.Order;
import com.jpabook.jpashop.domain.OrderItem;
import com.jpabook.jpashop.domain.OrderStatus;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    // Not good : entity exposed!
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> resultList = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : resultList) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o->o.getItem().getName());
        }
        return resultList;
    }

    // better than v1 , but to many queries
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return collect;
    }

    // use fetch join but even if you use distinct you cannot use paging!!!
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return collect;
    }

    // enable paging from v3 using 'default_batch_fetch_size' property
    // optimizes by fetch joining all 'to one' relationships
    // then the query from v3 by divided and sent separately
    @GetMapping("/api/v3_1/orders")
    public List<OrderDto> ordersV3_1(@RequestParam(value="offset", defaultValue = "0") int offset,
                                     @RequestParam(value="limit", defaultValue = "100") int limit){

        List<Order> orders = orderRepository.findAllWithMemberDeliveryWithParam(offset, limit);

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return collect;
    }

    // dto direct selection with jpa
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    // dto direct selection ( optimized collection search )
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findAllByDtoOptimize();
    }

    // dto with all tables combined with one query
    // then reset result of OrderFlatDto to OrderQueryDto and then return
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6(){
        List<OrderFlatDto> flatList = orderQueryRepository.findAllByDto_flat();

        return flatList.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

    @Data
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
           orderItems = order.getOrderItems().stream()
                   .map(orderItem -> new OrderItemDto(orderItem))
                   .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto{

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getItem().getPrice();
            count = orderItem.getCount();
        }
    }
}
