//package fall24.swp391.g1se1868.koiauction.scheduler;
//
//import fall24.swp391.g1se1868.koiauction.service.AuctionService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AuctionScheduler {
//    @Autowired
//    private AuctionService auctionService;
//
//    @Scheduled(fixedRate = 60000)
//    public void updateAuctionStatus() {
//        auctionService.updateAuctionStatusOngoing();
//    }
//}