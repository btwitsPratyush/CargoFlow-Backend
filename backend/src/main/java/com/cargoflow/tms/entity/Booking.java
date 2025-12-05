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
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_load_transporter_status", columnList = "load_id, transporter_id, status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "load_id", nullable = false)
    private Load load;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid_id", nullable = false)
    private Bid bid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id", nullable = false)
    private Transporter transporter;

    @Column(nullable = false)
    private int allocatedTrucks;

    @Column(nullable = false)
    private Double finalRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp bookedAt;
}
