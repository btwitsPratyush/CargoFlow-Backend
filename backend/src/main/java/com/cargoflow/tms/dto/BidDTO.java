package com.cargoflow.tms.dto;

import com.cargoflow.tms.entity.BidStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

public class BidDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "Load ID is required")
        private UUID loadId;

        @NotNull(message = "Transporter ID is required")
        private UUID transporterId;

        @NotNull(message = "Proposed rate is required")
        @Positive(message = "Proposed rate must be positive")
        private Double proposedRate;

        @NotNull(message = "Trucks offered is required")
        @Positive(message = "Trucks offered must be positive")
        private int trucksOffered;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID bidId;
        private UUID loadId;
        private UUID transporterId;
        private Double proposedRate;
        private int trucksOffered;
        private BidStatus status;
        private Timestamp submittedAt;
        private Double score; // For best bid display
    }
}
