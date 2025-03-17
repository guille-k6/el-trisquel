package com.trisquel.model.Dto;

import com.trisquel.model.DailyBook;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DailyBookDTO {
    private Long id;
    private VehicleDTO vehicle;
    private Date date;
    private Long vehicleKmsBefore;
    private Long vehicleKmsAfter;
    private Long kgTankBefore;
    private Long kgTankAfter;
    private Long ltExtractedTank;
    private Long ltRemainingFlask;
    private Long ltTotalFlask;
    private List<DailyBookItemDTO> items;

    public DailyBookDTO() {
    }

    public DailyBookDTO(Long id, VehicleDTO vehicle, Date date, List<DailyBookItemDTO> items) {
        this.id = id;
        this.vehicle = vehicle;
        this.date = date;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VehicleDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleDTO vehicle) {
        this.vehicle = vehicle;
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

    public static DailyBookDTO translateToDTO(DailyBook dailyBook) {
        DailyBookDTO dailyBookDTO = new DailyBookDTO();
        dailyBookDTO.setId(dailyBook.getId());
        dailyBookDTO.setDate(dailyBook.getDate());
        dailyBookDTO.setVehicle(VehicleDTO.translateToDTO(dailyBook.getVehicle()));
        dailyBookDTO.setVehicleKmsBefore(dailyBook.getVehicleKmsBefore());
        dailyBookDTO.setVehicleKmsAfter(dailyBook.getVehicleKmsAfter());
        dailyBookDTO.setKgTankBefore(dailyBook.getKgTankBefore());
        dailyBookDTO.setKgTankAfter(dailyBook.getKgTankAfter());
        dailyBookDTO.setLtExtractedTank(dailyBook.getLtExtractedTank());
        dailyBookDTO.setLtRemainingFlask(dailyBook.getLtRemainingFlask());
        dailyBookDTO.setLtTotalFlask(dailyBook.getLtTotalFlask());
        dailyBookDTO.setItems(DailyBookItemDTO.translateToDTOs(dailyBook.getItems()));
        return dailyBookDTO;
    }

    public static List<DailyBookDTO> translateToDTOs(List<DailyBook> dailyBooks) {
        return dailyBooks.stream().map(DailyBookDTO::translateToDTO).collect(Collectors.toList());
    }
}
