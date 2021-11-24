package com.jpabook.jpashop.Repository;

import com.jpabook.jpashop.domain.Address;
import com.jpabook.jpashop.domain.Member;
import com.jpabook.jpashop.domain.Order;
import com.jpabook.jpashop.domain.OrderStatus;
import com.jpabook.jpashop.domain.item.Book;
import com.jpabook.jpashop.domain.item.Item;
import com.jpabook.jpashop.exception.NotEnoughStockException;
import com.jpabook.jpashop.service.OrderService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void orderTest() throws Exception{
        // given
        Member member = createMember();

        Item item = createBook("jpa tester", 10000, 10);

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        Assert.assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        Assert.assertEquals("주문한 상품 종류 수가 정확해야한다.", 1, getOrder.getOrderItems().size());
        Assert.assertEquals("주문 가격은 가격 * 수량이다", 10000*orderCount, getOrder.getTotalPrice());
        Assert.assertEquals("주문 수량만큼 재고가 줄어야한다", 8, item.getStockQuantity());

    }

    @Test
    public void cancelTest() throws Exception{
        // given

        Member member = createMember();
        Item item = createBook("jpa tester", 10000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        Assert.assertEquals("주문 취소시 상태는 CANCEL", OrderStatus.CANCEL, getOrder.getStatus());
        Assert.assertEquals("주문이 취소된 상품은 그만큼 재고가 증가", 10, item.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void quantityTrackTest() throws Exception{
        // given
        Member member = createMember();
        Item item = createBook("jpa test", 10000, 10);

        int orderCount = 11;

        // when
        orderService.order(member.getId(), item.getId(), orderCount);

        // then
        Assert.fail("재고 수량 부족 예외가 발생해야한다!");

    }


    private Member createMember () {
        Member member = new Member();
        Item item = new Book();
        member.setName("mem1");
        member.setAddress(new Address("Seoul", "gangnam", "123-123"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        return book;
    }

}
