package com.cargoflow.tms.controller;

import com.cargoflow.tms.dto.BidDTO;
import com.cargoflow.tms.entity.BidStatus;
import com.cargoflow.tms.service.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @PostMapping
    public ResponseEntity<BidDTO.Response> submitBid(@Valid @RequestBody BidDTO.CreateRequest request) {
        return new ResponseEntity<>(bidService.submitBid(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BidDTO.Response>> searchBids(
            @RequestParam(required = false) UUID loadId,
            @RequestParam(required = false) UUID transporterId,
            @RequestParam(required = false) BidStatus status) {
        return ResponseEntity.ok(bidService.searchBids(loadId, transporterId, status));
    }

    @GetMapping("/{bidId}")
    public ResponseEntity<BidDTO.Response> getBid(@PathVariable UUID bidId) {
        return ResponseEntity.ok(bidService.getBid(bidId));
    }

    @PatchMapping("/{bidId}/reject")
    public ResponseEntity<Void> rejectBid(@PathVariable UUID bidId) {
        bidService.rejectBid(bidId);
        return ResponseEntity.noContent().build();
    }
}
