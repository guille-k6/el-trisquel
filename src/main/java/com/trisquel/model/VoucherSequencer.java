package com.trisquel.model;

import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "num_voucher_config")
public class VoucherSequencer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "voucher_num_seq")
    @SequenceGenerator(name = "voucher_num_seq", sequenceName = "voucher_num_seq", allocationSize = 1)
    private Long id;

    @Column(name = "number")
    private Long number;

    @Column(name = "creation")
    private ZonedDateTime creation;

    // Default constructor
    public VoucherSequencer() {
        this.creation = ZonedDateTime.now();
    }

    // Constructor with number
    public VoucherSequencer(Long number) {
        this.number = number;
        this.creation = ZonedDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public ZonedDateTime getCreation() {
        return creation;
    }

    public void setCreation(ZonedDateTime creation) {
        this.creation = creation;
    }

    @Override
    public String toString() {
        return "VoucherSequencer{" + "id=" + id + ", number=" + number + ", creation=" + creation + '}';
    }
}