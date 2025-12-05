package com.cargoflow.tms.service.impl;

import com.cargoflow.tms.dto.TransporterDTO;
import com.cargoflow.tms.entity.Transporter;
import com.cargoflow.tms.entity.TruckCapacity;
import com.cargoflow.tms.exception.CustomExceptions;
import com.cargoflow.tms.repository.TransporterRepository;
import com.cargoflow.tms.repository.TruckCapacityRepository;
import com.cargoflow.tms.service.TransporterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransporterServiceImpl implements TransporterService {

    private final TransporterRepository transporterRepository;
    private final TruckCapacityRepository truckCapacityRepository;

    @Override
    @Transactional
    public TransporterDTO.Response registerTransporter(TransporterDTO.RegisterRequest request) {
        if (transporterRepository.findByCompanyName(request.getCompanyName()).isPresent()) {
            throw new CustomExceptions.InvalidStatusTransitionException("Transporter with this company name already exists");
        }

        Transporter transporter = Transporter.builder()
                .companyName(request.getCompanyName())
                .rating(5.0) // Default rating
                .build();

        Transporter saved = transporterRepository.save(transporter);
        return mapToResponse(saved);
    }

    @Override
    public TransporterDTO.Response getTransporter(UUID transporterId) {
        Transporter transporter = transporterRepository.findById(transporterId)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Transporter not found"));
        return mapToResponse(transporter);
    }

    @Override
    @Transactional
    public void updateTruckCapacity(UUID transporterId, TransporterDTO.UpdateCapacityRequest request) {
        Transporter transporter = transporterRepository.findById(transporterId)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Transporter not found"));

        TruckCapacity capacity = truckCapacityRepository.findByTransporterAndTruckType(transporter, request.getTruckType())
                .orElse(TruckCapacity.builder()
                        .transporter(transporter)
                        .truckType(request.getTruckType())
                        .availableCount(0)
                        .build());

        capacity.setAvailableCount(request.getCount());
        truckCapacityRepository.save(capacity);
    }

    private TransporterDTO.Response mapToResponse(Transporter transporter) {
        return TransporterDTO.Response.builder()
                .transporterId(transporter.getTransporterId())
                .companyName(transporter.getCompanyName())
                .rating(transporter.getRating())
                .createdAt(transporter.getCreatedAt())
                .build();
    }
}
