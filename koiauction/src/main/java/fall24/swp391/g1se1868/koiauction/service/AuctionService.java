package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.Auction;
import fall24.swp391.g1se1868.koiauction.model.AuctionParticipant;
import fall24.swp391.g1se1868.koiauction.repository.AuctionParticipantRepository;
import fall24.swp391.g1se1868.koiauction.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private AuctionParticipantRepository auctionParticipantRepository;
    @Autowired
    JwtService jwtService;

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
    public List<Auction> getAuctionsParticipantByUser(String token){
        Integer userId = jwtService.getUserIdFromToken(token);
        List<Integer> auctionIds = auctionParticipantRepository.findAuctionIdsByUserId(userId);
        return auctionParticipantRepository.findAuctionsByIds(auctionIds);
    }
    }



