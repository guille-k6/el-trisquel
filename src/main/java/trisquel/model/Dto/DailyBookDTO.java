package trisquel.model.Dto;

import trisquel.model.DailyBook;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DailyBookDTO {
    private Long id;
    private VehicleDTO vehicle;
    private LocalDate date;
    private Long vehicleKmsBefore;
    private Long vehicleKmsAfter;
    private Long kgTankBefore;

    private Long kgTankAfter;

    private Long pressureTankBefore;
    private Long pressureTankAfter;
    private Long ltExtractedTank;
    private Long ltRemainingFlask;
    private Long ltTotalFlask;
    private String nitrogenProvider;
    private List<DailyBookItemDTO> items;

    public DailyBookDTO() {
    }

    public DailyBookDTO(Long id, VehicleDTO vehicle, LocalDate date, List<DailyBookItemDTO> items) {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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

    public Long getPressureTankBefore() {
        return pressureTankBefore;
    }

    public void setPressureTankBefore(Long pressureTankBefore) {
        this.pressureTankBefore = pressureTankBefore;
    }

    public Long getPressureTankAfter() {
        return pressureTankAfter;
    }

    public void setPressureTankAfter(Long pressureTankAfter) {
        this.pressureTankAfter = pressureTankAfter;
    }

    public String getNitrogenProvider() {
        return nitrogenProvider;
    }

    public void setNitrogenProvider(String nitrogenProvider) {
        this.nitrogenProvider = nitrogenProvider;
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
        dailyBookDTO.setPressureTankBefore(dailyBook.getPressureTankBefore());
        dailyBookDTO.setPressureTankAfter(dailyBook.getPressureTankAfter());
        dailyBookDTO.setLtExtractedTank(dailyBook.getLtExtractedTank());
        dailyBookDTO.setLtRemainingFlask(dailyBook.getLtRemainingFlask());
        dailyBookDTO.setLtTotalFlask(dailyBook.getLtTotalFlask());
        dailyBookDTO.setNitrogenProvider(dailyBook.getNitrogenProvider());
        dailyBookDTO.setItems(DailyBookItemDTO.translateToDTOs(dailyBook.getItems()));
        return dailyBookDTO;
    }

    public static List<DailyBookDTO> translateToDTOs(List<DailyBook> dailyBooks) {
        return dailyBooks.stream().map(DailyBookDTO::translateToDTO).collect(Collectors.toList());
    }
}
