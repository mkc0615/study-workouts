package com.jpabook.jpashop.domain;

import com.jpabook.jpashop.domain.item.Item;
import lombok.Getter;

import javax.persistence.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    // 실무에서는 다대다 관계를 쓰지 말것! 써야되면 다대일 일대다로 풀어서 사용 // 여기서는 공부겸 사용 //
    @ManyToMany
    @JoinTable(name="category_item",
        joinColumns = @JoinColumn(name="category_id"),
        inverseJoinColumns = @JoinColumn(name="item_id"))
    private List<Item> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

}
