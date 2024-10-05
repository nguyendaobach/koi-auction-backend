package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.Auction;
import fall24.swp391.g1se1868.koiauction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auction")
public class AuctionController {
    @Autowired
    private AuctionService auctionService;

    @GetMapping("/on-schedule")
    public List<Auction> getOnScheduleAuctions() {
        return auctionService.getOnScheduleAuctions();
    }

    @GetMapping("/on-going")
    public List<Auction> getOngoingAuctions() {
        return auctionService.getOnGoingAuctions();
    }
    
    @GetMapping("/past")
    public List<Map<String, Object>> getPastAuctionsWithWinnerName() {
        return auctionService.getPastAuctionsWithWinnerName();
    }

}
