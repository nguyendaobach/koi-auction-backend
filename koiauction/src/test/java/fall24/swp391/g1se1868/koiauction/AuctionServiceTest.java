package fall24.swp391.g1se1868.koiauction;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.repository.AuctionKoiRepository;
import fall24.swp391.g1se1868.koiauction.repository.AuctionParticipantRepository;
import fall24.swp391.g1se1868.koiauction.repository.AuctionRepository;
import fall24.swp391.g1se1868.koiauction.repository.KoiFishRepository;
import fall24.swp391.g1se1868.koiauction.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.SchedulerException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuctionServiceTest {

    @InjectMocks
    private AuctionService auctionService;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AuctionKoiRepository auctionKoiRepository;

    @Mock
    private AuctionParticipantRepository auctionParticipantRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private KoiFishRepository koiFishRepository;

    @Mock
    private SystemConfigService systemConfigService;

    @Mock
    private AuctionSchedulerService auctionSchedulerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddAuction_Success() {
        AuctionRequest request = new AuctionRequest();
        request.setAuctionMethod("Ascending");
        request.setStartTime(Instant.now().plusSeconds(86400));
        request.setEndTime(Instant.now().plusSeconds(172800));
        request.setStartingPrice(1000000L);
        request.setBuyoutPrice(50000000L);
        request.setBidderDeposit(100000L);
        request.setBidStep(500000L);
        request.setKoiIds(List.of(1, 2, 3));

        when(systemConfigService.getBreederDeposit()).thenReturn(0.1);
        when(systemConfigService.getAuctionFee()).thenReturn(200000.00);
        when(auctionRepository.save(any(Auction.class))).thenAnswer(invocation -> {
            Auction auction = invocation.getArgument(0);
            auction.setId(1);
            return auction;
        });
        KoiFish koi1 = new KoiFish();
        koi1.setId(1);
        koi1.setStatus("Active");

        KoiFish koi2 = new KoiFish();
        koi2.setId(2);
        koi2.setStatus("Active");

        KoiFish koi3 = new KoiFish();
        koi3.setId(3);
        koi3.setStatus("Active");

        when(koiFishRepository.findById(1)).thenReturn(Optional.of(koi1));
        when(koiFishRepository.findById(2)).thenReturn(Optional.of(koi2));
        when(koiFishRepository.findById(3)).thenReturn(Optional.of(koi3));

        Auction savedAuction = auctionService.addAuction(request, 1);

        assertNotNull(savedAuction);
        assertEquals("Pending", savedAuction.getStatus());
        verify(walletService).deposit(eq(1), eq(100000L), eq(savedAuction.getId()));
        try {
            verify(auctionSchedulerService).scheduleStartAuction(eq(savedAuction.getId()), any());
            verify(auctionSchedulerService).scheduleCloseAuction(eq(savedAuction.getId()), any());
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        verify(auctionKoiRepository, times(3)).save(any(AuctionKoi.class));
    }

    @Test
    void testAddAuction_Fail_LowStartingPrice() {
        AuctionRequest request = new AuctionRequest();
        request.setStartingPrice(0L);
        request.setAuctionMethod("Ascending");

        assertThrows(RuntimeException.class, () -> auctionService.addAuction(request, 1));
    }

    @Test
    void testAddAuction_Fail_InvalidTime() {
        AuctionRequest request = new AuctionRequest();
        request.setAuctionMethod("Ascending");
        request.setStartTime(Instant.now().minusSeconds(86400)); // Thời gian bắt đầu đã qua
        request.setEndTime(Instant.now().plusSeconds(86400));

        assertThrows(RuntimeException.class, () -> auctionService.addAuction(request, 1));
    }

    @Test
    void testAddAuction_Fail_NonActiveKoi() {
        AuctionRequest request = new AuctionRequest();
        request.setAuctionMethod("Ascending");
        request.setStartTime(Instant.now().plusSeconds(86400));
        request.setEndTime(Instant.now().plusSeconds(172800));
        request.setStartingPrice(1000L);
        request.setKoiIds(List.of(1));

        KoiFish inactiveKoi = new KoiFish();
        inactiveKoi.setStatus("Inactive"); // Koi không hoạt động

        when(systemConfigService.getBreederDeposit()).thenReturn(0.1);
        when(systemConfigService.getAuctionFee()).thenReturn(200000.0);
        when(auctionRepository.save(any(Auction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(koiFishRepository.findById(1)).thenReturn(Optional.of(inactiveKoi));

        assertThrows(RuntimeException.class, () -> auctionService.addAuction(request, 1));
    }

    @Test
    void testAddAuction_Fail_MissingKoi() {
        AuctionRequest request = new AuctionRequest();
        request.setAuctionMethod("Ascending");
        request.setStartTime(Instant.now().plusSeconds(86400));
        request.setEndTime(Instant.now().plusSeconds(172800));
        request.setStartingPrice(1000L);
        request.setBuyoutPrice(5000L);
        request.setBidderDeposit(100L);
        request.setBidStep(50L);
        request.setKoiIds(List.of(1, 2, 3));

        when(systemConfigService.getBreederDeposit()).thenReturn(0.1);
        when(systemConfigService.getAuctionFee()).thenReturn(200000.00);
        when(koiFishRepository.findById(anyInt())).thenReturn(Optional.empty());

        Auction savedAuctionMock = new Auction();
        savedAuctionMock.setId(1); // Đảm bảo có ID
        when(auctionRepository.save(any(Auction.class))).thenReturn(savedAuctionMock);

        assertThrows(IllegalArgumentException.class, () -> auctionService.addAuction(request, 1));
    }



    @Test
    void testAddAuction_Fail_NullFields() {
        AuctionRequest request = new AuctionRequest();
        request.setAuctionMethod(null); // Trường AuctionMethod null
        request.setStartTime(null); // Trường StartTime null

        assertThrows(NullPointerException.class, () -> auctionService.addAuction(request, 1));
    }

    @Test
    void testAddAuction_Fail_InvalidEndTime() {
        AuctionRequest request = new AuctionRequest();
        request.setAuctionMethod("Ascending");
        request.setStartTime(Instant.now().plusSeconds(86400));
        request.setEndTime(Instant.now().minusSeconds(3600)); // EndTime trước StartTime

        assertThrows(RuntimeException.class, () -> auctionService.addAuction(request, 1));
    }

    @Test
    void testUpdateAuction_Success_PendingStatus() {
        // Mock auction object with ID and status "Pending"
        Auction auction = new Auction();
        auction.setId(1);
        auction.setStatus("Pending");
        auction.setBreederID(1);

        AuctionRequest request = new AuctionRequest();
        request.setAuctionMethod("Ascending");
        request.setStartTime(Instant.now().plusSeconds(86400));
        request.setEndTime(Instant.now().plusSeconds(172800));
        request.setStartingPrice(1500L);
        request.setBuyoutPrice(5000L);
        request.setBidderDeposit(150L);
        request.setBidStep(50L);
        request.setKoiIds(List.of(1, 2, 3));
        when(auctionRepository.findById(1)).thenReturn(Optional.of(auction));
        when(systemConfigService.getBreederDeposit()).thenReturn(0.1);
        when(systemConfigService.getAuctionFee()).thenReturn(200000.00);
        when(auctionRepository.save(any(Auction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Auction updatedAuction = auctionService.updateAuction(1, request, 1);
        assertNotNull(updatedAuction, "Updated auction should not be null");
        assertEquals("Pending", updatedAuction.getStatus());
        verify(auctionKoiRepository, times(3)).save(any(AuctionKoi.class));
    }



    @Test
    void testUpdateAuction_Fail_NoPermission() {
        Auction auction = new Auction();
        auction.setId(1);
        auction.setBreederID(1);
        auction.setStatus("Pending");

        AuctionRequest request = new AuctionRequest();
        request.setAuctionMethod("Descending");

        when(auctionRepository.findById(1)).thenReturn(Optional.of(auction));

        assertThrows(RuntimeException.class, () -> auctionService.updateAuction(1, request, 2));
    }

    @Test
    void testUpdateAuction_Fail_InvalidStatus() {
        Auction auction = new Auction();
        auction.setId(1);
        auction.setBreederID(1);
        auction.setStatus("Closed");

        AuctionRequest request = new AuctionRequest();
        request.setAuctionMethod("Ascending");

        when(auctionRepository.findById(1)).thenReturn(Optional.of(auction));

        assertThrows(RuntimeException.class, () -> auctionService.updateAuction(1, request, 1));
    }

    @Test
    void testCancelAuction_Success_PendingStatus() {
        Auction auction = new Auction();
        auction.setId(1);
        auction.setBreederID(1);
        auction.setStatus("Pending");
        auction.setBreederDeposit(500L);

        when(auctionRepository.findById(1)).thenReturn(Optional.of(auction));

        auctionService.cancelAuction(1, 1);

        assertEquals("Cancelled", auction.getStatus());
        verify(walletService).refundDeposit(1, 500L, 1);
        verify(auctionRepository).save(auction);
    }

    @Test
    void testCancelAuction_Success_ScheduledNoParticipants() {
        Auction auction = new Auction();
        auction.setId(1);
        auction.setBreederID(1);
        auction.setStatus("Scheduled");
        auction.setBreederDeposit(500L);

        when(auctionRepository.findById(1)).thenReturn(Optional.of(auction));
        when(auctionParticipantRepository.findAuctionParticipantsByAuctionID(1)).thenReturn(Collections.emptyList());

        auctionService.cancelAuction(1, 1);

        assertEquals("Cancelled", auction.getStatus());
        verify(walletService).refundDeposit(1, 500L, 1);
    }

    @Test
    void testCancelAuction_Fail_WithParticipants() {
        Auction auction = new Auction();
        auction.setId(1);
        auction.setBreederID(1);
        auction.setStatus("Scheduled");

        when(auctionRepository.findById(1)).thenReturn(Optional.of(auction));
        when(auctionParticipantRepository.findAuctionParticipantsByAuctionID(1))
                .thenReturn(List.of(new AuctionParticipant()));

        assertThrows(IllegalArgumentException.class, () -> auctionService.cancelAuction(1, 1));
    }

    @Test
    void testCancelAuction_Fail_InvalidStatus() {
        Auction auction = new Auction();
        auction.setId(1);
        auction.setBreederID(1);
        auction.setStatus("Ongoing");

        when(auctionRepository.findById(1)).thenReturn(Optional.of(auction));

        assertThrows(IllegalArgumentException.class, () -> auctionService.cancelAuction(1, 1));
    }
}
