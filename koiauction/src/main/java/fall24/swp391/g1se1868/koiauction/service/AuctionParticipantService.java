package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.Auction;
import fall24.swp391.g1se1868.koiauction.model.AuctionParticipant;
import fall24.swp391.g1se1868.koiauction.model.Transaction;
import fall24.swp391.g1se1868.koiauction.model.Wallet;
import fall24.swp391.g1se1868.koiauction.repository.AuctionParticipantRepository;
import fall24.swp391.g1se1868.koiauction.repository.AuctionRepository;
import jakarta.transaction.Transactional;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuctionParticipantService {
    @Autowired
    private AuctionParticipantRepository auctionParticipantRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private WalletService walletService; @Transactional
    public String registerForAuction(Integer userId, Integer auctionId) {
        if (auctionParticipantRepository.existsByUserIdAndAuctionId(userId, auctionId)) {
            return "You are already registered for this auction.";
        }
        Wallet userWallet = walletService.getWalletByUserId(userId);
        Auction activeAuction = auctionRepository.findById(auctionId).get();
        if (userWallet.getAmount() < activeAuction.getBidderDeposit()) {
            return "Insufficient balance for deposit.";
        }
        Transaction tr = walletService.deposit(userId, activeAuction.getBidderDeposit());
        AuctionParticipant auctionParticipant = new AuctionParticipant();
        auctionParticipant.setUserID(userId);
        auctionParticipant.setAuctionID(auctionId);
        auctionParticipant.setParticipantAuctionDate(Instant.now());
        auctionParticipant.setTransactionID(tr.getId());
        auctionParticipantRepository.save(auctionParticipant);
        return "Registration successful!";
    }
}
