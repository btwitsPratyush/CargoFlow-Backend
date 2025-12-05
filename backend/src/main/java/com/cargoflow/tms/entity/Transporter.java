package com.cargoflow.tms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "transporters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transporter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transporterId;

    @Column(nullable = false, unique = true)
    private String companyName;

    private Double rating;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "transporter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TruckCapacity> truckCapacities;

    @OneToMany(mappedBy = "transporter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bid> bids;

    @OneToMany(mappedBy = "transporter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
}
