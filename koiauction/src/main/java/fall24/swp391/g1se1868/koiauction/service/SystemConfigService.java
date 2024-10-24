package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.SystemConfig;
import fall24.swp391.g1se1868.koiauction.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SystemConfigService {

    private static final String BREEDER_DEPOSIT = "Breeder Deposit";
    private static final String AUCTION_FEE = "Auction Fee";

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    public Double getBreederDeposit() {
        return systemConfigRepository.findByName(BREEDER_DEPOSIT)
                .map(SystemConfig::getValue)
                .orElseThrow(() -> new RuntimeException("Breeder Deposit not found"));
    }

    public Double getAuctionFee() {
        return systemConfigRepository.findByName(AUCTION_FEE)
                .map(SystemConfig::getValue)
                .orElseThrow(() -> new RuntimeException("Auction Fee not found"));
    }

    public void setBreederDeposit(Double value) {
        SystemConfig breederDeposit = systemConfigRepository.findByName(BREEDER_DEPOSIT)
                .orElse(new SystemConfig());
        breederDeposit.setName(BREEDER_DEPOSIT);
        breederDeposit.setValue(value);
        systemConfigRepository.save(breederDeposit);
    }

    public void setAuctionFee(Double value) {
        SystemConfig auctionFee = systemConfigRepository.findByName(AUCTION_FEE)
                .orElse(new SystemConfig());
        auctionFee.setName(AUCTION_FEE);
        auctionFee.setValue(value);
        systemConfigRepository.save(auctionFee);
    }
}

