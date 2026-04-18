package com.artgallery.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "collections")
public class CollectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // keep it simple: use raw IDs so it matches your existing style (Auction has artworkId, winnerId)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "artwork_id")
    private Long artworkId;

    @Column(name = "auction_id")
    private Long auctionId;

    @Column(name = "purchase_price")
    private Double purchasePrice;

    @Column(name = "won_date")
    private LocalDateTime wonDate;

    @Column(name = "certificate_url")
    private String certificateUrl;

    @Column(name = "shipping_status")
    private String shippingStatus;

    @Column(name = "acquired_at")
    private LocalDateTime acquiredAt;

    @Column(name = "acquisition_type")
    private String acquisitionType; // AUCTION_WIN / DIRECT_OWN / DIRECT_BUY

    @Column(name = "price_paid")
    private Double pricePaid;

    @Transient
    private Artwork artwork; // populated in service for UI cards

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getArtworkId() { return artworkId; }
    public Long getAuctionId() { return auctionId; }
    public Double getPurchasePrice() { return purchasePrice; }
    public LocalDateTime getWonDate() { return wonDate; }
    public String getCertificateUrl() { return certificateUrl; }
    public String getShippingStatus() { return shippingStatus; }
    public LocalDateTime getAcquiredAt() { return acquiredAt; }
    public String getAcquisitionType() { return acquisitionType; }
    public Double getPricePaid() { return pricePaid; }
    public Artwork getArtwork() { return artwork; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setArtworkId(Long artworkId) { this.artworkId = artworkId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }
    public void setPurchasePrice(Double purchasePrice) { this.purchasePrice = purchasePrice; }
    public void setWonDate(LocalDateTime wonDate) { this.wonDate = wonDate; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }
    public void setShippingStatus(String shippingStatus) { this.shippingStatus = shippingStatus; }
    public void setAcquiredAt(LocalDateTime acquiredAt) { this.acquiredAt = acquiredAt; }
    public void setAcquisitionType(String acquisitionType) { this.acquisitionType = acquisitionType; }
    public void setPricePaid(Double pricePaid) { this.pricePaid = pricePaid; }
    public void setArtwork(Artwork artwork) { this.artwork = artwork; }
}