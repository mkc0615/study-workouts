package com.jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address(){ // reflection proxy 등의 기술을 쓰기 위해서 빈 생성자를 만들어둔다
    }

    // value객체는 생성할 때만 객체 생성되고 수정은 불가능하게 하는게 좋음
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
