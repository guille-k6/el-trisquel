package com.trisquel.model;

import jakarta.persistence.*;


/**
 * Remito
 */
@Entity
@Table(name = "voucher")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "voucher_seq")
    @SequenceGenerator(name = "daily_book_item_seq", sequenceName = "voucher_seq", allocationSize = 1)
    private Long id;
    private String name;
    private boolean invoiceable;

    public Voucher() {
    }

    public Voucher(Long id, String name, boolean invoiceable) {
        this.id = id;
        this.name = name;
        this.invoiceable = invoiceable;
    }

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

    public boolean isInvoiceable() {
        return invoiceable;
    }

    public void setInvoiceable(boolean invoiceable) {
        this.invoiceable = invoiceable;
    }
}
