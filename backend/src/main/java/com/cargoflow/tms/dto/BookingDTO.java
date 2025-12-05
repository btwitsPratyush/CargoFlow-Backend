package com.cargoflow.tms.dto;

import com.cargoflow.tms.entity.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

public class BookingDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "Bid ID is required")
        private UUID bidId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID bookingId;
        private UUID loadId;
        private UUID bidId;
        private UUID transporterId;
        private int allocatedTrucks;
        private Double finalRate;
        private BookingStatus status;
        private Timestamp bookedAt;
    }
}
