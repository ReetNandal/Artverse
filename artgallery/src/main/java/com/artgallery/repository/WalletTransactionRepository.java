package com.artgallery.repository;

import com.artgallery.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findTop10ByWalletIdOrderByCreatedAtDesc(Long walletId);
}