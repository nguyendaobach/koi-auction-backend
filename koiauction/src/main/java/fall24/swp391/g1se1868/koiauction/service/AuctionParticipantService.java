package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.repository.AuctionParticipantRepository;
import fall24.swp391.g1se1868.koiauction.repository.AuctionRepository;
import jakarta.transaction.Transactional;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AuctionParticipantService {
    @Autowired
    private AuctionParticipantRepository auctionParticipantRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private WalletService walletService;

    @Transactional
    public String registerForAuction(Integer userId, Integer auctionId) {
        if (auctionParticipantRepository.existsByUserIdAndAuctionId(userId, auctionId)) {
            return "You are already registered for this auction.";
        }
        Wallet userWallet = walletService.getWalletByUserId(userId);
        Auction activeAuction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        if (userWallet.getAmount() < activeAuction.getBidderDeposit()) {
            return "Insufficient balance for deposit.";
        }
        if(activeAuction.getBreederID() == userId){
            throw new IllegalArgumentException("This is own's auction cannot register.");
        }
        Transaction transaction = walletService.deposit(userId, activeAuction.getBidderDeposit(),auctionId);
        AuctionParticipant auctionParticipant = new AuctionParticipant();
        AuctionParticipantId participantId = new AuctionParticipantId();
        Auction auction = new Auction(auctionId);
        User user = new User(userId);
        auctionParticipant.setAuctionID(auction);
        auctionParticipant.setUserID(user);
        auctionParticipant.setId(participantId);
        auctionParticipant.setParticipantAuctionDate(Instant.now());
        auctionParticipant.setTransactionID(transaction.getId());
        auctionParticipant.setStatus("Participated");
        auctionParticipantRepository.save(auctionParticipant);
        return "Registration successful!";
    }
    public List<AuctionParticipant> findByAuctionId(Integer auctionId){
        return auctionParticipantRepository.findAuctionParticipantsByAuctionID(auctionId);
    }

}
