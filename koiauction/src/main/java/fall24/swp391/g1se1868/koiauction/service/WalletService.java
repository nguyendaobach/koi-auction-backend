package fall24.swp391.g1se1868.koiauction.service;


import fall24.swp391.g1se1868.koiauction.model.Transaction;
import fall24.swp391.g1se1868.koiauction.model.Wallet;
import fall24.swp391.g1se1868.koiauction.repository.TransactionRepository;
import fall24.swp391.g1se1868.koiauction.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

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
        transaction.setTime(Instant.now());
        transaction.setTransactionType("Top-up");
        transaction.setStatus("Completed");
        transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByUserId(Integer userId) {
        Wallet wallet = getWalletByUserId(userId);
        return transactionRepository.findByWalletID(wallet.getId());
    }

    @Transactional
    public String payment(int payerUserId, Long amount) {
        Wallet payerWallet = walletRepository.findbyuserid(payerUserId)
                .orElseThrow(() -> new RuntimeException("Payer wallet not found"));
        Wallet recipientWallet = walletRepository.findbyuserid(1)
                .orElseThrow(() -> new RuntimeException("Recipient wallet not found"));
        if (payerWallet.getAmount().compareTo(amount) < 0) {
            return "Insufficient balance!";
        }
        payerWallet.setAmount(payerWallet.getAmount()-amount);
        walletRepository.save(payerWallet);
        recipientWallet.setAmount(recipientWallet.getAmount()+amount);
        walletRepository.save(recipientWallet);
        Transaction payerTransaction = new Transaction();
        payerTransaction.setWalletID(payerWallet);
        payerTransaction.setAmount(amount);  // Negative for deduction
        payerTransaction.setTime(Instant.now());
        payerTransaction.setTransactionType("Payment");
        payerTransaction.setStatus("Completed");
        transactionRepository.save(payerTransaction);
        return "Payment successful!";
    }
}

