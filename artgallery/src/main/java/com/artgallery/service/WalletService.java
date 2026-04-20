package com.artgallery.service;

import com.artgallery.model.Wallet;
import com.artgallery.model.WalletTransaction;
import com.artgallery.repository.WalletRepository;
import com.artgallery.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository txnRepository;

    public WalletService(WalletRepository walletRepository, WalletTransactionRepository txnRepository) {
        this.walletRepository = walletRepository;
        this.txnRepository = txnRepository;
    }

    public Wallet getOrCreateWallet(Integer userId) {
        return walletRepository.findByUserId(userId).orElseGet(() -> {
            Wallet w = new Wallet();
            w.setUserId(userId);
            w.setBalance(BigDecimal.ZERO);
            w.setCreatedAt(LocalDateTime.now());
            w.setUpdatedAt(LocalDateTime.now());
            return walletRepository.save(w);
        });
    }

    public List<WalletTransaction> getRecentTransactions(Long walletId) {
        return txnRepository.findTop10ByWalletIdOrderByCreatedAtDesc(walletId);
    }

    public BigDecimal getTotalAdded(Long walletId) {
        return txnRepository.findTop10ByWalletIdOrderByCreatedAtDesc(walletId).stream()
                .filter(t -> "DEPOSIT".equalsIgnoreCase(t.getType()))
                .map(WalletTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalSpent(Long walletId) {
        return txnRepository.findTop10ByWalletIdOrderByCreatedAtDesc(walletId).stream()
                .filter(t -> "SPEND".equalsIgnoreCase(t.getType()))
                .map(WalletTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void deposit(Integer userId, BigDecimal amount) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (amount == null || amount.compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException("Amount must be at least 1");
        }

        Wallet wallet = getOrCreateWallet(userId);

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        WalletTransaction txn = new WalletTransaction();
        txn.setWalletId(wallet.getId());
        txn.setType("DEPOSIT");
        txn.setAmount(amount);
        txn.setStatus("COMPLETED");
        txn.setDescription("Wallet top-up");
        txn.setCreatedAt(LocalDateTime.now());
        txnRepository.save(txn);
    }
    public void holdAmount(Integer userId, BigDecimal amount) {
        Wallet wallet = getOrCreateWallet(userId);

        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient available balance");
        }

        wallet.setHeldBalance(wallet.getHeldBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
    }

    public void releaseAmount(Integer userId, BigDecimal amount) {
        Wallet wallet = getOrCreateWallet(userId);

        BigDecimal newHeld = wallet.getHeldBalance().subtract(amount);
        if (newHeld.compareTo(BigDecimal.ZERO) < 0) {
            newHeld = BigDecimal.ZERO;
        }

        wallet.setHeldBalance(newHeld);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
    }

    public BigDecimal getAvailableBalance(Integer userId) {
        return getOrCreateWallet(userId).getAvailableBalance();
    }
}