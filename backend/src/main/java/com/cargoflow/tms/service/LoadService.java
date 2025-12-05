package com.cargoflow.tms.service;

import com.cargoflow.tms.dto.BidDTO;
import com.cargoflow.tms.dto.LoadDTO;
import com.cargoflow.tms.entity.LoadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LoadService {
    LoadDTO.Response createLoad(LoadDTO.CreateRequest request);
    Page<LoadDTO.Response> searchLoads(String shipperId, LoadStatus status, Pageable pageable);
    LoadDTO.Response getLoad(UUID loadId);
    void cancelLoad(UUID loadId);
    List<BidDTO.Response> getBestBids(UUID loadId);
}
