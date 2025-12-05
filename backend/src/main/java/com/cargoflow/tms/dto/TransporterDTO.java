package com.cargoflow.tms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

public class TransporterDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "Company name is required")
        private String companyName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID transporterId;
        private String companyName;
        private Double rating;
        private Timestamp createdAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCapacityRequest {
        @NotBlank(message = "Truck type is required")
        private String truckType;
        
        private int count; // Can be negative to reduce capacity? Requirement says "Update truck capacity", usually implies setting the new total or adding. Let's assume setting new total available count based on Entity "availableCount".
        // Actually, "Update truck capacity" usually means "I have 5 trucks now".
    }
}
