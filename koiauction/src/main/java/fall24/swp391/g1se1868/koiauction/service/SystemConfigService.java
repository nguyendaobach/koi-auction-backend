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
    public Double getWithdrawFree() {
        return systemConfigRepository.findByName("Withdraw Free")
                .map(SystemConfig::getValue)
                .orElseThrow(() -> new RuntimeException("Withdraw Free not found"));
    }

    public void setWithdrawFree(Double value) {
        SystemConfig withdrawFree = systemConfigRepository.findByName("Withdraw Free")
                .orElse(new SystemConfig());
        withdrawFree.setName("Withdraw Free");
        withdrawFree.setValue(value);
        systemConfigRepository.save(withdrawFree);
    }

    // Get và set cho Withdraw Fee Min
    public Double getWithdrawFeeMin() {
        return systemConfigRepository.findByName("Withdraw Fee Min")
                .map(SystemConfig::getValue)
                .orElseThrow(() -> new RuntimeException("Withdraw Fee Min not found"));
    }

    public void setWithdrawFeeMin(Double value) {
        SystemConfig withdrawFeeMin = systemConfigRepository.findByName("Withdraw Fee Min")
                .orElse(new SystemConfig());
        withdrawFeeMin.setName("Withdraw Fee Min");
        withdrawFeeMin.setValue(value);
        systemConfigRepository.save(withdrawFeeMin);
    }
}

