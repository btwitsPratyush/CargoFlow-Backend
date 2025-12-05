package com.cargoflow.tms.service;

import com.cargoflow.tms.dto.BookingDTO;

import java.util.UUID;

public interface BookingService {
    BookingDTO.Response createBooking(BookingDTO.CreateRequest request);
    BookingDTO.Response getBooking(UUID bookingId);
    void cancelBooking(UUID bookingId);
}
