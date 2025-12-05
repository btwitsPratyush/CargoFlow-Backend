package com.cargoflow.tms.repository;

import com.cargoflow.tms.entity.Load;
import com.cargoflow.tms.entity.LoadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoadRepository extends JpaRepository<Load, UUID> {
    Page<Load> findByShipperIdAndStatus(String shipperId, LoadStatus status, Pageable pageable);
    Page<Load> findByShipperId(String shipperId, Pageable pageable);
    Page<Load> findByStatus(LoadStatus status, Pageable pageable);
}
