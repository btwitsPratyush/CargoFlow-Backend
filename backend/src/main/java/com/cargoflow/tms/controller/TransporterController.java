package com.cargoflow.tms.controller;

import com.cargoflow.tms.dto.TransporterDTO;
import com.cargoflow.tms.service.TransporterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transporters")
@RequiredArgsConstructor
public class TransporterController {

    private final TransporterService transporterService;

    @PostMapping
    public ResponseEntity<TransporterDTO.Response> registerTransporter(@Valid @RequestBody TransporterDTO.RegisterRequest request) {
        return new ResponseEntity<>(transporterService.registerTransporter(request), HttpStatus.CREATED);
    }

    @GetMapping("/{transporterId}")
    public ResponseEntity<TransporterDTO.Response> getTransporter(@PathVariable UUID transporterId) {
        return ResponseEntity.ok(transporterService.getTransporter(transporterId));
    }

    @PutMapping("/{transporterId}/trucks")
    public ResponseEntity<Void> updateTruckCapacity(
            @PathVariable UUID transporterId,
            @Valid @RequestBody TransporterDTO.UpdateCapacityRequest request) {
        transporterService.updateTruckCapacity(transporterId, request);
        return ResponseEntity.noContent().build();
    }
}
