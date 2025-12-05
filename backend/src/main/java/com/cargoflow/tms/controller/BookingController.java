package com.cargoflow.tms.controller;

import com.cargoflow.tms.dto.BookingDTO;
import com.cargoflow.tms.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDTO.Response> createBooking(@Valid @RequestBody BookingDTO.CreateRequest request) {
        return new ResponseEntity<>(bookingService.createBooking(request), HttpStatus.CREATED);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDTO.Response> getBooking(@PathVariable UUID bookingId) {
        return ResponseEntity.ok(bookingService.getBooking(bookingId));
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}
