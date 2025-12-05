package com.cargoflow.tms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "bids", indexes = {
    @Index(name = "idx_bid_load_transporter_status", columnList = "load_id, transporter_id, status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bidId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "load_id", nullable = false)
    private Load load;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id", nullable = false)
    private Transporter transporter;

    @Column(nullable = false)
    private Double proposedRate;

    @Column(nullable = false)
    private int trucksOffered;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp submittedAt;
}
