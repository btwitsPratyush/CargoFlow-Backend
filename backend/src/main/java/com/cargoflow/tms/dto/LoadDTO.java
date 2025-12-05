package com.cargoflow.tms.dto;

import com.cargoflow.tms.entity.LoadStatus;
import com.cargoflow.tms.entity.WeightUnit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

public class LoadDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Shipper ID is required")
        private String shipperId;

        @NotBlank(message = "Loading city is required")
        private String loadingCity;

        @NotBlank(message = "Unloading city is required")
        private String unloadingCity;

        @NotNull(message = "Loading date is required")
        private Timestamp loadingDate;

        @NotBlank(message = "Product type is required")
        private String productType;

        @NotNull(message = "Weight is required")
        @Positive(message = "Weight must be positive")
        private Double weight;

        @NotNull(message = "Weight unit is required")
        private WeightUnit weightUnit;

        @NotBlank(message = "Truck type is required")
        private String truckType;

        @NotNull(message = "Number of trucks is required")
        @Positive(message = "Number of trucks must be positive")
        private int noOfTrucks;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID loadId;
        private String shipperId;
        private String loadingCity;
        private String unloadingCity;
        private Timestamp loadingDate;
        private String productType;
        private Double weight;
        private WeightUnit weightUnit;
        private String truckType;
        private int noOfTrucks;
        private LoadStatus status;
        private Timestamp datePosted;
    }
}
