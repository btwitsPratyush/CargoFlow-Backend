package com.cargoflow.tms.service.impl;

import com.cargoflow.tms.dto.BookingDTO;
import com.cargoflow.tms.entity.*;
import com.cargoflow.tms.exception.CustomExceptions;
import com.cargoflow.tms.repository.*;
import com.cargoflow.tms.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BidRepository bidRepository;
    private final LoadRepository loadRepository;
    private final TruckCapacityRepository truckCapacityRepository;

    @Override
    @Transactional
    public BookingDTO.Response createBooking(BookingDTO.CreateRequest request) {
        try {
            Bid bid = bidRepository.findById(request.getBidId())
                    .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Bid not found"));

            if (bid.getStatus() != BidStatus.PENDING) {
                throw new CustomExceptions.InvalidStatusTransitionException("Bid is not PENDING");
            }

            Load load = bid.getLoad();
            Transporter transporter = bid.getTransporter();

            // 1. Validate Load Status
            if (load.getStatus() == LoadStatus.BOOKED || load.getStatus() == LoadStatus.CANCELLED) {
                throw new CustomExceptions.LoadAlreadyBookedException("Load is already BOOKED or CANCELLED");
            }

            // 2. Validate Transporter Capacity
            TruckCapacity capacity = truckCapacityRepository.findByTransporterAndTruckType(transporter, load.getTruckType())
                    .orElseThrow(() -> new CustomExceptions.InsufficientCapacityException("Transporter has no capacity record for this truck type"));

            if (capacity.getAvailableCount() < bid.getTrucksOffered()) {
                throw new CustomExceptions.InsufficientCapacityException("Transporter does not have enough available trucks");
            }

            // 3. Calculate Remaining Trucks for Load
            Integer alreadyAllocated = bookingRepository.sumAllocatedTrucksByLoadId(load.getLoadId());
            if (alreadyAllocated == null) alreadyAllocated = 0;
            int remainingTrucks = load.getNoOfTrucks() - alreadyAllocated;

            if (remainingTrucks <= 0) {
                throw new CustomExceptions.LoadAlreadyBookedException("Load is already fully booked");
            }
            
            // If bid offers more than remaining, we can only allocate remaining? 
            // Or should we reject? Requirement says "Allow multiple transporters to book portions of a load".
            // Let's assume we allocate what is offered, but if it exceeds remaining, we cap it?
            // Or maybe the bid should be for exactly what is needed?
            // "AllocatedTrucks" in Booking entity suggests we can allocate specific amount.
            // Let's assume we allocate MIN(bid.trucksOffered, remainingTrucks).
            int trucksToAllocate = Math.min(bid.getTrucksOffered(), remainingTrucks);

            // 4. Create Booking
            Booking booking = Booking.builder()
                    .load(load)
                    .bid(bid)
                    .transporter(transporter)
                    .allocatedTrucks(trucksToAllocate)
                    .finalRate(bid.getProposedRate())
                    .status(BookingStatus.CONFIRMED)
                    .build();

            bookingRepository.save(booking);

            // 5. Update Bid Status
            bid.setStatus(BidStatus.ACCEPTED);
            bidRepository.save(bid);

            // 6. Deduct Capacity
            capacity.setAvailableCount(capacity.getAvailableCount() - trucksToAllocate);
            truckCapacityRepository.save(capacity);

            // 7. Update Load Status if fully booked
            if (remainingTrucks - trucksToAllocate == 0) {
                load.setStatus(LoadStatus.BOOKED);
                loadRepository.save(load);
            }

            return mapToResponse(booking);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomExceptions.LoadAlreadyBookedException("Load was updated by another transaction. Please retry.");
        }
    }

    @Override
    public BookingDTO.Response getBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Booking not found"));
        return mapToResponse(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new CustomExceptions.InvalidStatusTransitionException("Only CONFIRMED bookings can be cancelled");
        }

        // 1. Restore Capacity
        Transporter transporter = booking.getTransporter();
        Load load = booking.getLoad();
        TruckCapacity capacity = truckCapacityRepository.findByTransporterAndTruckType(transporter, load.getTruckType())
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Capacity record not found")); // Should not happen

        capacity.setAvailableCount(capacity.getAvailableCount() + booking.getAllocatedTrucks());
        truckCapacityRepository.save(capacity);

        // 2. Update Booking Status
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // 3. Update Load Status (if it was BOOKED, it might become OPEN_FOR_BIDS again)
        if (load.getStatus() == LoadStatus.BOOKED) {
            load.setStatus(LoadStatus.OPEN_FOR_BIDS);
            loadRepository.save(load);
        }
        
        // 4. Revert Bid Status? 
        // Requirement doesn't explicitly say, but usually if booking is cancelled, bid might go back to PENDING or REJECTED.
        // Or maybe we leave it as ACCEPTED but the booking is cancelled.
        // Let's leave bid as ACCEPTED to show history, or maybe we should set it to REJECTED/CANCELLED?
        // Let's keep it simple and just focus on Booking status.
    }

    private BookingDTO.Response mapToResponse(Booking booking) {
        return BookingDTO.Response.builder()
                .bookingId(booking.getBookingId())
                .loadId(booking.getLoad().getLoadId())
                .bidId(booking.getBid().getBidId())
                .transporterId(booking.getTransporter().getTransporterId())
                .allocatedTrucks(booking.getAllocatedTrucks())
                .finalRate(booking.getFinalRate())
                .status(booking.getStatus())
                .bookedAt(booking.getBookedAt())
                .build();
    }
}
