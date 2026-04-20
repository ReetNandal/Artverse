package com.artgallery.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ match DB users.id INT
    @Column(name="user_id", nullable=false, unique=true)
    private Integer userId;

    @Column(nullable=false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "held_balance", nullable = false)
    private BigDecimal heldBalance = BigDecimal.ZERO;

    public BigDecimal getHeldBalance() {
        return heldBalance == null ? BigDecimal.ZERO : heldBalance;
    }

    public void setHeldBalance(BigDecimal heldBalance) {
        this.heldBalance = heldBalance;
    }

    @Transient
    public BigDecimal getAvailableBalance() {
        BigDecimal bal = balance == null ? BigDecimal.ZERO : balance;
        BigDecimal held = heldBalance == null ? BigDecimal.ZERO : heldBalance;
        return bal.subtract(held);
    }
    public Long getId() { return id; }
    public Integer getUserId() { return userId; }
    public BigDecimal getBalance() { return balance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}