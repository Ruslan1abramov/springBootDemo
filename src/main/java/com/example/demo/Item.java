package com.example.demo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
class Item {

    private @Id @GeneratedValue Long id;
    private String name;
    private int amount;
    private String invCode;

    Item(){}

    Item(String name, int amount, String invCode) {
        this.name = name;
        this.amount = amount;
        this.invCode = invCode;
    }
}
