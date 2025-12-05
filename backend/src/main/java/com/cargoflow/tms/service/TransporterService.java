package com.cargoflow.tms.service;

import com.cargoflow.tms.dto.TransporterDTO;

import java.util.UUID;

public interface TransporterService {
    TransporterDTO.Response registerTransporter(TransporterDTO.RegisterRequest request);
    TransporterDTO.Response getTransporter(UUID transporterId);
    void updateTruckCapacity(UUID transporterId, TransporterDTO.UpdateCapacityRequest request);
}
