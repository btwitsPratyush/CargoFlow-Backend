package com.cargoflow.tms.controller;

import com.cargoflow.tms.dto.BidDTO;
import com.cargoflow.tms.dto.LoadDTO;
import com.cargoflow.tms.entity.LoadStatus;
import com.cargoflow.tms.service.LoadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loads")
@RequiredArgsConstructor
public class LoadController {

    private final LoadService loadService;

    @PostMapping
    public ResponseEntity<LoadDTO.Response> createLoad(@Valid @RequestBody LoadDTO.CreateRequest request) {
        return new ResponseEntity<>(loadService.createLoad(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<LoadDTO.Response>> searchLoads(
            @RequestParam(required = false) String shipperId,
            @RequestParam(required = false) LoadStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(loadService.searchLoads(shipperId, status, pageable));
    }

    @GetMapping("/{loadId}")
    public ResponseEntity<LoadDTO.Response> getLoad(@PathVariable UUID loadId) {
        return ResponseEntity.ok(loadService.getLoad(loadId));
    }

    @PatchMapping("/{loadId}/cancel")
    public ResponseEntity<Void> cancelLoad(@PathVariable UUID loadId) {
        loadService.cancelLoad(loadId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{loadId}/best-bids")
    public ResponseEntity<List<BidDTO.Response>> getBestBids(@PathVariable UUID loadId) {
        return ResponseEntity.ok(loadService.getBestBids(loadId));
    }
}
