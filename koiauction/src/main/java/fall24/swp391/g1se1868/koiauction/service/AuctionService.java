package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.Auction;
import fall24.swp391.g1se1868.koiauction.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuctionService {
    @Autowired
    private AuctionRepository auctionRepository;

    public List<Auction> getOnScheduleAuctions() {
        return auctionRepository.findOnScheduleAuctions();
    }

    public List<Auction> getOnGoingAuctions() {
        return auctionRepository.findOngoingAuctions();
    }

    public List<Map<String, Object>> getPastAuctionsWithWinnerName() {
        List<Object[]> results = auctionRepository.findPastAuctionsWithWinnerName();
        List<Map<String, Object>> pastAuctions = new ArrayList<>();

        for (Object[] result : results) {
            Auction auction = (Auction) result[0];
            String winnerName = (String) result[1];

            Map<String, Object> auctionData = new HashMap<>();
            auctionData.put("auction", auction);
            auctionData.put("winnerName", winnerName);
            pastAuctions.add(auctionData);
        }

        return pastAuctions;
    }


}
