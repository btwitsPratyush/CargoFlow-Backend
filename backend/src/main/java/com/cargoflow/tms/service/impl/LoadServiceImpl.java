package com.cargoflow.tms.service.impl;

import com.cargoflow.tms.dto.BidDTO;
import com.cargoflow.tms.dto.LoadDTO;
import com.cargoflow.tms.entity.Bid;
import com.cargoflow.tms.entity.BidStatus;
import com.cargoflow.tms.entity.Load;
import com.cargoflow.tms.entity.LoadStatus;
import com.cargoflow.tms.exception.CustomExceptions;
import com.cargoflow.tms.repository.BidRepository;
import com.cargoflow.tms.repository.LoadRepository;
import com.cargoflow.tms.service.LoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoadServiceImpl implements LoadService {

    private final LoadRepository loadRepository;
    private final BidRepository bidRepository;

    @Override
    @Transactional
    public LoadDTO.Response createLoad(LoadDTO.CreateRequest request) {
        Load load = Load.builder()
                .shipperId(request.getShipperId())
                .loadingCity(request.getLoadingCity())
                .unloadingCity(request.getUnloadingCity())
                .loadingDate(request.getLoadingDate())
                .productType(request.getProductType())
                .weight(request.getWeight())
                .weightUnit(request.getWeightUnit())
                .truckType(request.getTruckType())
                .noOfTrucks(request.getNoOfTrucks())
                .status(LoadStatus.POSTED)
                .build();

        Load saved = loadRepository.save(load);
        return mapToResponse(saved);
    }

    @Override
    public Page<LoadDTO.Response> searchLoads(String shipperId, LoadStatus status, Pageable pageable) {
        Page<Load> loads;
        if (shipperId != null && status != null) {
            loads = loadRepository.findByShipperIdAndStatus(shipperId, status, pageable);
        } else if (shipperId != null) {
            loads = loadRepository.findByShipperId(shipperId, pageable);
        } else if (status != null) {
            loads = loadRepository.findByStatus(status, pageable);
        } else {
            loads = loadRepository.findAll(pageable);
        }
        return loads.map(this::mapToResponse);
    }

    @Override
    public LoadDTO.Response getLoad(UUID loadId) {
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Load not found"));
        return mapToResponse(load);
    }

    @Override
    @Transactional
    public void cancelLoad(UUID loadId) {
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Load not found"));

        if (load.getStatus() == LoadStatus.BOOKED) {
            throw new CustomExceptions.InvalidStatusTransitionException("Cannot cancel a BOOKED load");
        }

        load.setStatus(LoadStatus.CANCELLED);
        loadRepository.save(load);
    }

    @Override
    public List<BidDTO.Response> getBestBids(UUID loadId) {
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Load not found"));

        List<Bid> bids = bidRepository.findByLoad_LoadIdAndStatus(loadId, BidStatus.PENDING);

        return bids.stream()
                .map(bid -> {
                    double score = calculateScore(bid);
                    BidDTO.Response response = mapBidToResponse(bid);
                    response.setScore(score);
                    return response;
                })
                .sorted(Comparator.comparingDouble(BidDTO.Response::getScore).reversed())
                .collect(Collectors.toList());
    }

    private double calculateScore(Bid bid) {
        double rate = bid.getProposedRate();
        double rating = bid.getTransporter().getRating();
        
        // Handle division by zero if rate is 0 (though validation should prevent this)
        if (rate <= 0) return 0;

        return (1 / rate) * 0.7 + (rating / 5.0) * 0.3;
    }

    private LoadDTO.Response mapToResponse(Load load) {
        return LoadDTO.Response.builder()
                .loadId(load.getLoadId())
                .shipperId(load.getShipperId())
                .loadingCity(load.getLoadingCity())
                .unloadingCity(load.getUnloadingCity())
                .loadingDate(load.getLoadingDate())
                .productType(load.getProductType())
                .weight(load.getWeight())
                .weightUnit(load.getWeightUnit())
                .truckType(load.getTruckType())
                .noOfTrucks(load.getNoOfTrucks())
                .status(load.getStatus())
                .datePosted(load.getDatePosted())
                .build();
    }

    private BidDTO.Response mapBidToResponse(Bid bid) {
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
