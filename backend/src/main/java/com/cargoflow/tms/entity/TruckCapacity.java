package com.cargoflow.tms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "truck_capacities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TruckCapacity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID capacityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id", nullable = false)
    private Transporter transporter;

    @Column(nullable = false)
    private String truckType;

    @Column(nullable = false)
    private int availableCount;
}
