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
}

