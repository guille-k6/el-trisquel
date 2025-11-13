package trisquel.model.Dto;

import trisquel.model.Vehicle;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class VehicleDTO {
    private Long id;
    private String name;
    private LocalDate purchaseDate;
    private Long purchaseDatePrice;

    public VehicleDTO(Long id, String name, LocalDate purchaseDate, Long purchaseDatePrice) {
        this.id = id;
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.purchaseDatePrice = purchaseDatePrice;
    }

    public VehicleDTO() {
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

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Long getPurchaseDatePrice() {
        return purchaseDatePrice;
    }

    public void setPurchaseDatePrice(Long purchaseDatePrice) {
        this.purchaseDatePrice = purchaseDatePrice;
    }

    public static VehicleDTO translateToDTO(Vehicle vehicle) {
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setId(vehicle.getId());
        vehicleDTO.setName(vehicle.getName());
        vehicleDTO.setPurchaseDate(vehicle.getPurchaseDate());
        vehicleDTO.setPurchaseDatePrice(vehicle.getPurchaseDatePrice());
        return vehicleDTO;
    }

    public static List<VehicleDTO> translateToDTO(List<Vehicle> vehicles) {
        return vehicles.stream().map(VehicleDTO::translateToDTO).collect(Collectors.toList());
    }
}
