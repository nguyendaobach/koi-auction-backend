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
        // Kiểm tra nếu người dùng đã đăng ký cho phiên đấu giá này
        if (auctionParticipantRepository.existsByUserIdAndAuctionId(userId, auctionId)) {
            return "You are already registered for this auction.";
        }
        Wallet userWallet = walletService.getWalletByUserId(userId);
        Auction activeAuction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        if (userWallet.getAmount() < activeAuction.getBidderDeposit()) {
            return "Insufficient balance for deposit.";
        }
        Transaction transaction = walletService.deposit(userId, activeAuction.getBidderDeposit());
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

}
