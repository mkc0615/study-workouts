package com.jpabook.jpashop.Repository;

import com.jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    // 동적 쿼리 사용 방법1 : jpql를 하드로 직접 작성 -> 복잡해지고 권장 x
    public List<Order> findAll(OrderSearch orderSearch){

        List<Order> resultList = em.createQuery("select o from Order o join o.member m " +
                        "where o.status =:status " +
                        "and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000) // 페이징 -> 최대 1000개
                .getResultList();

        return resultList;
    }

    // 동적 쿼리 사용 방법2 : jpa criteria 사용 -> 이것도 권장은 안함 ( 너무 어려워 유지보수 안됨 )
    public List<Order> findAllByCriteria(OrderSearch orderSearch){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        if(orderSearch.getOrderStatus() != null){
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        if(StringUtils.hasText(orderSearch.getMemberName())){
            Predicate name = cb.like(m.<String>get("name"), "%"+orderSearch.getMemberName()+"%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    // 최적의 방법은 후에 기술할 queryDsl을 사용한 것!

    // join fetch query
    public List<Order> findAllWithMemberDelivery(){
        return em.createQuery(
                "select o from Order o"+
                        " join fetch o.member m"+
                        "join fetch o.delivery d", Order.class
        ).getResultList();
    }

    //
    public List<OrderSimpleQueryDto> findOrderDtos(){
        return em.createQuery("select new jpabook.jpashop.Repository.OrderSimpleQueryDto("+
                "o.id, o.name, o.orderDate, o.orderStatus, o.address)"+
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }

}
