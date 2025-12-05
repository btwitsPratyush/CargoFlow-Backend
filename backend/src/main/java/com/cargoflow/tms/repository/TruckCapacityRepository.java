package com.cargoflow.tms.repository;

import com.cargoflow.tms.entity.Transporter;
import com.cargoflow.tms.entity.TruckCapacity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TruckCapacityRepository extends JpaRepository<TruckCapacity, UUID> {
    Optional<TruckCapacity> findByTransporterAndTruckType(Transporter transporter, String truckType);
}
