package com.example.demo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
class Item {

    @ApiModelProperty(notes = "The database generated product ID")
    private @Id @GeneratedValue Long id;
    @ApiModelProperty(notes = "Item's name")
    private String name;
    @ApiModelProperty(notes = "Amount of items available")
    private int amount;
    @ApiModelProperty(notes = "Item's inventory code")
    private String invCode;

    Item(){}

    Item(String name, int amount, String invCode) {
        this.name = name;
        this.amount = amount;
        this.invCode = invCode;
    }
}
