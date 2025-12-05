package com.cargoflow.tms.repository;

import com.cargoflow.tms.entity.Booking;
import com.cargoflow.tms.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    @Query("SELECT SUM(b.allocatedTrucks) FROM Booking b WHERE b.load.loadId = :loadId AND b.status = 'CONFIRMED'")
    Integer sumAllocatedTrucksByLoadId(@Param("loadId") UUID loadId);
    
    List<Booking> findByLoad_LoadId(UUID loadId);
}
