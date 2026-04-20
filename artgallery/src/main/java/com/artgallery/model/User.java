package com.artgallery.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive;

    // ---------------- BOT FIELDS ----------------
    @Column(name = "is_bot")
    private Boolean isBot = false;

    @Column(name = "bot_active")
    private Boolean botActive = false;

    @Column(name = "bot_max_bid_limit")
    private Double botMaxBidLimit;
    // --------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    public Boolean getIsBot() { return isBot; }
    public void setIsBot(Boolean bot) { isBot = bot; }

    public Boolean getBotActive() { return botActive; }
    public void setBotActive(Boolean botActive) { this.botActive = botActive; }

    public Double getBotMaxBidLimit() { return botMaxBidLimit; }
    public void setBotMaxBidLimit(Double botMaxBidLimit) { this.botMaxBidLimit = botMaxBidLimit; }
}