package com.cargoflow.tms.service.impl;

import com.cargoflow.tms.dto.BidDTO;
import com.cargoflow.tms.entity.Bid;
import com.cargoflow.tms.entity.BidStatus;
import com.cargoflow.tms.entity.Load;
import com.cargoflow.tms.entity.LoadStatus;
import com.cargoflow.tms.entity.Transporter;
import com.cargoflow.tms.exception.CustomExceptions;
import com.cargoflow.tms.repository.BidRepository;
import com.cargoflow.tms.repository.LoadRepository;
import com.cargoflow.tms.repository.TransporterRepository;
import com.cargoflow.tms.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final LoadRepository loadRepository;
    private final TransporterRepository transporterRepository;
    private final com.cargoflow.tms.repository.TruckCapacityRepository truckCapacityRepository;

    @Override
    @Transactional
    public BidDTO.Response submitBid(BidDTO.CreateRequest request) {
        Load load = loadRepository.findById(request.getLoadId())
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Load not found"));

        if (load.getStatus() == LoadStatus.BOOKED || load.getStatus() == LoadStatus.CANCELLED) {
            throw new CustomExceptions.InvalidStatusTransitionException("Cannot bid on a BOOKED or CANCELLED load");
        }

        Transporter transporter = transporterRepository.findById(request.getTransporterId())
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Transporter not found"));

        // Validate Transporter Capacity
        com.cargoflow.tms.entity.TruckCapacity capacity = truckCapacityRepository.findByTransporterAndTruckType(transporter, load.getTruckType())
                .orElseThrow(() -> new CustomExceptions.InsufficientCapacityException("Transporter has no capacity record for this truck type"));

        if (capacity.getAvailableCount() < request.getTrucksOffered()) {
            throw new CustomExceptions.InsufficientCapacityException("Transporter does not have enough available trucks");
        }

        Bid bid = Bid.builder()
                .load(load)
                .transporter(transporter)
                .proposedRate(request.getProposedRate())
                .trucksOffered(request.getTrucksOffered())
                .status(BidStatus.PENDING)
                .build();

        Bid saved = bidRepository.save(bid);

        // Transition Load to OPEN_FOR_BIDS if it was POSTED
        if (load.getStatus() == LoadStatus.POSTED) {
            load.setStatus(LoadStatus.OPEN_FOR_BIDS);
            loadRepository.save(load);
        }

        return mapToResponse(saved);
    }

    @Override
    public List<BidDTO.Response> searchBids(UUID loadId, UUID transporterId, BidStatus status) {
        return bidRepository.findBids(loadId, transporterId, status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BidDTO.Response getBid(UUID bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Bid not found"));
        return mapToResponse(bid);
    }

    @Override
    @Transactional
    public void rejectBid(UUID bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Bid not found"));
        
        if (bid.getStatus() != BidStatus.PENDING) {
             throw new CustomExceptions.InvalidStatusTransitionException("Only PENDING bids can be rejected");
        }

        bid.setStatus(BidStatus.REJECTED);
        bidRepository.save(bid);
    }

    private BidDTO.Response mapToResponse(Bid bid) {
        return BidDTO.Response.builder()
                .bidId(bid.getBidId())
                .loadId(bid.getLoad().getLoadId())
                .transporterId(bid.getTransporter().getTransporterId())
                .proposedRate(bid.getProposedRate())
                .trucksOffered(bid.getTrucksOffered())
                .status(bid.getStatus())
                .submittedAt(bid.getSubmittedAt())
                .build();
    }
}
