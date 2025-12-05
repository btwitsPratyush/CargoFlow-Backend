package com.cargoflow.tms.service;

import com.cargoflow.tms.dto.BidDTO;
import com.cargoflow.tms.entity.Bid;
import com.cargoflow.tms.entity.BidStatus;
import com.cargoflow.tms.entity.Load;
import com.cargoflow.tms.entity.Transporter;
import com.cargoflow.tms.repository.BidRepository;
import com.cargoflow.tms.repository.LoadRepository;
import com.cargoflow.tms.service.impl.LoadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoadServiceTest {

    @Mock
    private LoadRepository loadRepository;

    @Mock
    private BidRepository bidRepository;

    @InjectMocks
    private LoadServiceImpl loadService;

    private Load load;
    private Transporter t1;
    private Transporter t2;

    @BeforeEach
    void setUp() {
        load = Load.builder().loadId(UUID.randomUUID()).build();
        t1 = Transporter.builder().transporterId(UUID.randomUUID()).rating(4.0).build();
        t2 = Transporter.builder().transporterId(UUID.randomUUID()).rating(5.0).build();
    }

    @Test
    void testBestBidAlgorithm() {
        // Bid 1: Rate 1000, Rating 4.0
        // Score = (1/1000)*0.7 + (4/5)*0.3 = 0.0007 + 0.24 = 0.2407
        Bid bid1 = Bid.builder()
                .bidId(UUID.randomUUID())
                .load(load)
                .transporter(t1)
                .proposedRate(1000.0)
                .trucksOffered(1)
                .status(BidStatus.PENDING)
                .build();

        // Bid 2: Rate 900, Rating 5.0
        // Score = (1/900)*0.7 + (5/5)*0.3 = 0.000777 + 0.3 = 0.300777
        Bid bid2 = Bid.builder()
                .bidId(UUID.randomUUID())
                .load(load)
                .transporter(t2)
                .proposedRate(900.0)
                .trucksOffered(1)
                .status(BidStatus.PENDING)
                .build();

        when(loadRepository.findById(load.getLoadId())).thenReturn(Optional.of(load));
        when(bidRepository.findByLoad_LoadIdAndStatus(load.getLoadId(), BidStatus.PENDING))
                .thenReturn(Arrays.asList(bid1, bid2));

        List<BidDTO.Response> bestBids = loadService.getBestBids(load.getLoadId());

        assertEquals(2, bestBids.size());
        assertEquals(bid2.getBidId(), bestBids.get(0).getBidId()); // Higher score should be first
        assertEquals(bid1.getBidId(), bestBids.get(1).getBidId());
    }
}
