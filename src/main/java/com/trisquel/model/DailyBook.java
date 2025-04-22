package com.trisquel.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "daily_book")
public class DailyBook {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daily_book_seq")
    @SequenceGenerator(name = "daily_book_seq", sequenceName = "daily_book_seq", allocationSize = 1)
    private Long id;

    private LocalDate date;
    private Long vehicleKmsBefore;
    private Long vehicleKmsAfter;
    private Long kgTankBefore;
    private Long kgTankAfter;
    private Long ltExtractedTank;
    private Long ltRemainingFlask;
    private Long ltTotalFlask;
    @OneToMany(mappedBy = "dailyBook", cascade = CascadeType.ALL)
    private List<DailyBookItem> items;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id")
    private Vehicle vehicle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getVehicleKmsBefore() {
        return vehicleKmsBefore;
    }

    public void setVehicleKmsBefore(Long vehicleKmsBefore) {
        this.vehicleKmsBefore = vehicleKmsBefore;
    }

    public Long getVehicleKmsAfter() {
        return vehicleKmsAfter;
    }

    public void setVehicleKmsAfter(Long vehicleKmsAfter) {
        this.vehicleKmsAfter = vehicleKmsAfter;
    }

    public Long getKgTankBefore() {
        return kgTankBefore;
    }

    public void setKgTankBefore(Long kgTankBefore) {
        this.kgTankBefore = kgTankBefore;
    }

    public Long getKgTankAfter() {
        return kgTankAfter;
    }

    public void setKgTankAfter(Long kgTankAfter) {
        this.kgTankAfter = kgTankAfter;
    }

    public Long getLtExtractedTank() {
        return ltExtractedTank;
    }

    public void setLtExtractedTank(Long ltExtractedTank) {
        this.ltExtractedTank = ltExtractedTank;
    }

    public Long getLtRemainingFlask() {
        return ltRemainingFlask;
    }

    public void setLtRemainingFlask(Long ltRemainingFlask) {
        this.ltRemainingFlask = ltRemainingFlask;
    }

    public Long getLtTotalFlask() {
        return ltTotalFlask;
    }

    public void setLtTotalFlask(Long ltTotalFlask) {
        this.ltTotalFlask = ltTotalFlask;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<DailyBookItem> getItems() {
        return items;
    }

    public void setItems(List<DailyBookItem> items) {
        this.items = items;
    }
}
