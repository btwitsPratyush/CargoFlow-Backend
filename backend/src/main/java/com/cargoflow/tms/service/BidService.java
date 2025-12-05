package com.cargoflow.tms.service;

import com.cargoflow.tms.dto.BidDTO;
import com.cargoflow.tms.entity.BidStatus;

import java.util.List;
import java.util.UUID;

public interface BidService {
    BidDTO.Response submitBid(BidDTO.CreateRequest request);
    List<BidDTO.Response> searchBids(UUID loadId, UUID transporterId, BidStatus status);
    BidDTO.Response getBid(UUID bidId);
    void rejectBid(UUID bidId);
}
