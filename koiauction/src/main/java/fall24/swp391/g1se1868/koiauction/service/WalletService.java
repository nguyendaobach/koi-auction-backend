package fall24.swp391.g1se1868.koiauction.service;


import fall24.swp391.g1se1868.koiauction.model.Auction;
import fall24.swp391.g1se1868.koiauction.model.Transaction;
import fall24.swp391.g1se1868.koiauction.model.Wallet;
import fall24.swp391.g1se1868.koiauction.repository.TransactionRepository;
import fall24.swp391.g1se1868.koiauction.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private SystemConfigService systemConfigService;

    @Lazy
    @Autowired
    AuctionService auctionService;

    public Wallet getWalletByUserId(Integer userId) {
        return walletRepository.findbyuserid(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user ID: " + userId));
    }

    public void addFunds(Integer userId, Long amount) {
        Wallet wallet = getWalletByUserId(userId);
        wallet.setAmount(wallet.getAmount() + amount);
        walletRepository.save(wallet);
        Transaction transaction = new Transaction();
        transaction.setWalletID(wallet);
        transaction.setAmount(amount);
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(vietnamZone);
        transaction.setTime(now.toInstant());
        transaction.setTransactionType("Top-up");
        transaction.setStatus("Completed");
        transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByUserId(Integer userId) {
        Wallet wallet = getWalletByUserId(userId);
        return transactionRepository.findByWalletID(wallet.getId());
    }

    @Transactional
    public String paymentforAuction(int payerUserId, Integer auctionId) {
        Wallet payerWallet = walletRepository.findbyuserid(payerUserId)
                .orElseThrow(() -> new RuntimeException("Payer wallet not found"));
        Wallet recipientWallet = walletRepository.findbyuserid(1)
                .orElseThrow(() -> new RuntimeException("Recipient wallet not found"));
        Auction auction = auctionService.getAuctionById(auctionId);
        Long amount = auction.getFinalPrice()-auction.getBidderDeposit();
        if(amount==0||amount==null){
            throw new IllegalArgumentException("Final Price Error");
        }
        if (payerWallet.getAmount().compareTo(amount) < 0) {
            throw new RuntimeException( "Insufficient balance!");
        }
        payerWallet.setAmount(payerWallet.getAmount()-amount);
        walletRepository.save(payerWallet);
        recipientWallet.setAmount(recipientWallet.getAmount()+amount);
        walletRepository.save(recipientWallet);
        Transaction payerTransaction = new Transaction();
        payerTransaction.setWalletID(payerWallet);
        payerTransaction.setAuctionID(auctionId);
        payerTransaction.setAmount(amount);  // Negative for deduction
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(vietnamZone);
        payerTransaction.setTime(now.toInstant());
        payerTransaction.setTransactionType("Payment");
        payerTransaction.setStatus("Completed");
        transactionRepository.save(payerTransaction);
        auctionService.updateAuctionStatusPaid(auctionId);
        return "Payment successful!";
    }

    public boolean isAuctionPaid(Integer auctionId) {
        Optional<Transaction> transaction = transactionRepository
                .findCompletedPaymentTransactionByAuctionId(auctionId);

        return transaction.isPresent();
    }

    @Transactional
    public Transaction deposit(int payerUserId, Long amount, int auctionId) {
        Wallet payerWallet = walletRepository.findbyuserid(payerUserId)
                .orElseThrow(() -> new RuntimeException("Payer wallet not found"));
        Wallet recipientWallet = walletRepository.findbyuserid(1)
                .orElseThrow(() -> new RuntimeException("Recipient wallet not found"));
        if (payerWallet.getAmount().compareTo(amount) < 0) {
            return null;
        }
        payerWallet.setAmount(payerWallet.getAmount()-amount);
        walletRepository.save(payerWallet);
        recipientWallet.setAmount(recipientWallet.getAmount()+amount);
        walletRepository.save(recipientWallet);
        Transaction payerTransaction = new Transaction();
        payerTransaction.setWalletID(payerWallet);
        payerTransaction.setAmount(amount);
        payerTransaction.setAuctionID(auctionId);
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(vietnamZone);
        payerTransaction.setTime(now.toInstant());
        payerTransaction.setTransactionType("Deposit");
        payerTransaction.setStatus("Completed");
        transactionRepository.save(payerTransaction);
        return payerTransaction;
    }
    @Transactional
    public Transaction refund(int payerUserId, Long amount, int auctionId) {
        Wallet payerWallet = walletRepository.findbyuserid(payerUserId)
                .orElseThrow(() -> new RuntimeException("Payer wallet not found"));
        Wallet systemWallet = walletRepository.findbyuserid(1)
                .orElseThrow(() -> new RuntimeException("System wallet not found"));
        if (systemWallet.getAmount().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds in system wallet");
        }
        systemWallet.setAmount(systemWallet.getAmount() - amount);
        walletRepository.save(systemWallet);
        payerWallet.setAmount(payerWallet.getAmount() + amount);
        walletRepository.save(payerWallet);
        Transaction refundTransaction = new Transaction();
        refundTransaction.setWalletID(payerWallet); // Ghi lại giao dịch với ví người dùng
        refundTransaction.setAmount(amount);
        refundTransaction.setAuctionID(auctionId);
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(vietnamZone);
        refundTransaction.setTime(now.toInstant());
        refundTransaction.setTransactionType("Refund");  // Loại giao dịch là hoàn tiền
        refundTransaction.setStatus("Completed");
        transactionRepository.save(refundTransaction);
        return refundTransaction;
    }
    // 1. Send Withdrawal Request
    public void sendWithdrawRequest(Integer userId, Long amount) {
        Wallet wallet = getWalletByUserId(userId);
        Long fee = Math.max(
                Math.round(amount * systemConfigService.getWithdrawFree()),
                systemConfigService.getWithdrawFeeMin().longValue()
        );
        if (wallet.getAmount() < (fee+amount)) {
            throw new RuntimeException("Insufficient funds to cover the withdrawal fee.");
        }
        Wallet systemWallet = walletRepository.findbyuserid(1)
                .orElseThrow(() -> new RuntimeException("System wallet not found"));
        systemWallet.setAmount(systemWallet.getAmount() - amount);
        walletRepository.save(systemWallet);
        wallet.setAmount(wallet.getAmount() - (fee+amount));
        walletRepository.save(wallet);
        Transaction transaction = new Transaction();
        transaction.setWalletID(wallet);
        transaction.setAmount(fee+amount);
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        transaction.setTime(ZonedDateTime.now(vietnamZone).toInstant());
        transaction.setTransactionType("Withdraw");
        transaction.setStatus("Pending");
        transactionRepository.save(transaction);
    }
    public void completeWithdraw(Integer transactionId) {
        Transaction transaction = getTransactionById(transactionId);

        if (!"Pending".equals(transaction.getStatus())) {
            throw new IllegalStateException("Only Pending transactions can be completed.");
        }
        Long withdrawAmount = transaction.getAmount();
        Long fee = Math.max(
                Math.round(withdrawAmount * systemConfigService.getWithdrawFree()),
                systemConfigService.getWithdrawFeeMin().longValue()
        );
        Wallet wallet = transaction.getWalletID();
        if (wallet.getAmount() < (fee+withdrawAmount)) {
            throw new RuntimeException("Insufficient funds to cover the withdrawal fee.");
        }
        transaction.setStatus("Completed");
        transactionRepository.save(transaction);
    }

    public List<Transaction> getWithdrawRequests(String status) {
        return transactionRepository.findByTransactionTypeAndStatus("Withdraw", status);
    }

    public Transaction getTransactionById(Integer transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found"));
    }

    @Transactional
    public void addUserWallet(Integer id){
        Wallet wallet=new Wallet(id,0L);
        walletRepository.save(wallet);
    }

    public static void main(String[] args) {
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(vietnamZone);
        System.out.println("Local time: " + now);
        System.out.println("UTC time: " + now.toInstant());
    }
}

