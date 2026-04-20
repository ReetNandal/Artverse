package com.artgallery.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="wallet_id", nullable=false)
    private Long walletId;

    @Column(nullable=false)
    private String type; // DEPOSIT/SPEND/WITHDRAW/REFUND

    @Column(nullable=false)
    private BigDecimal amount;

    @Column(nullable=false)
    private String status; // COMPLETED/PENDING/FAILED

    @Column(nullable=false)
    private String description;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public Long getWalletId() { return walletId; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }
    public void setType(String type) { this.type = type; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}