package com.trisquel.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_seq")
    @SequenceGenerator(name = "vehicle_seq", sequenceName = "vehicle_seq", allocationSize = 1)
    private Long id;

    private String name;
    private Date purchaseDate;
    private Long purchaseDatePrice;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Long getPurchaseDatePrice() {
        return purchaseDatePrice;
    }

    public void setPurchaseDatePrice(Long purchaseDatePrice) {
        this.purchaseDatePrice = purchaseDatePrice;
    }

}
