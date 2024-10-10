package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.repository.AuctionKoiRepository;
import fall24.swp391.g1se1868.koiauction.repository.AuctionParticipantRepository;
import fall24.swp391.g1se1868.koiauction.repository.AuctionRepository;
import jakarta.transaction.Transactional;
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

    @Autowired
    private AuctionKoiRepository auctionKoiRepository;

    @Autowired
    private AuctionParticipantRepository auctionParticipantRepository;

    public List<AuctionWithKoi> getAllAuctionsWithKoi() {
        List<Auction> auctions = auctionRepository.findAll();
        return convertToAuctionWithKoi(auctions);
    }

    public AuctionWithKoi getAuctionWithKoiByID(Integer id) {
        Auction auction = auctionRepository.getById(id);
        List<KoiFish> koiFishList = auctionKoiRepository.findKoiFishByAuctionId(id);
        return new AuctionWithKoi(auction, koiFishList);
    }

    public List<AuctionWithKoi> getOnScheduleAuctionsWithKoi() {
        List<Auction> auctions = auctionRepository.findOnScheduleAuctions();
        return convertToAuctionWithKoi(auctions);
    }

    public List<AuctionWithKoi> getOnGoingAuctionsWithKoi() {
        List<Auction> auctions = auctionRepository.findOngoingAuctions();
        return convertToAuctionWithKoi(auctions);
    }


    private List<AuctionWithKoi> convertToAuctionWithKoi(List<Auction> auctions) {
        List<AuctionWithKoi> auctionWithKoiList = new ArrayList<>();
        for (Auction auction : auctions) {
            List<KoiFish> koiFishList = auctionKoiRepository.findKoiFishByAuctionId(auction.getId());
            auctionWithKoiList.add(new AuctionWithKoi(auction, koiFishList));
        }
        return auctionWithKoiList;
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

    public List<AuctionWithKoi> getAuctionsParticipantByUser(int UserID){
        List<Integer> auctionIds = auctionParticipantRepository.findAuctionIdsByUserId(UserID);
        return convertToAuctionWithKoi(auctionParticipantRepository.findAuctionsByIds(auctionIds));
    }

    public List<AuctionWithKoi> getWinnerAuctionByWinnerID(int WinnerID){
        return convertToAuctionWithKoi(auctionRepository.getAuctionbyWinnerID(WinnerID));
    }

    public boolean isUserParticipantForAuction(int userId, int auctionId) {
        return auctionParticipantRepository.existsByUserIdAndAuctionId(userId, auctionId);
    }
    @Transactional
    public String addAuction(AuctionRequest request,int breerderID) {

        Auction auction = new Auction();
        auction.setBreederID(breerderID);
        auction.setAuctionMethod(request.getAuctionMethod());
        auction.setStartTime(request.getStartTime());
        auction.setEndTime(request.getEndTime());
        auction.setStartingPrice(request.getStartingPrice());
        auction.setBuyoutPrice(request.getBuyoutPrice());
        auction.setBidderDeposit(request.getStartingPrice()*10/100);
        auction.setBreederDeposit(request.getStartingPrice()*20/100);
        auction.setAuctionFee(500000L);
        auction.setBidStep(request.getBidStep());
        auction.setStatus("Pending");

        Auction savedAuction = auctionRepository.save(auction);

        for (Integer koiId : request.getKoiIds()) {
            AuctionKoi auctionKoi = new AuctionKoi();
            AuctionKoiId auctionKoiId = new AuctionKoiId();

            auctionKoiId.setAuctionID(savedAuction.getId());
            auctionKoiId.setKoiID(koiId);

            auctionKoi.setId(auctionKoiId);
            auctionKoi.setAuctionID(savedAuction);
            auctionKoi.setKoiID(new KoiFish(koiId));
            auctionKoiRepository.save(auctionKoi);
        }

        return savedAuction!=null?"Add Auction Successfully":"Add Auction Failed";
    }
    public List<AuctionWithKoi> getAllActionRequest(){
        List<Auction> list = auctionRepository.getAllAuctionRequest();
        return convertToAuctionWithKoi(list);
    }

    public Auction approveAuction(Integer auctionId, Integer UserID) {
        Auction auction = auctionRepository.getById(auctionId);
        if (!auction.getStatus().equals("Pending")) {
            throw new IllegalArgumentException("Auction must be in Pending status to be approved.");
        }
        auction.setStatus("Scheduled");
        auction.setStaffID(UserID);
        return auctionRepository.save(auction);
    }
    public Auction rejectAuction(Integer auctionId, Integer UserID) {
        Auction auction = auctionRepository.getById(auctionId);
        if (!auction.getStatus().equals("Pending")) {
            throw new IllegalArgumentException("Auction must be in Pending status to be approved.");
        }
        auction.setStatus("Reject");
        auction.setStaffID(UserID);
        return auctionRepository.save(auction);
    }
}
