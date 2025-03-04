package com.trisquel.model.Dto;

import java.sql.Date;
import java.util.List;

public class DailyBookDTO {
    private Long id;
    private Long vehicleId;
    private Date date;
    private List<DailyBookItemDTO> items;

    public DailyBookDTO(Long id, Long vehicleId, Date date, List<DailyBookItemDTO> items) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.date = date;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<DailyBookItemDTO> getItems() {
        return items;
    }

    public void setItems(List<DailyBookItemDTO> items) {
        this.items = items;
    }
}
