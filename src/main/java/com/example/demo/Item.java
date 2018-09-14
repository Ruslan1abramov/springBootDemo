package com.example.demo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@Entity
@XmlRootElement(name = "item")
class Item {

    @XmlElement
    @ApiModelProperty(notes = "The database generated product ID")
    private @Id @GeneratedValue Long id;
    @XmlElement
    @ApiModelProperty(notes = "Item's name")
    private String name;
    @XmlElement
    @ApiModelProperty(notes = "Amount of items available")
    private int amount;
    @XmlElement
    @ApiModelProperty(notes = "Item's inventory code")
    private String invCode;

    Item(){}

    Item(String name, int amount, String invCode) {
        this.name = name;
        this.amount = amount;
        this.invCode = invCode;
    }
}
