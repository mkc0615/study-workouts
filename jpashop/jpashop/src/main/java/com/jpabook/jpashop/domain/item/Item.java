package com.jpabook.jpashop.domain.item;

import com.jpabook.jpashop.domain.Category;
import com.jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="Dtype")
@Getter
@Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //=business logic=//
    // 재고 증가
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }
    // 재고 감소
    public void removeStock(int orderQuantity){
        int restStock = this.stockQuantity - orderQuantity;
        if(restStock < 0){
            throw new NotEnoughStockException("Need More Stock!");
        }
        this.stockQuantity = restStock;
    }

}
