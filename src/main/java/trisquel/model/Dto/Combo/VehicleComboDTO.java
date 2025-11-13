package trisquel.model.Dto.Combo;

import trisquel.model.Dto.VehicleDTO;

import java.util.List;

public class VehicleComboDTO {
    VehicleDTO defaultConfig;
    List<VehicleDTO> vehicles;

    public VehicleComboDTO(VehicleDTO defaultConfig, List<VehicleDTO> vehicles) {
        this.defaultConfig = defaultConfig;
        this.vehicles = vehicles;
    }

    public VehicleComboDTO(List<VehicleDTO> vehicles) {
        this.vehicles = vehicles;
    }

    public VehicleDTO getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(VehicleDTO defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public List<VehicleDTO> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<VehicleDTO> vehicles) {
        this.vehicles = vehicles;
    }
}
