package com.trisquel.service;

import com.trisquel.model.Vehicle;
import com.trisquel.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
class VehicleService {
    @Autowired
    VehicleService(VehicleRepository repository){
        this.repository = repository;
    }
    private final VehicleRepository repository;

    public List<Vehicle> findAll() { return repository.findAll(); }
    public Optional<Vehicle> findById(Long id) { return repository.findById(id); }
    public Vehicle save(Vehicle vehicle) { return repository.save(vehicle); }
    public void delete(Long id) { repository.deleteById(id); }
}
