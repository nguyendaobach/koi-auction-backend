package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system-config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping("/breeder-deposit")
    public ResponseEntity<Double> getBreederDeposit() {
        Double breederDeposit = systemConfigService.getBreederDeposit();
        return ResponseEntity.ok(breederDeposit);
    }

    @GetMapping("/auction-fee")
    public ResponseEntity<Double> getAuctionFee() {
        Double auctionFee = systemConfigService.getAuctionFee();
        return ResponseEntity.ok(auctionFee);
    }

    @PutMapping("/breeder-deposit")
    public ResponseEntity<Void> updateBreederDeposit(@RequestParam Double value) {
        systemConfigService.setBreederDeposit(value);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/auction-fee")
    public ResponseEntity<Void> updateAuctionFee(@RequestParam Double value) {
        systemConfigService.setAuctionFee(value);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/withdraw-free")
    public ResponseEntity<Double> getWithdrawFree() {
        Double withdrawFree = systemConfigService.getWithdrawFree();
        return ResponseEntity.ok(withdrawFree);
    }

    @PutMapping("/withdraw-free")
    public ResponseEntity<Void> updateWithdrawFree(@RequestParam Double value) {
        systemConfigService.setWithdrawFree(value);
        return ResponseEntity.ok().build();
    }

    // Get và update cho Withdraw Fee Min
    @GetMapping("/withdraw-fee-min")
    public ResponseEntity<Double> getWithdrawFeeMin() {
        Double withdrawFeeMin = systemConfigService.getWithdrawFeeMin();
        return ResponseEntity.ok(withdrawFeeMin);
    }

    @PutMapping("/withdraw-fee-min")
    public ResponseEntity<Void> updateWithdrawFeeMin(@RequestParam Double value) {
        systemConfigService.setWithdrawFeeMin(value);
        return ResponseEntity.ok().build();
    }
}

