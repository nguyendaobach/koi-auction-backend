package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.config.VNPayConfig;
import fall24.swp391.g1se1868.koiauction.model.StringResponse;
import fall24.swp391.g1se1868.koiauction.model.Transaction;
import fall24.swp391.g1se1868.koiauction.model.UserPrinciple;
import fall24.swp391.g1se1868.koiauction.model.Wallet;
import fall24.swp391.g1se1868.koiauction.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;
    @Autowired
    private VNPayConfig VNPayConfig;

    @GetMapping("/get-wallet")
    public ResponseEntity<Wallet> getWalletByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        Wallet wallet = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/add-funds")
    public ResponseEntity<StringResponse> addFunds(@RequestParam("amount") Long amountStr,
                                                   @RequestParam("callbackUrl") String callbackUrl) {
        try {
            // Lấy user đã được xác thực
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("User is not authenticated");
            }
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            int userId = userPrinciple.getId();

            // Tính toán và xác thực số tiền
            long amount = amountStr * 100;

            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String orderType = "billpayment";
            String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
            String vnp_TmnCode = VNPayConfig.getVnp_TmnCode();

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Nạp tiền vào ví: " + vnp_TxnRef);
            vnp_Params.put("vnp_OrderType", orderType);
            vnp_Params.put("vnp_Locale", "vn");

            // Thêm callbackUrl vào VNPay
            vnp_Params.put("vnp_ReturnUrl", VNPayConfig.getVnp_ReturnUrl() + "?userId=" + userId + "&callbackUrl=" + URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8));
            vnp_Params.put("vnp_IpAddr", "127.0.0.1");

            ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
            ZonedDateTime now = ZonedDateTime.now(vietnamZone);
            String vnp_CreateDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            String vnp_ExpireDate = now.plusMinutes(15).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            // Tạo URL thanh toán
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String queryUrl = query.toString();
            String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.getSecretKey(), hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = VNPayConfig.getVnp_PayUrl() + "?" + queryUrl;

            return ResponseEntity.ok(new StringResponse(paymentUrl));

        } catch (Exception e) {
            // Log lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StringResponse("An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/vnpay_return")
    public void handleVNPayReturn(
            @RequestParam Map<String, String> params,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String callbackUrl,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_TxnRef = params.get("vnp_TxnRef");
        String vnp_Amount = params.get("vnp_Amount");

        boolean isSuccess = "00".equals(vnp_ResponseCode);

        try {
            if (isSuccess && userId != null) {
                walletService.addFunds(Integer.parseInt(userId), Long.parseLong(vnp_Amount) / 100);
            }
            String redirectUrl;
            if (callbackUrl != null && !callbackUrl.isEmpty()) {
                if (!callbackUrl.startsWith("http://") && !callbackUrl.startsWith("https://")) {
                    callbackUrl = "http://" + callbackUrl;
                }
                redirectUrl = UriComponentsBuilder.fromUriString(callbackUrl)
                        .queryParam("success", isSuccess)
                        .queryParam("txnRef", vnp_TxnRef)
                        .queryParam("amount", Long.parseLong(vnp_Amount) / 100)
                        .toUriString();
            } else {
                redirectUrl = UriComponentsBuilder.fromUriString("/api/payment/result")
                        .queryParam("success", isSuccess)
                        .queryParam("txnRef", vnp_TxnRef)
                        .queryParam("amount", Long.parseLong(vnp_Amount) / 100)
                        .toUriString();
            }
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            String redirectUrl;
            redirectUrl = UriComponentsBuilder.fromUriString("/api/payment/result")
                    .queryParam("success", false)
                    .queryParam("txnRef", vnp_TxnRef)
                    .queryParam("amount", Long.parseLong(vnp_Amount) / 100)
                    .toUriString();
            response.sendRedirect(redirectUrl);
        }
    }

    @GetMapping("/result")
    public String paymentResult(@RequestParam boolean success,
                                @RequestParam String txnRef,
                                @RequestParam long amount,
                                Model model) {
        model.addAttribute("success", success);
        model.addAttribute("txnRef", txnRef);
        model.addAttribute("amount", amount);
        return "payment-result";  // Trả về tên của view template
    }

    @PostMapping("/payment")
    public ResponseEntity<StringResponse> makePayment( @RequestParam Integer auctionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        try {
            String response = walletService.paymentforAuction(userId, auctionId);
            return ResponseEntity.ok(new StringResponse(response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(new StringResponse(e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StringResponse(e.getMessage()));
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        List<Transaction> transactions = walletService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }
    @GetMapping("/transactions/{id}")
    public ResponseEntity<Transaction> getTransactionsByID(@PathVariable Integer id) {
        Transaction transactions = walletService.getTransactionById(id);
        return ResponseEntity.ok(transactions);
    }
    @GetMapping("/checkPaymentStatus/{auctionId}")
    public ResponseEntity<String> checkPaymentStatus(@PathVariable Integer auctionId) {
        boolean isPaid = walletService.isAuctionPaid(auctionId);
        if (isPaid) {
            return ResponseEntity.ok("Auction has been paid.");
        } else {
            return ResponseEntity.ok("Auction has not been paid.");
        }
    }
    @PostMapping("/withdraw")
    public ResponseEntity<String> sendWithdrawRequest( @RequestParam Long amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        try {
            walletService.sendWithdrawRequest(userId, amount);
            return ResponseEntity.ok("Withdrawal request sent successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Complete Withdraw with Fee Deduction
    @PostMapping("/withdraw/complete/{transactionId}")
    public ResponseEntity<String> completeWithdraw(@PathVariable Integer transactionId) {
        try {
            walletService.completeWithdraw(transactionId);
            return ResponseEntity.ok("Withdrawal completed successfully with applicable fees.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. Get Withdraw Requests by Status
    @GetMapping("/withdraw/status")
    public ResponseEntity<List<Transaction>> getWithdrawRequests(@RequestParam String status) {
        try {
            List<Transaction> transactions = walletService.getWithdrawRequests(status);
            return ResponseEntity.ok(transactions);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
