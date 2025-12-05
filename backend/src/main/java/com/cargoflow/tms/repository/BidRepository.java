package com.cargoflow.tms.repository;

import com.cargoflow.tms.entity.Bid;
import com.cargoflow.tms.entity.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {
    List<Bid> findByLoad_LoadId(UUID loadId);
    List<Bid> findByLoad_LoadIdAndStatus(UUID loadId, BidStatus status);
    List<Bid> findByTransporter_TransporterId(UUID transporterId);
    
    @Query("SELECT b FROM Bid b WHERE (:loadId IS NULL OR b.load.loadId = :loadId) " +
           "AND (:transporterId IS NULL OR b.transporter.transporterId = :transporterId) " +
           "AND (:status IS NULL OR b.status = :status)")
    List<Bid> findBids(@Param("loadId") UUID loadId, 
                       @Param("transporterId") UUID transporterId, 
                       @Param("status") BidStatus status);
}
