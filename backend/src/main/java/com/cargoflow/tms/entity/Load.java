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
@Table(name = "loads")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Load {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID loadId;

    @Column(nullable = false)
    private String shipperId;

    @Column(nullable = false)
    private String loadingCity;

    @Column(nullable = false)
    private String unloadingCity;

    @Column(nullable = false)
    private Timestamp loadingDate;

    @Column(nullable = false)
    private String productType;

    @Column(nullable = false)
    private Double weight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeightUnit weightUnit;

    @Column(nullable = false)
    private String truckType;

    @Column(nullable = false)
    private int noOfTrucks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoadStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp datePosted;

    @Version
    private Long version;

    @OneToMany(mappedBy = "load", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bid> bids;

    @OneToMany(mappedBy = "load", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
}
