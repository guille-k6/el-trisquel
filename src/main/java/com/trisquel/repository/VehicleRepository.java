package com.trisquel.repository;

import com.trisquel.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public
interface VehicleRepository extends JpaRepository<Vehicle, Long> {}
